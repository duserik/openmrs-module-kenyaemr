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

package org.openmrs.module.kenyaemr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles direct fragment requests, e.g. kenyaemr/help.frag.page
 */
@Controller
public class FragmentRequestController {

	protected static final Log log = LogFactory.getLog(FragmentRequestController.class);

	@RequestMapping("**/*.frag.page")
	public String handleUrlWithDotFrag(HttpServletRequest request, HttpServletResponse response) {
		String path = request.getServletPath();
		path = path.substring(1, path.lastIndexOf(".frag.page"));

		int index = path.indexOf("/");
		if (index < 0) {
			throw new IllegalArgumentException("Malformed fragment request URL: " + request.getRequestURI());
		}

		String fragmentProvider = path.substring(0, index);
		String fragment = path.substring(index + 1);

		if (log.isDebugEnabled()) {
			log.debug("Handling direct fragment request for " + fragmentProvider + ":" + fragment);
		}

		request.setAttribute("fragmentProvider", fragmentProvider);
		request.setAttribute("fragment", fragment);

		return "forward:/" + EmrConstants.MODULE_ID + "/fragment.page";
	}
}