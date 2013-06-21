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

package org.openmrs.module.kenyaemr.form;

import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.handler.ExistingVisitAssignmentHandler;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.util.OpenmrsUtil;

import java.util.Locale;

/**
 *
 */
public class EmrVisitAssignmentHandler extends ExistingVisitAssignmentHandler {

	/**
	 * @see org.openmrs.api.handler.ExistingVisitAssignmentHandler#getDisplayName(java.util.Locale)
	 */
	@Override
	public String getDisplayName(Locale locale) {
		return Context.getMessageSourceService().getMessage("Assigns to new or existing visit", null, locale);
	}

	/**
	 * @see org.openmrs.api.handler.ExistingVisitAssignmentHandler#beforeCreateEncounter(org.openmrs.Encounter)
	 */
	@Override
	public void beforeCreateEncounter(Encounter encounter) {

		// Do the default assignment to an existing visit.
		super.beforeCreateEncounter(encounter);

		// Do nothing if the encounter already belongs to a visit.
		if (encounter.getVisit() != null)
			return;

		Visit visit = new Visit();
		visit.setStartDatetime(OpenmrsUtil.firstSecondOfDay(encounter.getEncounterDatetime()));
		visit.setStopDatetime(OpenmrsUtil.getLastMomentOfDay(encounter.getEncounterDatetime()));
		visit.setLocation(encounter.getLocation());
		visit.setPatient(encounter.getPatient());
		visit.setVisitType(getVisitType(encounter));

		encounter.setVisit(visit);
	}

	/**
	 * Gets the visit type for an encounter. For now this returns just OUTPATIENT but in future if we have different
	 * visit types, this would consult the form descriptor
	 * @param encounter the encounter
	 * @return the visit type for the encounter
	 */
	protected VisitType getVisitType(Encounter encounter) throws APIException {
		return Metadata.getVisitType(Metadata.OUTPATIENT_VISIT_TYPE);
	}
}