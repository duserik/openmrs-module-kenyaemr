/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.kenyaemr.interceptor;

import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.page.controller.AdminFirstTimeSetupPageController;
import org.openmrs.module.kenyaemr.page.controller.LoginPageController;
import org.openmrs.ui.framework.interceptor.PageRequestInterceptor;
import org.openmrs.ui.framework.page.PageAction;
import org.openmrs.ui.framework.page.PageContext;
import org.openmrs.ui.framework.page.Redirect;
import org.springframework.stereotype.Component;

/**
 * Interceptor to check if setup is required
 */
@Component
public class SetupRequiredPageRequestInterceptor implements PageRequestInterceptor {

	/**
	 * @see PageRequestInterceptor#beforeHandleRequest(org.openmrs.ui.framework.page.PageContext)
	 */
	@Override
	public void beforeHandleRequest(PageContext pageContext) throws PageAction {
		boolean onLoginPage = instanceOf(pageContext.getController(), LoginPageController.class);
		boolean onSetupPage = instanceOf(pageContext.getController(), AdminFirstTimeSetupPageController.class);

		// Redirect to first-time setup page if module is not yet configured
		if (!onLoginPage && !onSetupPage && Context.getService(KenyaEmrService.class).isSetupRequired()) {
			throw new Redirect(EmrConstants.MODULE_ID, "adminFirstTimeSetup", null);
		}
	}

	private static boolean instanceOf(Object obj, Class<?> clazz) {
		return obj.getClass().getCanonicalName().equals(clazz.getCanonicalName());
	}
}