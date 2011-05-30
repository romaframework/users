/*
 * Copyright 2006-2007 Giordano Maestro (giordano.maestro--at--assetdata.it)
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
package org.romaframework.module.users.logger;

import org.romaframework.aspect.logging.AbstractLogger;
import org.romaframework.aspect.logging.LoggingAspect;
import org.romaframework.aspect.logging.LoggingConstants;
import org.romaframework.aspect.persistence.PersistenceAspect;
import org.romaframework.aspect.persistence.QueryByFilter;
import org.romaframework.core.Roma;
import org.romaframework.module.admin.InfoHelper;
import org.romaframework.module.admin.RealmHelper;
import org.romaframework.module.admin.domain.Info;
import org.romaframework.module.admin.domain.InfoCategory;
import org.romaframework.module.users.domain.ActivityLog;

/**
 * Is logs the event using the ActivityLog of the users module
 * 
 * @author Giordano Maestro (giordano.maestro--at--assetdata.it)
 * 
 */
public class DBLogger extends AbstractLogger {

  public DBLogger(LoggingAspect loggingAspect) {
    super(loggingAspect);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.romaframework.aspect.logging.Logger#getModes()
   */
  public String[] getModes() {

    String[] modes = { LoggingConstants.MODE_DB };
    return modes;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.romaframework.aspect.logging.Logger#print(int, java.lang.String, java.lang.String)
   */
  public void print(int level, String category, String message) {
    PersistenceAspect db = Roma.context().persistence();
    String categoryName = ActivityLog.LOG_CATEGORY_NAME;
    QueryByFilter filter = new QueryByFilter(Info.class);
    filter.setStrategy(PersistenceAspect.STRATEGY_DETACHING);
    filter.addItem("text", QueryByFilter.FIELD_EQUALS, category);
    filter.addItem("category.name", QueryByFilter.FIELD_EQUALS, categoryName);

    Info info = db.queryOne(filter);

    if (info == null) {
      InfoCategory iCat = InfoHelper.getInstance().getInfoCategory(categoryName);
      if (iCat == null) {
        iCat = new InfoCategory(RealmHelper.getCurrentRealm(), categoryName, categoryName);
      }

      info = new Info(iCat, category);
    }

    ActivityLog log = new ActivityLog(level, info, message);

    db.createObject(log);

  }

}
