package org.romaframework.module.users.factory;

import org.romaframework.core.factory.AbstractFactory;
import org.romaframework.module.users.domain.BaseAccount;

public class BaseAccountFactory extends AbstractFactory<BaseAccount> {

	@Override
	public BaseAccount create() {
		return new BaseAccount();
	}

}
