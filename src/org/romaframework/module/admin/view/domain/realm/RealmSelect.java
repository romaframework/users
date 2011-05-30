package org.romaframework.module.admin.view.domain.realm;

import java.util.ArrayList;
import java.util.List;

import org.romaframework.frontend.domain.crud.CRUDSelect;
import org.romaframework.module.users.domain.Realm;

public class RealmSelect extends CRUDSelect<Realm> {
  public RealmSelect() {
    super(RealmListable.class, RealmInstance.class, RealmInstance.class, RealmInstance.class);
    filter = new RealmFilter();
    result = new ArrayList<RealmListable>();
  }

  @Override
  public RealmFilter getFilter() {
    return filter;
  }

  @Override
  public List<RealmListable> getResult() {
    return result;
  }

  @Override
  public void setResult(Object iValue) {
    result = (List<RealmListable>) iValue;
  }

  protected RealmFilter         filter;

  protected List<RealmListable> result;
}
