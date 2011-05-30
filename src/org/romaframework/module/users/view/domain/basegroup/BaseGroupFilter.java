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

package org.romaframework.module.users.view.domain.basegroup;

import java.util.List;

import org.romaframework.aspect.core.annotation.AnnotationConstants;
import org.romaframework.aspect.core.annotation.CoreClass;
import org.romaframework.aspect.view.ViewAspect;
import org.romaframework.aspect.view.ViewCallback;
import org.romaframework.aspect.view.ViewConstants;
import org.romaframework.aspect.view.annotation.ViewClass;
import org.romaframework.aspect.view.annotation.ViewField;
import org.romaframework.aspect.view.feature.ViewElementFeatures;
import org.romaframework.core.flow.ObjectContext;
import org.romaframework.frontend.domain.entity.ComposedEntityInstance;
import org.romaframework.module.admin.RealmHelper;
import org.romaframework.module.admin.domain.Realm;
import org.romaframework.module.users.domain.BaseGroup;

@CoreClass(entity = BaseGroup.class)
@ViewClass(label = "")
public class BaseGroupFilter extends ComposedEntityInstance<BaseGroup> implements ViewCallback {

  @ViewField(render = ViewConstants.RENDER_SELECT, selectionField = "selectedRealm")
  private List<Realm> realm = RealmHelper.getRealms();

  @ViewField(visible = AnnotationConstants.FALSE)
  private Realm       selectedRealm;

  public BaseGroupFilter() {
    this(new BaseGroup(RealmHelper.getCurrentRealm()));
  }

  public BaseGroupFilter(BaseGroup iBaseGroup) {
    super(iBaseGroup);
    selectedRealm = entity.getRealm();
  }

  public void onShow() {
    ObjectContext.getInstance().setFieldFeature(this, ViewAspect.ASPECT_NAME, "realm", ViewElementFeatures.ENABLED,
        selectedRealm == null);
  }

  /**
   * @return the realm
   */
  public List<Realm> getRealm() {
    return realm;
  }

  /**
   * @return the selected
   */
  public Realm getSelectedRealm() {
    return selectedRealm;
  }

  /**
   * @param selected
   *          the selected to set
   */
  public void setSelectedRealm(Realm selected) {
    selectedRealm = selected;
  }

  public void onDispose() {
  }
}
