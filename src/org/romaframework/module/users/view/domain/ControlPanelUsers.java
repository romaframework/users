package org.romaframework.module.users.view.domain;

import org.romaframework.frontend.domain.page.ContainerPage;
import org.romaframework.module.admin.view.domain.realm.RealmMain;
import org.romaframework.module.users.view.domain.activitylog.ActivityLogMain;
import org.romaframework.module.users.view.domain.baseaccount.BaseAccountMain;
import org.romaframework.module.users.view.domain.basegroup.BaseGroupMain;
import org.romaframework.module.users.view.domain.baseprofile.BaseProfileMain;
import org.romaframework.module.users.view.domain.configuration.ConfigurationBaseAccount;

public class ControlPanelUsers extends ContainerPage {

	public ControlPanelUsers() {
		addPage("Accounts", new BaseAccountMain());
		addPage("Groups", new BaseGroupMain());
		addPage("Profiles", new BaseProfileMain());
		addPage("Log", new ActivityLogMain());
		addPage("Realms", new RealmMain());
		addPage("Configuration", new ConfigurationBaseAccount());
	}
}
