package org.romaframework.module.admin.view.domain.realm;

import org.romaframework.aspect.core.annotation.CoreClass;
import org.romaframework.frontend.domain.entity.ComposedEntityInstance;
import org.romaframework.module.users.domain.Realm;

@CoreClass(entity = Realm.class)
public class RealmListable extends ComposedEntityInstance<Realm> {
  public RealmListable(Realm iEntity) {
    super(iEntity);
  }

  // INSERT HERE GETTER METHOD TO VIEW ADDITIONAL FIELDS
}
