/*
 * Copyright 2006 Luca Garulli (luca.garulli--at--assetdata.it)
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

package org.romaframework.module.users.install;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;

import org.romaframework.aspect.persistence.PersistenceAspect;
import org.romaframework.core.Roma;
import org.romaframework.module.admin.InfoHelper;
import org.romaframework.module.admin.domain.Info;
import org.romaframework.module.admin.domain.InfoCategory;
import org.romaframework.module.admin.domain.Realm;
import org.romaframework.module.admin.install.AdminApplicationInstaller;
import org.romaframework.module.users.ActivityLogCategories;
import org.romaframework.module.users.UsersAuthentication;
import org.romaframework.module.users.UsersHelper;
import org.romaframework.module.users.UsersInfoConstants;
import org.romaframework.module.users.domain.ActivityLog;
import org.romaframework.module.users.domain.BaseAccount;
import org.romaframework.module.users.domain.BaseFunction;
import org.romaframework.module.users.domain.BaseProfile;

public class UsersApplicationInstaller extends AdminApplicationInstaller {

  public static final String PROFILE_ADMINISTRATOR = "Administrator";
  public static final String PROFILE_BASIC = "Basic";
  public static final String ACCOUNT_ADMIN         = "admin";
  public static final String ACCOUNT_USER         = "user";
  public static final String ACCOUNT_SEPARATOR     = ".";

  protected Realm            realm;
  protected BaseProfile      pAnonymous;
  protected BaseProfile      pAdmin;
  protected BaseProfile      pBasic;
  protected Info             defStatus;

  @Override
  public synchronized boolean install() {
    if (!super.install())
      return false;

    PersistenceAspect db = Roma.context().persistence();

    InfoCategory accountCategory = InfoHelper.getInstance().getInfoCategory(realm, UsersInfoConstants.ACCOUNT_CATEGORY_NAME);

    if (accountCategory != null)
      return false;

    createInfos(db);
    createProfiles();
    try {
      createAccounts();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    return true;
  }

  @Override
  public synchronized boolean install(Object obj) {
    realm = (Realm) obj;
    return install();
  }

  protected void createInfos(PersistenceAspect db) {
    InfoCategory cat = InfoHelper.getInstance().setInfoCategory(realm, UsersInfoConstants.ACCOUNT_CATEGORY_NAME);
    defStatus = InfoHelper.getInstance().setInfo(cat, UsersInfoConstants.STATUS_ACTIVE);
    InfoHelper.getInstance().setInfo(cat, UsersInfoConstants.STATUS_UNACTIVE);
    InfoHelper.getInstance().setInfo(cat, UsersInfoConstants.STATUS_SUSPENDED);

    InfoHelper.getInstance().setInfoCategory(realm, ActivityLog.LOG_CATEGORY_NAME);
    InfoHelper.getInstance().setInfo(realm, ActivityLog.LOG_CATEGORY_NAME, ActivityLogCategories.CATEGORY_SYSTEM);
    InfoHelper.getInstance().setInfo(realm, ActivityLog.LOG_CATEGORY_NAME, ActivityLogCategories.CATEGORY_LOGIN);
    InfoHelper.getInstance().setInfo(realm, ActivityLog.LOG_CATEGORY_NAME, ActivityLogCategories.CATEGORY_ADMIN);
  }

  protected void createAccounts() throws NoSuchAlgorithmException {
    BaseAccount aAdmin = new BaseAccount(realm);
    if (realm == null) {
      aAdmin.setName(ACCOUNT_ADMIN);
      aAdmin.setPassword(ACCOUNT_ADMIN);
    } else {
      aAdmin.setName(ACCOUNT_ADMIN + ACCOUNT_SEPARATOR + realm);
      aAdmin.setPassword(ACCOUNT_ADMIN + ACCOUNT_SEPARATOR + realm);
    }
    aAdmin.setSignedOn(new Date());
    aAdmin.setStatus(defStatus);
    aAdmin.setLastModified(aAdmin.getSignedOn());
    aAdmin.setProfile(pAdmin);

    UsersHelper.getInstance().setAccount(aAdmin);
    BaseAccount uUser = new BaseAccount(realm);
    if (realm == null) {
      uUser.setName(ACCOUNT_USER);
      uUser.setPassword(ACCOUNT_USER);
    } else {
      uUser.setName(ACCOUNT_USER + ACCOUNT_SEPARATOR + realm);
      uUser.setPassword(ACCOUNT_USER + ACCOUNT_SEPARATOR + realm);
    }
    uUser.setSignedOn(new Date());
    uUser.setStatus(defStatus);
    uUser.setLastModified(uUser.getSignedOn());
    uUser.setProfile(pBasic);

    UsersHelper.getInstance().setAccount(uUser);
  }

  protected void createProfiles() {
    pAnonymous = new BaseProfile(realm);
    pAnonymous.setName(UsersAuthentication.ANONYMOUS_PROFILE_NAME);
    pAnonymous.setHomePage("HomePage");
    pAnonymous.setFunctions(new HashMap<String, BaseFunction>());
    pAnonymous.setMode(BaseProfile.MODE_ALLOW_ALL_BUT);
    UsersHelper.getInstance().setProfile(pAnonymous);

    pAdmin = new BaseProfile();
    if (realm == null) {
      pAdmin.setName(PROFILE_ADMINISTRATOR);
    } else {
      pAdmin.setName(PROFILE_ADMINISTRATOR + ACCOUNT_SEPARATOR + realm);
    }
    pAdmin.setHomePage("HomePageAdmin");
    pAdmin.setMode(BaseProfile.MODE_ALLOW_ALL_BUT);
    UsersHelper.getInstance().setProfile(pAdmin);
    
    pBasic = new BaseProfile();
    if (realm == null) {
    	pBasic.setName(PROFILE_BASIC);
    } else {
    	pBasic.setName(PROFILE_BASIC + ACCOUNT_SEPARATOR + realm);
    }
    pBasic.setHomePage("HomePage");
    pBasic.setMode(BaseProfile.MODE_ALLOW_ALL_BUT);
    UsersHelper.getInstance().setProfile(pBasic);
  }

  /**
   * @return the realm
   */
  public Realm getRealm() {
    return realm;
  }

  /**
   * @param realm
   *          the realm to set
   */
  public void setRealm(Realm realm) {
    this.realm = realm;
  }
}
