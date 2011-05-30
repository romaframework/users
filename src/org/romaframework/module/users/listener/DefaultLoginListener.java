package org.romaframework.module.users.listener;

import org.romaframework.aspect.authentication.LoginListener;
import org.romaframework.core.Roma;
import org.romaframework.core.exception.ConfigurationException;
import org.romaframework.core.exception.UserException;
import org.romaframework.core.flow.ObjectContext;
import org.romaframework.frontend.RomaFrontend;
import org.romaframework.module.users.domain.BaseAccount;

public class DefaultLoginListener implements LoginListener {

	public void onError(Throwable t) {
		throw new UserException(null, "$authentication.error", t);
	}

	public void onSuccess() {
		String homePage = ((BaseAccount) Roma.session().getActiveSessionInfo().getAccount()).getProfile().getHomePage();
		if (homePage == null)
			throw new ConfigurationException("$Login.homepage.error");
		Object toDisplay = ObjectContext.getInstance().getObject(homePage);
		RomaFrontend.flow().clearHistory();
		RomaFrontend.flow().forward(toDisplay);
	}

}
