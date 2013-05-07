package org.romaframework.module.users.factory;

import org.romaframework.core.factory.AbstractFactory;
import org.romaframework.module.users.domain.BaseProfile;
import org.romaframework.module.users.domain.CustomProfiling;

public class BaseProfileFactory extends AbstractFactory<BaseProfile> {

	@Override
	public BaseProfile create() {
		return new BaseProfile();
	}

}
