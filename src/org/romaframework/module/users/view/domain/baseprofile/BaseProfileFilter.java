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

package org.romaframework.module.users.view.domain.baseprofile;

import java.util.List;

import org.romaframework.aspect.core.annotation.AnnotationConstants;
import org.romaframework.aspect.view.SelectionMode;
import org.romaframework.aspect.view.ViewCallback;
import org.romaframework.aspect.view.ViewConstants;
import org.romaframework.aspect.view.annotation.ViewClass;
import org.romaframework.aspect.view.annotation.ViewField;
import org.romaframework.aspect.view.feature.ViewFieldFeatures;
import org.romaframework.core.Roma;
import org.romaframework.frontend.domain.entity.ComposedEntityInstance;
import org.romaframework.module.users.RealmHelper;
import org.romaframework.module.users.domain.BaseProfile;
import org.romaframework.module.users.domain.Realm;

@ViewClass(label = "")
public class BaseProfileFilter extends ComposedEntityInstance<BaseProfile> implements ViewCallback {

	@ViewField(render = ViewConstants.RENDER_SELECT, selectionField = "selectedRealm")
	private List<Realm>	realm	= RealmHelper.getRealms();

	@ViewField(visible = AnnotationConstants.FALSE)
	private Realm				selectedRealm;

	public BaseProfileFilter() {
		super(new BaseProfile(RealmHelper.getCurrentRealm()));
		selectedRealm = entity.getRealm();
	}

	public BaseProfileFilter(BaseProfile iProfile) {
		super(iProfile);
	}

	public void onShow() {
		Roma.setFeature(this, "realm", ViewFieldFeatures.VISIBLE, selectedRealm == null);
	}

	@ViewField(label = "Mode", render = "select", selectionField = "entity.mode", selectionMode = SelectionMode.SELECTION_MODE_INDEX)
	public String[] getModes() {
		return BaseProfileHelper.MODES;
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
