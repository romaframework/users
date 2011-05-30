package org.romaframework.module.admin.view.domain.realm;

import java.util.ArrayList;
import java.util.List;

import org.romaframework.aspect.core.annotation.AnnotationConstants;
import org.romaframework.aspect.core.annotation.CoreField;
import org.romaframework.core.domain.entity.ComposedEntity;
import org.romaframework.frontend.domain.crud.CRUDMain;
import org.romaframework.frontend.domain.crud.CRUDPaging;
import org.romaframework.frontend.domain.message.Message;

public class RealmMain extends CRUDMain<RealmListable> {

  public RealmMain() {
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

  @SuppressWarnings("unchecked")
	@Override
  public void setResult(Object iValue) {
    result = (List<RealmListable>) iValue;
  }

  @Override
  public void responseMessage(Message iMessage, Object iResponse) {
    if (iResponse == null || !((Boolean) iResponse))
      // USER SELECT NO
      return;

    if (iMessage.getId().equals(CRUDPaging.QUERY_ALL_MESSAGE)) {
      queryRequest.setRangeFrom(0, queryRequest.getTotalItems());
      executeQuery();
      return;
    }

    Object[] selection = getSelection();
    if (selection != null) {
      Object selectedInstance;
      ArrayList<Object> selectedInstances = new ArrayList<Object>();
      // BUILD THE ARRAY OF OBJECT TO DELETE
      for (int i = 0; i < selection.length; ++i) {
        selectedInstance = selection[i];

        if (selection[i] instanceof ComposedEntity<?>)
          // GET THE COMPOSED SELECTION
          selectedInstance = ((ComposedEntity<?>) selectedInstance).getEntity();

        selectedInstances.add(selectedInstance);
      }

      // DELETE THE PERSISTENT OBJECTS
      getPersistenceAspect().deleteObjects(selectedInstances.toArray());

      // RE-EXECUTE THE QUERY TO UPDATE VIEW
      search();
    }
  }

  @CoreField(embedded = AnnotationConstants.TRUE)
  protected RealmFilter         filter;

  protected List<RealmListable> result;
}
