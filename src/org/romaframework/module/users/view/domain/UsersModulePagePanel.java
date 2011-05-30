/*
 * Copyright 2006-2010 Luca Garulli (luca.garulli--at--assetdata.it)
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
 */package org.romaframework.module.users.view.domain;

import org.romaframework.aspect.flow.annotation.FlowAction;
import org.romaframework.frontend.domain.ControlPanelMain;
import org.romaframework.frontend.domain.link.AbstractDynaLink;
import org.romaframework.frontend.domain.link.ClassDynaLink;
import org.romaframework.frontend.domain.page.PagePanel;
import org.romaframework.module.users.view.domain.activitylog.ActivityLogMain;
import org.romaframework.module.users.view.domain.baseaccount.BaseAccountMain;
import org.romaframework.module.users.view.domain.basegroup.BaseGroupMain;
import org.romaframework.module.users.view.domain.baseprofile.BaseProfileMain;

public class UsersModulePagePanel implements PagePanel {
	private AbstractDynaLink[]	links	= new AbstractDynaLink[] { new ClassDynaLink("Accounts", BaseAccountMain.class),
			new ClassDynaLink("Profiles", BaseProfileMain.class), new ClassDynaLink("Groups", BaseGroupMain.class),
			new ClassDynaLink("Application logs", ActivityLogMain.class) };

	@FlowAction(next = ControlPanelMain.class)
	public void onIcon() {
	}

	public String getIcon() {
		return "users.png";
	}

	public AbstractDynaLink[] getLinks() {
		return links;
	}
}
