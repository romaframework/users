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

package org.romaframework.module.users.view.domain.menu;

import org.romaframework.aspect.authentication.AuthenticationAspect;
import org.romaframework.aspect.core.annotation.CoreClass;
import org.romaframework.aspect.flow.FlowAspect;
import org.romaframework.aspect.flow.annotation.FlowAction;
import org.romaframework.aspect.view.ViewCallback;
import org.romaframework.core.Roma;
import org.romaframework.frontend.domain.message.MessageOk;
import org.romaframework.module.admin.domain.Realm;
import org.romaframework.module.admin.view.domain.menu.AbstractMenuPanel;
import org.romaframework.module.users.domain.BaseAccount;
import org.romaframework.module.users.view.domain.activitylog.ActivityLogMain;
import org.romaframework.module.users.view.domain.baseaccount.BaseAccountMain;
import org.romaframework.module.users.view.domain.basegroup.BaseGroupMain;
import org.romaframework.module.users.view.domain.baseprofile.BaseProfileMain;
import org.romaframework.module.users.view.domain.configuration.ConfigurationBaseAccount;
import org.romaframework.module.users.view.domain.realm.RealmAdminMain;

@CoreClass(orderActions = "accounts profiles groups activityLogs realms")
public class UsersPanel extends AbstractMenuPanel implements ViewCallback {

	private boolean	disableRealmAccountCreation;

	@FlowAction(next = BaseAccountMain.class, position = "screen://body")
	public void accounts() {
	}

	@FlowAction(next = BaseProfileMain.class, position = "screen://body")
	public void profiles() {
	}

	@FlowAction(next = BaseGroupMain.class, position = "screen://body")
	public void groups() {
	}

	@FlowAction(next = ConfigurationBaseAccount.class, position = "screen://body")
	public void configuration() {
	}
	
	@FlowAction(next = ActivityLogMain.class, position = "screen://body")
	public void activityLogs() {
	}

	public void realms() {
		if (disableRealmAccountCreation) {
			Roma.aspect(FlowAspect.class).forward(new MessageOk("", "Function Disabled", null, "Function Disabled!"));
		} else {
			Roma.aspect(FlowAspect.class).forward(new RealmAdminMain(), "screen://body");
		}
	}

	public void onDispose() {

	}

	public void onShow() {
		AuthenticationAspect aAsp = Roma.aspect(AuthenticationAspect.class);

		BaseAccount account = (BaseAccount) aAsp.getCurrentAccount();

		disableRealmAccountCreation = false;

		if (account != null) {
			Realm realm = (Realm) aAsp.getCurrentRealm();
			if (realm != null) {
				disableRealmAccountCreation = true;
			}
		} else {
			disableRealmAccountCreation = true;
		}

	}
}
