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

import org.romaframework.aspect.core.annotation.AnnotationConstants;
import org.romaframework.aspect.view.annotation.ViewField;
import org.romaframework.frontend.domain.crud.CRUDHelper;
import org.romaframework.module.admin.InfoHelper;
import org.romaframework.module.admin.RealmHelper;
import org.romaframework.module.admin.domain.Info;
import org.romaframework.module.users.view.domain.baseaccount.BaseAccountSelect;

public class ActivityLog extends Tracking {

	private static final long	serialVersionUID	= 7777838320505525065L;

	private Info               category;
  private int                level;

  private static Integer[]   LEVELS            = new Integer[] { 0, 1, 2, 3, 4 };
  private static Info[]      CATEGORIES;

  public static final String LOG_CATEGORY_NAME = "Log";

  public ActivityLog() {
    // DOUBLE CHECKING LOCKING PATTERN TO AVOID STATIC INITIALIZATION FOR
    // ENHANCEMENT ISSUES
  }

  public ActivityLog(int iLevel, Info iCategory, String iNotes) {
    super(iNotes);
    level = iLevel;
    category = iCategory;
  }

  public void onAccount() {
    CRUDHelper.show(BaseAccountSelect.class, this, "account");
  }

  @ViewField(enabled = AnnotationConstants.FALSE)
  @Override
  public BaseAccount getAccount() {
    return super.getAccount();
  }

  public Info getCategory() {
    return category;
  }

  public int getLevel() {
    return level;
  }

  public void setCategory(Info category) {
    this.category = category;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  @ViewField(selectionField = "level")
  public Integer[] getLevels() {
    return LEVELS;
  }

  @ViewField(selectionField = "category")
  public Info[] getCategories() {
    if (CATEGORIES == null) {
      synchronized (ActivityLog.class) {
        if (CATEGORIES == null) {
          CATEGORIES = InfoHelper.getInstance().getInfoArray(RealmHelper.getCurrentRealm(), LOG_CATEGORY_NAME);
        }
      }
    }
  	return CATEGORIES;
  }
}
