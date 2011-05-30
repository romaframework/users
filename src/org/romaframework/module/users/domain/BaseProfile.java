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

package org.romaframework.module.users.domain;

import java.io.Serializable;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.romaframework.core.domain.type.RealmAware;
import org.romaframework.frontend.domain.crud.CRUDHelper;
import org.romaframework.module.users.view.domain.baseprofile.BaseProfileSelect;

public class BaseProfile implements Serializable, RealmAware, Principal {

	private static final long	serialVersionUID	= 2147431210150249521L;

	protected Realm											realm;

	protected String										name;
	protected BaseProfile								parent;
	protected Byte											mode;
	protected Map<String, BaseFunction>	functions;
	protected String										homePage;
	protected String										notes;

	public static final byte						MODE_ALLOW_ALL_BUT	= 0;
	public static final byte						MODE_DENY_ALL_BUT		= 1;

	public BaseProfile() {
	}

	public BaseProfile(Realm iSpace) {
		realm = iSpace;
	}

	public BaseProfile(Realm iSpace, String iName, BaseProfile iParent, byte iMode, String iHomePage) {
		this(iName, iParent, iMode, iHomePage);
		realm = iSpace;
	}

	public BaseProfile(String iName, BaseProfile iParent, byte iMode, String iHomePage) {
		name = iName;
		parent = iParent;
		mode = iMode;
		homePage = iHomePage;

		functions = new TreeMap<String, BaseFunction>();
	}

	public void onParent() {
		CRUDHelper.show(BaseProfileSelect.class, this, "parent");
	}

	@Override
	public String toString() {
		return name;
	}

	public Byte getMode() {
		return mode;
	}

	public BaseProfile setMode(Byte iInheritMode) {
		mode = iInheritMode == null || iInheritMode == -1 ? null : iInheritMode;
		return this;
	}

	public String getName() {
		return name;
	}

	public BaseProfile setName(String name) {
		this.name = name;
		return this;
	}

	public BaseProfile getParent() {
		return parent;
	}

	public BaseProfile setParent(BaseProfile parent) {
		this.parent = parent;
		return this;
	}

	public Map<String, BaseFunction> getFunctions() {
		return functions;
	}

	public BaseProfile setFunctions(Map<String, BaseFunction> functions) {
		this.functions = functions;
		return this;
	}

	public BaseProfile addFunction(String iName, boolean iAllowed) {
		if (functions == null)
			functions = new HashMap<String, BaseFunction>();

		BaseFunction func = new BaseFunction(iName, iAllowed);
		functions.put(iName, func);
		return this;
	}

	public BaseProfile removeFunction(String iKey) {
		functions.remove(iKey);
		return this;
	}

	public String getNotes() {
		return notes;
	}

	public BaseProfile setNotes(String notes) {
		this.notes = notes;
		return this;
	}

	public String getHomePage() {
		return homePage;
	}

	public BaseProfile setHomePage(String homePage) {
		this.homePage = homePage;
		return this;
	}

	/**
	 * @return the space
	 */
	public Realm getRealm() {
		return realm;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((realm == null) ? 0 : realm.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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

		final BaseProfile other = (BaseProfile) obj;

		if (realm == null) {
			if (other.realm != null)
				return false;
		} else if (!realm.equals(other.realm))
			return false;

		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
