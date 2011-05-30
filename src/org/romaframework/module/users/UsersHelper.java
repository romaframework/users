/*
 *
 * Copyright 2007 Luca Molino (luca.molino--AT--assetdata.it)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.romaframework.module.users;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.romaframework.aspect.persistence.PersistenceAspect;
import org.romaframework.aspect.persistence.QueryByFilter;
import org.romaframework.core.Roma;
import org.romaframework.module.admin.InfoHelper;
import org.romaframework.module.admin.RealmHelper;
import org.romaframework.module.admin.domain.Info;
import org.romaframework.module.admin.domain.Realm;
import org.romaframework.module.users.domain.BaseAccount;
import org.romaframework.module.users.domain.BaseProfile;
import org.romaframework.module.users.install.UsersApplicationInstaller;

/**
 * @author l.molino
 */
public class UsersHelper {

	private static UsersHelper	instance	= new UsersHelper();

	public UsersHelper() {
	}

	public List<BaseProfile> getProfileList() {
		return getProfileList(RealmHelper.getCurrentRealm());
	}

	/**
	 * Method that retrieve all BaseProfile known in specified Realm
	 * 
	 * @param realm
	 *          The realm in which search profiles
	 * @return A List containing all BaseProfile known in the specified Realm, an empty List if the specified realm doesn't exists, or
	 *         a List containing all BaseProfile if specified realm==null
	 */
	public List<BaseProfile> getProfileList(Realm iRealm) {
		QueryByFilter query = new QueryByFilter(BaseProfile.class, null);
		query.setStrategy(PersistenceAspect.STRATEGY_DETACHING);
		if (iRealm != null) {
			query.addItem("realm", QueryByFilter.FIELD_EQUALS, iRealm);
		}
		return Roma.context().persistence().query(query);
	}

	public BaseProfile[] getProfileArray() {
		return getProfileArray(RealmHelper.getCurrentRealm());
	}

	public BaseProfile[] getProfileArray(Realm iRealm) {
		List<BaseProfile> profiles = getProfileList(iRealm);
		BaseProfile[] profileArray = new BaseProfile[profiles.size()];
		if (profiles.size() > 0) {
			profiles.toArray(profileArray);
		}
		return profileArray;
	}

	public BaseProfile getProfile(String iName) {
		return getProfile(iName, RealmHelper.getCurrentRealm());
	}

	public BaseProfile getProfile(String iName, Realm iRealm) {
		QueryByFilter query = new QueryByFilter(BaseProfile.class);
		query.setStrategy(PersistenceAspect.STRATEGY_DETACHING);
		if (iRealm != null) {
			query.addItem("realm", QueryByFilter.FIELD_EQUALS, iRealm);
		}
		if (iName != null) {
			query.addItem("name", QueryByFilter.FIELD_EQUALS, iName);
		}
		return Roma.context().persistence().queryOne(query);
	}

	private BaseProfile setProfile(Realm iRealm, String iName, byte iMode, String homePage) {
		if (homePage != null) {
			BaseProfile profile = new BaseProfile(iRealm, iName, null, iMode, homePage);
			return setProfile(profile);
		} else
			return setProfile(iRealm, iName, iMode);
	}

	public BaseProfile setProfile(Realm iRealm, String iName) {
		return setProfile(iRealm, iName, BaseProfile.MODE_ALLOW_ALL_BUT);
	}

	public BaseProfile setProfile(Realm iRealm, String iName, byte iMode) {
		BaseProfile profile = new BaseProfile(iRealm, iName, null, iMode, "HomePage");
		return setProfile(profile);
	}

	public BaseProfile setProfile(BaseProfile iProfile) {
		List<BaseProfile> profiles = getProfileList(iProfile.getRealm());
		if (!profiles.contains(iProfile)) {
			iProfile = Roma.context().persistence().createObject(iProfile);
		}
		return iProfile;
	}

	public List<BaseAccount> getAccountList(String iProfileName) {
		return getAccountList(iProfileName, RealmHelper.getCurrentRealm());
	}

