/*
 * Copyright 2006-2007 Luca Garulli (luca.garulli--at--assetdata.it)
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

package org.romaframework.module.users.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.romaframework.aspect.authentication.AuthenticationAspect;
import org.romaframework.aspect.core.annotation.AnnotationConstants;
import org.romaframework.aspect.session.SessionAccount;
import org.romaframework.aspect.view.annotation.ViewField;
import org.romaframework.core.Roma;
import org.romaframework.module.admin.InfoHelper;
import org.romaframework.module.admin.domain.CustomizableEntry;
import org.romaframework.module.admin.domain.Info;
import org.romaframework.module.admin.domain.Realm;
import org.romaframework.module.users.UsersInfoConstants;

/**
 * Class that represents an account
 * 
 * @author Luca Garulli (luca.garulli--at--assetdata.it)
 */
public class BaseAccount extends AbstractAccount implements SessionAccount, Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -2197776437029602641L;

	private static final String	LAST_PASSWORD_UPDATE_TIME	= "password.last_password_update";

	protected BaseProfile				profile;
	protected Date							signedOn;
	protected Date							lastModified;

	@ViewField(visible = AnnotationConstants.FALSE)
	protected String						userIdentification;

	protected String						notes;
	protected Info							status;

	protected Boolean						changePasswordNextLogin;
	protected String						email;
	protected Set<BaseGroup>		groups;

	@ViewField(render = "password")
	protected String						password;

	public BaseAccount() {
	}

	public BaseAccount(Realm iRealm) {
		realm = iRealm;
	}

	public BaseAccount(String name, String password, BaseProfile iProfile) {
		this(name, password, iProfile, InfoHelper.getInstance().getInfo(UsersInfoConstants.ACCOUNT_CATEGORY_NAME,
				UsersInfoConstants.STATUS_ACTIVE));
	}

	public BaseAccount(String name, String password, BaseProfile iProfile, Info iStatus) {
		this.name = name;
		setPassword(password);
		status = iStatus;
		profile = iProfile;
		signedOn = new Date();
		lastModified = signedOn;
	}

	public BaseAccount(Realm iRealm, String name, String password, BaseProfile iProfile) {
		this(name, password, iProfile);
		realm = iRealm;
	}

	public BaseAccount(Realm iRealm, String name, String password, BaseProfile iProfile, Info iStatus) {
		this(name, password, iProfile, iStatus);
		realm = iRealm;
	}

	@Override
	public String toString() {
		return name;
	}

	public BaseProfile getProfile() {
		return profile;
	}

	public void setProfile(Object iProfile) {
		profile = (BaseProfile) iProfile;
	}

	public void setProfile(BaseProfile iProfile) {
		profile = iProfile;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String iPassword) {
		if (iPassword != null && iPassword.equals(password))
			// you are not changing password
			return;

		try {
			password = Roma.aspect(AuthenticationAspect.class).encryptPassword(iPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	@ViewField(visible = AnnotationConstants.FALSE)
	public Date getLastModifiedPassword() {
		CustomizableEntry entry = getConfiguration(LAST_PASSWORD_UPDATE_TIME);
		if (entry != null) {
			return entry.getDateTimeValue();
		}
		return null;
	}

	public void setLastModifiedPassword(Date date) {
		CustomizableEntry entry = getConfiguration(LAST_PASSWORD_UPDATE_TIME);
		if (entry != null) {
			entry.setDateTimeValue(date);
		} else {
			entry = new CustomizableEntry(LAST_PASSWORD_UPDATE_TIME, CustomizableEntry.TYPE_DATETIME, null);
			entry.setDateTimeValue(date);
			getConfiguration().put(LAST_PASSWORD_UPDATE_TIME, entry);
		}
	}

	public Date getSignedOn() {
		return signedOn;
	}

	public void setSignedOn(Date signedOn) {
		this.signedOn = signedOn;
	}

	public Info getStatus() {
		return status;
	}

	public void setStatus(Info status) {
		this.status = status;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Boolean isChangePasswordNextLogin() {
		return changePasswordNextLogin;
	}

	public void setChangePasswordNextLogin(Boolean changePasswordAtNextLogin) {
		changePasswordNextLogin = changePasswordAtNextLogin;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@ViewField(visible = AnnotationConstants.FALSE)
	public BaseGroup getDefaultGroup() {
		if (groups != null && groups.size() == 1)
			return groups.iterator().next();
		return null;
	}

	/**
	 * @return the groups
	 */
	public Set<BaseGroup> getGroups() {
		return groups;
	}

	/**
	 * @param groups
	 *          the groups to set
	 */
	public void setGroups(Set<BaseGroup> groups) {
		this.groups = groups;
	}

	public boolean addGroup(BaseGroup iGroup) {
		if (groups.contains(iGroup))
			return false;

		groups.add(iGroup);
		return true;
	}

	public String getUserIdentification() {
		return userIdentification;
	}

	public void setUserIdentification(String userIdentification) {
		this.userIdentification = userIdentification;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((realm == null) ? 0 : realm.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseAccount other = (BaseAccount) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (realm == null) {
			if (other.realm != null)
				return false;
		} else if (!realm.equals(other.realm))
			return false;
		return true;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
}
