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

import org.romaframework.aspect.core.annotation.CoreClass;
import org.romaframework.aspect.view.ViewConstants;
import org.romaframework.aspect.view.annotation.ViewField;
import org.romaframework.core.flow.ObjectContext;
import org.romaframework.frontend.domain.crud.CRUDHelper;
import org.romaframework.frontend.domain.crud.CRUDInstance;
import org.romaframework.module.admin.RealmHelper;
import org.romaframework.module.admin.view.domain.customizable.CustomizableConfigurationForm;
import org.romaframework.module.users.domain.BaseGroup;
import org.romaframework.module.users.view.domain.account.AccountSelect;

@CoreClass(entity = BaseGroup.class)
public class BaseGroupInstance extends CRUDInstance<BaseGroup> {

  @ViewField(label = "", render = ViewConstants.RENDER_OBJECTEMBEDDED)
  CustomizableConfigurationForm configuration;

  /**
   * @return the configuration
   */
  public CustomizableConfigurationForm getConfiguration() {
    return configuration;
  }

  /**
   * @param configuration
   *          the configuration to set
   */
  public void setConfiguration(CustomizableConfigurationForm configuration) {
    this.configuration = configuration;
  }

  public void onAccountListAdd() {
    CRUDHelper.show(AccountSelect.class, entity, "accountList");
  }

  @Override
  public void onCreate() {

    BaseGroup newGroup = new BaseGroup(RealmHelper.getCurrentRealm());

    setEntity(newGroup);

    configuration = new CustomizableConfigurationForm(entity);
    ObjectContext.getInstance().fieldChanged(this, "configuration");
  }

  @Override
  public void onUpdate() {
    configuration = new CustomizableConfigurationForm(entity);
    ObjectContext.getInstance().fieldChanged(this, "configuration");
  }

  @Override
  public void onShow() {
    ObjectContext.getInstance().fieldChanged(this, "configuration");
  }

}
