package org.romaframework.module.admin.view.domain.realm;

import org.romaframework.aspect.view.annotation.ViewClass;
import org.romaframework.frontend.domain.entity.ComposedEntityInstance;
import org.romaframework.module.users.domain.Realm;

@ViewClass(label = "")
public class RealmFilter extends ComposedEntityInstance<Realm> {
  public RealmFilter() {
    this(new Realm());
  }

  public RealmFilter(Realm iInfoCategory) {
    super(iInfoCategory);
  }
}
