package org.romaframework.module.users.view.domain;

import org.romaframework.aspect.core.annotation.CoreClass;
import org.romaframework.aspect.view.ViewCallback;
import org.romaframework.aspect.view.annotation.ViewClass;
import org.romaframework.aspect.view.feature.ViewFieldFeatures;
import org.romaframework.core.Roma;
import org.romaframework.module.users.RealmHelper;
import org.romaframework.module.users.domain.Realm;

@CoreClass(orderFields = "realmName userName userPassword")
@ViewClass(label = "Login", render = "popup", layout = "popup")
public class RealmLogin extends Login implements ViewCallback {
	public static final String	SESSION_REALM_PARAM	= "_REALMNAME_";
	protected Realm							realm;
	protected String						realmName;

	public RealmLogin() {
	}

	public RealmLogin(Realm realm) {
		this.realm = realm;
	}

	public RealmLogin(Realm realm, String iUserName) {
		this(realm);
		setUserName(iUserName);
	}

	public RealmLogin(Realm realm, String iUserName, String iUserPassword) {
		this(realm, iUserName);
		setUserPassword(iUserPassword);
	}

	public void onShow() {
		if (realm == null)
			realm = (Realm) Roma.session().getProperty(SESSION_REALM_PARAM);

		realmName = realm != null ? realm.getName() : "";
		Roma.fieldChanged(this, "realmName");
		Roma.setFeature(this,"realmName", ViewFieldFeatures.ENABLED, realm == null);
	}

	@Override
	public void login() {
		if (realmName != null && realmName.length() > 0)
			realm = RealmHelper.getRealm(realmName);

		super.login(realm);
	}

	public String getRealmName() {
		return realmName;
	}

	public void setRealmName(String realmName) {
		this.realmName = realmName;
	}

	public void onDispose() {
	}
}
