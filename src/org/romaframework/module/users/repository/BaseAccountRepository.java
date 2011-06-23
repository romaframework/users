package org.romaframework.module.users.repository;

import java.util.ArrayList;
import java.util.List;

import org.romaframework.aspect.persistence.PersistenceAspect;
import org.romaframework.aspect.persistence.QueryByFilter;
import org.romaframework.aspect.session.SessionInfo;
import org.romaframework.core.Roma;
import org.romaframework.core.repository.PersistenceAspectRepository;
import org.romaframework.frontend.RomaFrontend;
import org.romaframework.module.users.RealmHelper;
import org.romaframework.module.users.UsersInfoConstants;
import org.romaframework.module.users.domain.ActivityLog;
import org.romaframework.module.users.domain.BaseAccount;
import org.romaframework.module.users.domain.BaseAccountStatus;
import org.romaframework.module.users.domain.BaseGroup;
import org.romaframework.module.users.domain.Realm;

/**
 * Repository class for BaseGroup entity. By default it extends the PersistenceAspectRepository class that delegates the execution
 * of all commands to the PersistenceAspect. <br/>
 * <br/>
 * This class was generated by Roma Meta Framework CRUD wizard.
 */
public class BaseAccountRepository extends PersistenceAspectRepository<BaseAccount> {
	public List<BaseAccount> findByRealm(Realm iRealm) {
		QueryByFilter query = new QueryByFilter(BaseAccount.class);
		query.addItem("realm", QueryByFilter.FIELD_EQUALS, iRealm);

		return findByCriteria(query);
	}

	public BaseAccount findByName(String iName) {
		return findByName(Roma.context().persistence(), iName);
	}

	public BaseAccount findByName(PersistenceAspect db, String iName) {
		QueryByFilter query = new QueryByFilter(BaseAccount.class);
		query.addItem("name", QueryByFilter.FIELD_EQUALS, iName);
		query.addItem("realm", QueryByFilter.FIELD_EQUALS, RealmHelper.getCurrentRealm());
		query.setMode(PersistenceAspect.FULL_MODE_LOADING);
		query.setStrategy(PersistenceAspect.STRATEGY_DETACHING);

		return findFirstByCriteria(db, query);
	}

	public List<BaseAccount> findOfActiveAccountsOfGroups(BaseAccount iAccount) {
		QueryByFilter query = new QueryByFilter(BaseAccount.class, QueryByFilter.PREDICATE_OR);
		query.setStrategy(PersistenceAspect.STRATEGY_DETACHING);

		for (BaseGroup group : iAccount.getGroups())
			query.addItem("groups", QueryByFilter.FIELD_CONTAINS, group);

		query.addOrder("name", QueryByFilter.ORDER_ASC);
		query.addOrder("realm.name", QueryByFilter.ORDER_ASC);

		List<BaseAccount> queryResult = Roma.context().persistence().query(query);

		List<BaseAccount> result = filterActiveAccounts(queryResult);

		return result;
	}

	private List<BaseAccount> filterActiveAccounts(List<BaseAccount> queryResult) {
		QueryByFilter acc = new QueryByFilter(BaseAccountStatus.class);
		acc.addItem("name", QueryByFilter.FIELD_EQUALS, UsersInfoConstants.ACCOUNT_CATEGORY_NAME);
		acc.setStrategy(PersistenceAspect.STRATEGY_DETACHING);
		BaseAccountStatus active = Roma.context().persistence().queryOne(acc);

		List<BaseAccount> result = new ArrayList<BaseAccount>();
		for (BaseAccount a : queryResult)
			if (a.getStatus().equals(active))
				result.add(a);
		return result;
	}

	@Override
	public BaseAccount update(BaseAccount object) {
		BaseAccount account = super.update(object);
		SessionInfo sess = RomaFrontend.session().getActiveSessionInfo();
		if (sess != null && sess.getAccount() != null && sess.getAccount().equals(account))
			// I'M UPDATING THE CURRENT ACCOUNT: REPLACE IT WITH THE NEW ONE
			sess.setAccount(account);

		return account;
	}

	@Override
	public void delete(Object[] iObjects) {
		for (Object o : iObjects)
			delete((BaseAccount) o);
	}

	@Override
	public void delete(BaseAccount object) {
		if (Roma.existComponent(ActivityLogRepository.class)) {
			// DELETE ALL THE LOGS BEFORE
			List<ActivityLog> logs = Roma.component(ActivityLogRepository.class).findByAccount(object);
			Roma.component(ActivityLogRepository.class).delete(logs.toArray());
		}

		super.delete(object);
	}

}
