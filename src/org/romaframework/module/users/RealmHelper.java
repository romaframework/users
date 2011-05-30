package org.romaframework.module.users;

import java.util.List;

import org.romaframework.aspect.authentication.AuthenticationAspect;
import org.romaframework.aspect.persistence.PersistenceAspect;
import org.romaframework.aspect.persistence.QueryByFilter;
import org.romaframework.core.Roma;
import org.romaframework.module.users.domain.Realm;

public class RealmHelper {

	public static final String ATTR_REALM_NAME = "_REALMNAME_";

	public static Realm getCurrentRealm() {
		return (Realm) Roma.aspect(AuthenticationAspect.class).getCurrentRealm();
	}

	public static List<Realm> getRealms() {
		PersistenceAspect db = Roma.context().persistence();
		QueryByFilter filter = new QueryByFilter(Realm.class);
		filter.addOrder("name", QueryByFilter.ORDER_ASC);
		filter.setStrategy(PersistenceAspect.STRATEGY_DETACHING);
		return db.query(filter);
	}

	public static Realm getRealm(String iName) {
		PersistenceAspect db = Roma.context().persistence();
		QueryByFilter filter = new QueryByFilter(Realm.class);
		filter.addItem("name", QueryByFilter.FIELD_EQUALS, iName);
		filter.setStrategy(PersistenceAspect.STRATEGY_DETACHING);
		return db.queryOne(filter);
	}
}
