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
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.kenyaemr.calculation.CalculationManager;
import org.openmrs.module.kenyaemr.form.FormManager;
import org.openmrs.module.kenyaemr.lab.LabManager;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.reporting.ReportManager;
import org.openmrs.module.kenyaemr.util.BuildProperties;
import org.openmrs.ui.framework.UiContextRefreshedCallback;
import org.openmrs.ui.framework.fragment.FragmentFactory;
import org.openmrs.ui.framework.page.PageFactory;
import org.openmrs.ui.framework.resource.ResourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Kenya EMR master content manager, used as a registered singleton
 */
@Component
public class KenyaEmr implements UiContextRefreshedCallback {

	protected static final Log log = LogFactory.getLog(KenyaEmrActivator.class);

	@Autowired
	MetadataManager metadataManager;

	@Autowired
	RegimenManager regimenManager;

	@Autowired
	FormManager formManager;

	@Autowired
	CalculationManager calculationManager;

	@Autowired
	ReportManager reportManager;

	@Autowired
	LabManager labManager;

	/**
	 * Gets the module version
	 * @return the version
	 */
	public String getModuleVersion() {
		return ModuleFactory.getModuleById(KenyaEmrConstants.MODULE_ID).getVersion();
	}

	/**
	 * Gets the module build properties
	 * @return the build properties map or null if not available
	 */
	public BuildProperties getModuleBuildProperties() {
		List<BuildProperties> propBeans = Context.getRegisteredComponents(BuildProperties.class);
		return propBeans.size() > 0 ? propBeans.get(0) : null;
	}

	/**
	 * Gets the metadata manager
	 * @return the metadata manager
	 */
	public MetadataManager getMetadataManager() {
		return metadataManager;
	}

	/**
	 * Gets the regimen manager
	 * @return the regimen manager
	 */
	public RegimenManager getRegimenManager() {
		return regimenManager;
	}

	/**
	 * Gets the form manager
	 * @return the form manager
	 */
	public FormManager getFormManager() {
		return formManager;
	}

	/**
	 * Gets the calculation manager
	 * @return the calculation manager
	 */
	public CalculationManager getCalculationManager() {
		return calculationManager;
	}

	/**
	 * Gets the report manager
	 * @return the report manager
	 */
	public ReportManager getReportManager() {
		return reportManager;
	}

	/**
	 * Gets the lab manager
	 * @return the lab manager
	 */
	public LabManager getLabManager() {
		return labManager;
	}

	/**
	 * Utility method to get the singleton instance from the application context in situations where you
	 * can't use @Autowired or @SpringBean. Use as a last resort.
	 * @return the singleton instance
	 */
	public static KenyaEmr getInstance() {
		return Context.getRegisteredComponents(KenyaEmr.class).get(0);
	}

	/**
	 * Handles a context refresh
	 */
	public void afterContextRefreshed(PageFactory pageFactory, FragmentFactory fragmentFactory, ResourceFactory resourceFactory) {
		try {
			calculationManager.refreshCalculationClasses();
			formManager.refreshFormDescriptors();
			reportManager.refreshReportBuilders();
		}
		catch (Exception ex) {
			log.error("Error during Kenya EMR context refresh", ex);
		}
	}
}