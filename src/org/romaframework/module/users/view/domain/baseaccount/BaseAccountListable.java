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

package org.romaframework.module.users.view.domain.baseaccount;

import org.romaframework.aspect.core.annotation.CoreClass;
import org.romaframework.frontend.domain.entity.ComposedEntityInstance;
import org.romaframework.module.admin.domain.Realm;
import org.romaframework.module.users.domain.BaseAccount;

@CoreClass(entity = BaseAccount.class)
public class BaseAccountListable extends ComposedEntityInstance<BaseAccount> {
  public BaseAccountListable(BaseAccount iEntity) {
    super(iEntity);
  }

  public Realm getRealm() {
    if (entity.getProfile() != null)
      return entity.getProfile().getRealm();
    return null;
  }
}
