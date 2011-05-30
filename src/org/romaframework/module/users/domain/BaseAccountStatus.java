package org.romaframework.module.users.domain;

public class BaseAccountStatus {
	private String	name;

	public BaseAccountStatus(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
