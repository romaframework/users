/*
 * Copyright 2009 Luigi Dell'Aquila (luigi.dellaquila@assetdata.it)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.romaframework.module.security.users;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.romaframework.aspect.authentication.AuthenticationAspect;
import org.romaframework.aspect.security.Secure;
import org.romaframework.aspect.security.SecurityAspect;
import org.romaframework.aspect.security.SecurityAspectAbstract;
import org.romaframework.aspect.security.exception.SecurityException;
import org.romaframework.aspect.security.feature.SecurityActionFeatures;
import org.romaframework.aspect.security.feature.SecurityClassFeatures;
import org.romaframework.aspect.security.feature.SecurityFieldFeatures;
import org.romaframework.aspect.session.SessionInfo;
import org.romaframework.aspect.view.ViewAspect;
import org.romaframework.aspect.view.feature.ViewFieldFeatures;
import org.romaframework.core.Roma;
import org.romaframework.core.schema.SchemaAction;
import org.romaframework.core.schema.SchemaClass;
import org.romaframework.core.schema.SchemaClassElement;
import org.romaframework.core.schema.SchemaEvent;
import org.romaframework.core.schema.SchemaField;
import org.romaframework.core.schema.xmlannotations.XmlEventAnnotation;
import org.romaframework.module.users.domain.AbstractAccount;
import org.romaframework.module.users.domain.BaseAccount;
import org.romaframework.module.users.domain.BaseGroup;

public class UsersSecurityAspect extends SecurityAspectAbstract {

	// protected String
	@Override
	public String aspectName() {
		return ASPECT_NAME;
	}

	@Override
	public void startup() {
		super.startup();
	}

	public Object getUnderlyingComponent() {
		return null;
	}

	public void configEvent(SchemaEvent event, Annotation eventAnnotation, Annotation genericAnnotation, XmlEventAnnotation node) {
	}

	private BaseAccount getAccount() {
		BaseAccount account = (BaseAccount) Roma.aspect(AuthenticationAspect.class).getCurrentAccount();
		return account;
	}

	public boolean canRead(Object obj, SchemaField iSchemaField) {
		if (obj instanceof Secure && !((Secure) obj).canRead()) {
			return false;
		}
		return canRead(obj, iSchemaField, getAccount());
	}

	public boolean canWrite(Object obj, SchemaField iSchemaField) {
		if (obj instanceof Secure && !((Secure) obj).canWrite()) {
			return false;
		}
		return canWrite(obj, iSchemaField, getAccount());
	}

	public boolean canExecute(Object obj, SchemaClassElement iSchemaElement) {
		return canExecute(obj, iSchemaElement, getAccount());
	}

	public boolean canRead(Object obj, SchemaField iSchemaField, AbstractAccount account) {
		String[] readRules = (String[]) iSchemaField.getFeature(SecurityAspect.ASPECT_NAME, SecurityFieldFeatures.READ_ROLES);
		if (readRules == null || readRules.equals("")) {
			readRules = (String[]) iSchemaField.getEntity().getFeature(SecurityAspect.ASPECT_NAME, SecurityClassFeatures.READ_ROLES);
		}
		return matchesRule(iSchemaField.toString(), account, readRules);
	}

	public boolean canWrite(Object obj, SchemaField iSchemaField, AbstractAccount account) {
		String[] readRules = (String[]) iSchemaField.getFeature(SecurityAspect.ASPECT_NAME, SecurityFieldFeatures.WRITE_ROLES);
		if (readRules == null || readRules.equals("")) {
			readRules = (String[]) iSchemaField.getEntity().getFeature(SecurityAspect.ASPECT_NAME, SecurityClassFeatures.WRITE_ROLES);
		}
		return matchesRule(iSchemaField.toString(), account, readRules);
	}

	public boolean canExecute(Object obj, SchemaClassElement iSchemaAction, AbstractAccount account) {
		String[] readRules = (String[]) iSchemaAction.getFeature(SecurityAspect.ASPECT_NAME, SecurityActionFeatures.ROLES);
		if (readRules == null || readRules.equals("")) {
			readRules = (String[]) iSchemaAction.getEntity().getFeature(SecurityAspect.ASPECT_NAME, SecurityClassFeatures.EXECUTE_ROLES);
		}
		return matchesRule(iSchemaAction.toString(), account, readRules);
	}

	public boolean matchesRule(String iResource, AbstractAccount account, String[] readRules) {
		if (readRules == null || readRules.length == 0)
			return true;// no rules exist on this element
		if (account == null) {
			throw new SecurityException("The resource requested '" + iResource
					+ "' is protected and need an authenticated account to access in");
		}

		for (String readRule : readRules) {
			readRule = readRule.trim();
			if (readRule.isEmpty())
				throw new IllegalArgumentException("Found an empty rule for the resource: " + iResource);

			int split_idx = readRule.indexOf(':');
			if (split_idx == -1)
				throw new IllegalArgumentException("Found wrong rule: '" + readRule + "' for the resource: " + iResource);
			String target = readRule.substring(0, split_idx);
			String rule = readRule.substring(split_idx + 1);
			if ("user".equalsIgnoreCase(target)) {
				if (Pattern.matches(rule, account.getName()))
					return true;
			}
			if (account instanceof BaseAccount) {
				BaseAccount baseAccount = (BaseAccount) account;
				if ("profile".equalsIgnoreCase(target)) {
					if (baseAccount.getProfile() != null && baseAccount.getProfile().getName() != null) {
						if (Pattern.matches(rule, baseAccount.getProfile().getName()))
							return true;
					}
				}
				if ("group".equalsIgnoreCase(target)) {
					for (BaseGroup group : baseAccount.getGroups()) {
						if (matchesRule(iResource, group, readRules))
							return true;
					}
				}
			}
		}
		return false;
	}

	public Object decrypt(Object obj, String fieldName) {
		// TODO implement this!!!
		throw new UnsupportedOperationException();
	}

	public Object encrypt(Object obj, String fieldName) {
		// TODO implement this!!!
		throw new UnsupportedOperationException();
	}

	public int getPriority() {
		return 0;
	}

	public void onAfterActionExecution(Object iContent, SchemaClassElement iAction, Object returnedValue) {
	}

	public Object onAfterFieldRead(Object iContent, SchemaField iField, Object iCurrentValue) {
		if (iCurrentValue instanceof Collection<?>) {
			Iterator<?> iter = ((Collection<?>) iCurrentValue).iterator();
			while (iter.hasNext()) {
				if (hasToRemoveValue(iter.next()))
					iter.remove();
			}
		}

		if (iCurrentValue instanceof Map<?, ?>) {
			Map<?, ?> map = (Map<?, ?>) iCurrentValue;

			Object key;

			Iterator<?> iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<?, ?> iter = (Map.Entry<?, ?>) iterator.next();
				key = iter.getKey();

				// CHECK THE KEY
				if (key != null && !allowClass(Roma.schema().getSchemaClass(key.getClass())))
					iterator.remove();
				// CHECK THE VALUE
				else if (iter.getValue() != null && !allowClass(Roma.schema().getSchemaClass(iter.getValue().getClass())))
					iterator.remove();
			}
		}

		return iCurrentValue;
	}

	public Object onAfterFieldWrite(Object iContent, SchemaField iField, Object iCurrentValue) {
		return iCurrentValue;
	}

	public boolean onBeforeActionExecution(Object iContent, SchemaClassElement iAction) {
		if (canExecute(iContent, iAction)) {
			return true;
		}
		throw new SecurityException("Current account can't execute the action '" + iAction + "' because has no privileges");
	}

	public Object onBeforeFieldRead(Object iContent, SchemaField iField, Object iCurrentValue) {
		if (canRead(iContent, iField)) {
			if (!canWrite(iContent, iField)) {
				Boolean enabled = (Boolean) iField.getFeature(ViewAspect.ASPECT_NAME, ViewFieldFeatures.ENABLED);
				if (enabled == null || enabled) {
					Roma.setFieldFeature(iContent, ViewAspect.ASPECT_NAME, iField.getName(), ViewFieldFeatures.ENABLED, false);
				}
			}
			return IGNORED;
		}
		Boolean enabled = (Boolean) iField.getFeature(ViewAspect.ASPECT_NAME, ViewFieldFeatures.ENABLED);
		if (enabled == null || enabled) {
			Roma.setFieldFeature(iContent, ViewAspect.ASPECT_NAME, iField.getName(), ViewFieldFeatures.ENABLED, false);
		}
		return null;
	}

	public Object onBeforeFieldWrite(Object iContent, SchemaField iField, Object iCurrentValue) {
		if (canWrite(iContent, iField)) {
			return iCurrentValue;
		}
		Object result = iField.getValue(iContent);
		Boolean enabled = (Boolean) iField.getFeature(ViewAspect.ASPECT_NAME, ViewFieldFeatures.ENABLED);
		if (enabled == null || enabled) {
			Roma.setFieldFeature(iContent, ViewAspect.ASPECT_NAME, iField.getName(), ViewFieldFeatures.ENABLED, false);
		}
		return result;
	}

	public Object onException(Object iContent, SchemaClassElement iElement, Throwable iThrowed) {
		return true;
	}

	public void onFieldRefresh(SessionInfo iSession, Object iContent, SchemaField iField) {
	}

	public boolean allowAction(SchemaAction iAction) {
		if (iAction == null)
			return true;

		String[] rule = (String[]) iAction.getFeature(SecurityAspect.ASPECT_NAME, SecurityActionFeatures.ROLES);
		if (rule == null) {
			rule = (String[]) iAction.getEntity().getFeature(SecurityAspect.ASPECT_NAME, SecurityClassFeatures.EXECUTE_ROLES);
		}
		if (rule == null)
			return true;
		return matchesRule(iAction.toString(), getAccount(), rule);
	}

	public boolean allowClass(SchemaClass iClass) {
		if (iClass == null)
			return true;

		String rule[] = (String[]) iClass.getFeature(SecurityAspect.ASPECT_NAME, SecurityClassFeatures.READ_ROLES);
		if (rule == null)
			return true;
		return matchesRule(iClass.toString(), getAccount(), rule);
	}

	public boolean allowEvent(SchemaEvent iEvent) {
		if (iEvent == null)
			return true;

		String[] rule = (String[]) iEvent.getFeature(SecurityAspect.ASPECT_NAME, SecurityActionFeatures.ROLES);
		if (rule == null) {
			rule = (String[]) iEvent.getEntity().getFeature(SecurityAspect.ASPECT_NAME, SecurityClassFeatures.EXECUTE_ROLES);
		}
		if (rule == null)
			return true;
		return matchesRule(iEvent.toString(), getAccount(), rule);
	}

	public boolean allowField(SchemaField iField) {
		if (iField == null)
			return true;

		String[] rule = (String[]) iField.getFeature(SecurityAspect.ASPECT_NAME, SecurityFieldFeatures.READ_ROLES);
		if (rule == null) {
			rule = (String[]) iField.getEntity().getFeature(SecurityAspect.ASPECT_NAME, SecurityClassFeatures.READ_ROLES);
		}
		if (rule == null)
			return true;
		return matchesRule(iField.toString(), getAccount(), rule);
	}

	private boolean hasToRemoveValue(Object iValue) {
		if (iValue instanceof Secure && !((Secure) iValue).canRead())
			return true;

		if (iValue != null && !allowClass(Roma.schema().getSchemaClassIfExist(iValue.getClass())))
			return true;

		return false;
	}
}
