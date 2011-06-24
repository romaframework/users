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

package org.romaframework.module.users.view.domain.activitylog;

import java.util.Date;
import java.util.List;

import org.romaframework.aspect.core.annotation.AnnotationConstants;
import org.romaframework.aspect.core.annotation.CoreClass;
import org.romaframework.aspect.view.ViewCallback;
import org.romaframework.aspect.view.ViewConstants;
import org.romaframework.aspect.view.annotation.ViewClass;
import org.romaframework.aspect.view.annotation.ViewField;
import org.romaframework.aspect.view.feature.ViewFieldFeatures;
import org.romaframework.core.Roma;
import org.romaframework.frontend.domain.entity.ComposedEntityInstance;
import org.romaframework.module.users.RealmHelper;
import org.romaframework.module.users.domain.ActivityLog;
import org.romaframework.module.users.domain.Realm;

@CoreClass(orderFields = "realms rangeFrom rangeTo entity")
@ViewClass(label = "")
public class ActivityLogFilter extends ComposedEntityInstance<ActivityLog> implements ViewCallback {

	@ViewField(render = ViewConstants.RENDER_SELECT, selectionField = "selectedRealm")
	protected List<Realm>	realms;

	@ViewField(visible = AnnotationConstants.FALSE)
	protected Realm				selectedRealm;

	private Date					rangeFrom;
	private Date					rangeTo;

	public ActivityLogFilter() {
		super(new ActivityLog());
	}

	public ActivityLogFilter(ActivityLog iActivityLog) {
		super(iActivityLog);
	}

	public void onShow() {
		realms = RealmHelper.getRealms();
		selectedRealm = RealmHelper.getCurrentRealm();

		Roma.fieldChanged(this, "realms");
		Roma.setFeature(this, "realms", ViewFieldFeatures.VISIBLE, selectedRealm == null);
	}

	public Date getRangeFrom() {
		return rangeFrom;
	}

	public void setRangeFrom(Date from) {
		rangeFrom = from;
	}

	public Date getRangeTo() {
		return rangeTo;
	}

	public void setRangeTo(Date to) {
		rangeTo = to;
	}

	public Realm getSelectedRealm() {
		return selectedRealm;
	}

	public void setSelectedRealm(Realm selectedRealm) {
		this.selectedRealm = selectedRealm;
	}

	public List<Realm> getRealms() {
		return realms;
	}

	public void onDispose() {
	}
}