	public List<BaseAccount> getAccountList(String iProfileName, Realm iRealm) {
		return getAccountList(getProfile(iProfileName, iRealm));
	}

	public List<BaseAccount> getAccountList(BaseProfile iProfile) {
		List<BaseAccount> result;
		QueryByFilter filter = new QueryByFilter(BaseAccount.class);
		filter.setStrategy(PersistenceAspect.STRATEGY_DETACHING);
		if (iProfile != null) {
			filter.addItem("profile", QueryByFilter.FIELD_EQUALS, iProfile);
		}
		result = Roma.context().persistence().query(filter);
		return result;
	}

	public BaseAccount getAccount(String iProfileName, String iName) {
		return getAccount(iProfileName, iName, RealmHelper.getCurrentRealm());
	}

	public BaseAccount getAccount(String iProfileName, String iName, Realm iRealm) {
		return getAccount(getProfile(iProfileName, iRealm), iName);
	}

	public BaseAccount getAccount(BaseProfile iProfile, String iName) {
		QueryByFilter filter = new QueryByFilter(BaseAccount.class);
		filter.setStrategy(PersistenceAspect.STRATEGY_DETACHING);
		if (iProfile != null) {
			filter.addItem("profile", QueryByFilter.FIELD_EQUALS, iProfile);
		}
		filter.addItem("name", QueryByFilter.FIELD_EQUALS, iName);
		return Roma.context().persistence().queryOne(filter);
	}

	public BaseAccount setAccount(Realm iRealm, String iProfileName, String iName, String iPassword) throws NoSuchAlgorithmException {
		return setAccount(getProfile(iProfileName, iRealm), iName, iPassword, null);
	}

	public BaseAccount setAccount(BaseProfile iProfile, String iName, String iPassword) throws NoSuchAlgorithmException {
		return setAccount(iProfile, iName, iPassword, null);
	}

	public BaseAccount setAccount(Realm iRealm, String iProfileName, String iName, String iPassword, Info iStatus)
			throws NoSuchAlgorithmException {
		return setAccount(getProfile(iProfileName, iRealm), iName, iPassword, iStatus);
	}

	public BaseAccount setAccount(BaseProfile iProfile, String iName, String iPassword, Info iStatus) throws NoSuchAlgorithmException {
		if (iStatus == null)
			iStatus = InfoHelper.getInstance().getInfo(RealmHelper.getCurrentRealm(), UsersInfoConstants.ACCOUNT_CATEGORY_NAME,
					UsersInfoConstants.STATUS_ACTIVE);
		BaseAccount iAccount = new BaseAccount(iProfile.getRealm());
		iAccount.setName(iName);
		iAccount.setPassword(iPassword);
		iAccount.setProfile(iProfile);
		iAccount.setStatus(iStatus);
		return storeAccount(iAccount);
	}

	public BaseAccount setAccount(BaseAccount iAccount) {
		return storeAccount(iAccount);
	}

	private BaseAccount storeAccount(BaseAccount iAccount) {
		List<BaseAccount> result = getAccountList(iAccount.getProfile());
		if (!result.contains(iAccount)) {
			iAccount = Roma.context().persistence().createObject(iAccount);
		}
		return iAccount;
	}

	public Realm createRealm(String iName) throws NoSuchAlgorithmException {
		return createRealm(iName, "HomePage");
	}

	public Realm createRealm(String iName, String homePage) throws NoSuchAlgorithmException {
		Realm realm = new Realm(iName);
		BaseProfile adminProfile = UsersHelper.getInstance().setProfile(realm, UsersApplicationInstaller.PROFILE_ADMINISTRATOR,
				BaseProfile.MODE_ALLOW_ALL_BUT, homePage);
		BaseAccount adminAccount = UsersHelper.getInstance().setAccount(adminProfile, UsersApplicationInstaller.ACCOUNT_ADMIN,
				UsersApplicationInstaller.ACCOUNT_ADMIN);
		Roma.context().persistence().createObject(adminAccount);
		return Roma.context().persistence().createObject(realm);
	}

	public static UsersHelper getInstance() {
		return instance;
	}
}
