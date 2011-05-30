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

import java.security.Principal;

import org.romaframework.aspect.core.annotation.CoreClass;
import org.romaframework.aspect.core.annotation.CoreClass.LOADING_MODE;
import org.romaframework.aspect.hook.annotation.HookAction;
import org.romaframework.aspect.hook.annotation.HookScope;
import org.romaframework.core.Roma;
import org.romaframework.frontend.RomaFrontend;
import org.romaframework.frontend.domain.ControlPanel;
import org.romaframework.frontend.domain.message.Message;
import org.romaframework.frontend.domain.message.MessageResponseListener;
import org.romaframework.frontend.domain.wrapper.ModifiableOrderMemberWrapper;
import org.romaframework.frontend.domain.wrapper.OrderMemberWrapperElement;
import org.romaframework.module.users.domain.BaseAccount;
import org.romaframework.module.users.domain.BaseGroup;
import org.romaframework.module.users.domain.BaseProfile;
import org.romaframework.module.users.view.domain.principal.SecurityPrincipalSelectorWindow;

@CoreClass(loading = LOADING_MODE.EARLY)
public class UsersModuleGUIHook implements MessageResponseListener {

	@HookAction(hookBeforeFieldRead = "ControlPanel.panels", scope = HookScope.APPLICATION)
	public void registerControlPanel(ControlPanel iControlPanel) {
		if (iControlPanel.getPanel(UsersModulePagePanel.class) == null)
			iControlPanel.addPanel(new UsersModulePagePanel());
	}

	@HookAction(hookBeforeAction = "ModifiableOrderMemberWrapper.add", scope = HookScope.APPLICATION)
	public void showSecurityPrincipalSelector(ModifiableOrderMemberWrapper<?> iObject) {
		RomaFrontend.flow().forward(new SecurityPrincipalSelectorWindow(this, iObject));
	}

	public void responseMessage(Message iMessage, Object iResponse) {
		ModifiableOrderMemberWrapper<?> context = (ModifiableOrderMemberWrapper<?>) ((SecurityPrincipalSelectorWindow) iMessage)
				.getContext();
		context.getToOrder().add(new OrderMemberWrapperElement(iResponse) {
			public String getValue() {
				Object selection = super.getInitialObject();

				if (selection instanceof Principal) {
					Principal p = (Principal) selection;

					if (selection instanceof BaseAccount)
						return "user:" + p.getName();
					if (selection instanceof BaseProfile)
						return "profile:" + p.getName();
					if (selection instanceof BaseGroup)
						return "group:" + p.getName();
					else
						return null;
				} else
					return selection.toString();
			}
		});
		Roma.fieldChanged(context, "toOrder");
	}
}
