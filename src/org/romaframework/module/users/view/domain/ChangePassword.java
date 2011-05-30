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
package org.romaframework.module.users.view.domain;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.romaframework.aspect.authentication.AuthenticationAspect;
import org.romaframework.aspect.core.annotation.CoreClass;
import org.romaframework.aspect.flow.FlowAspect;
import org.romaframework.aspect.persistence.PersistenceAspect;
import org.romaframework.aspect.view.ViewConstants;
import org.romaframework.aspect.view.annotation.ViewField;
import org.romaframework.core.Roma;
import org.romaframework.core.flow.ObjectContext;
import org.romaframework.frontend.RomaFrontend;
import org.romaframework.frontend.domain.message.MessageOk;
import org.romaframework.frontend.domain.message.MessageResponseListener;
import org.romaframework.frontend.domain.page.Page;
import org.romaframework.module.users.UsersModule;
import org.romaframework.module.users.domain.BaseAccount;
import org.romaframework.module.users.repository.BaseAccountRepository;

/**
 * Change password simple form.
 * 
 * @author Luca Garulli (luca.garulli--at--assetdata.it)
 */
@CoreClass(orderFields = "message oldPassword password confirmPassword", orderActions = "change cancel")
public class ChangePassword extends Page {

	@ViewField(render = "password")
	private String									oldPassword;

	@ViewField(render = "password")
	private String									password;

	@ViewField(render = "password")
	private String									confirmPassword;

	private MessageResponseListener	listener;

	private BaseAccount							account;

	public ChangePassword(BaseAccount iAccount, MessageResponseListener iListener) {
		account = iAccount;
		listener = iListener;
	}

	public void cancel() {
		back();
	}

	/**
	 * Change the password, reset the flag
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	public void change() throws NoSuchAlgorithmException {
		if (password == null && confirmPassword == null)
			return;

		if (password == null || confirmPassword == null || !password.equals(confirmPassword)) {
			Roma.aspect(FlowAspect.class).forward(new MessageOk("error", "", null, "$ChangePassword.change.error"));
			return;
		}

		String cypherOldPassword = null;
		if (oldPassword != null)
			cypherOldPassword = Roma.aspect(AuthenticationAspect.class).encryptPassword(oldPassword);
		if (cypherOldPassword == null || !cypherOldPassword.equals(account.getPassword())) {
			RomaFrontend.flow().forward(new MessageOk("error", "", null, "$ChangePassword.oldPassword.error"), "screen:popup");
			return;
		}
		if (!AccountManagementUtility.isPasswordMathedRegExpression(password)) {
			RomaFrontend.flow().forward(new MessageOk("error", "", null, "$ChangePassword.invalidPassword.error"), "screen:popup");
			return;
		}
		if (!AccountManagementUtility.isPasswordUnused(account, password)) {
			RomaFrontend.flow().forward(new MessageOk("error", "", null, "$ChangePassword.alreadyUsed.error"), "screen:popup");
			return;
		}

		// UPDATE THE OBJECT PERSISTENTLY
		account.setPassword(password);
		account.setChangePasswordNextLogin(false);
		account.setLastModified(new Date());
		account.setLastModifiedPassword(new Date());
		account = Roma.component(BaseAccountRepository.class).update(account, PersistenceAspect.STRATEGY_DETACHING);
		Roma.session().getActiveSessionInfo().setAccount(account);

		String mess = "$ChangePassword.message.feature";
		Integer passwordPeriod = ObjectContext.getInstance().getComponent(UsersModule.class).getPasswordPeriod();
		Integer accountPeriod = ObjectContext.getInstance().getComponent(UsersModule.class).getAccountPeriod();
		if (passwordPeriod != null) {
			mess += Roma.i18n().resolveString(ChangePassword.class, "message.password", passwordPeriod);
		}
		if (accountPeriod != null) {
			Integer scadenza = (Math.round((account.getLastModified().getTime()) / AccountManagementUtility.DAY_MILLISECONDS) + accountPeriod)
					- Math.round((new Date()).getTime() / AccountManagementUtility.DAY_MILLISECONDS);
			mess += Roma.i18n().resolveString(ChangePassword.class, "message.account", scadenza);
		}
		Roma.aspect(FlowAspect.class).forward(new MessageOk("CHANGE PASSWORD", "", null, mess), "screen:popup");
		back();
		// WAKE UP LISTENER
		if (listener != null)
			listener.responseMessage(null, Boolean.TRUE);
	}

	@ViewField(render = ViewConstants.RENDER_LABEL)
	public String getMessage() {
		return Roma.i18n().getString("ChangePassword.message.text");
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String userPassword) {
		password = userPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
}
