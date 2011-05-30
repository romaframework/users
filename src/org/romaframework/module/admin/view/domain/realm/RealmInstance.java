package org.romaframework.module.admin.view.domain.realm;

import org.romaframework.aspect.core.annotation.CoreClass;
import org.romaframework.aspect.validation.CustomValidation;
import org.romaframework.aspect.validation.ValidationException;
import org.romaframework.frontend.domain.crud.CRUDInstance;
import org.romaframework.module.users.domain.Realm;

@CoreClass(entity = Realm.class)
public class RealmInstance extends CRUDInstance<Realm> implements CustomValidation {

	public RealmInstance() {
		super();
	}

	public void validate() throws ValidationException {
	}

	@Override
	public void save() {
		super.save();
	}

}
