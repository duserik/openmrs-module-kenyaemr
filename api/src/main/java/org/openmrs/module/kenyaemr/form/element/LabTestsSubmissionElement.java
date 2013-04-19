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

package org.openmrs.module.kenyaemr.form.element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.FormSubmissionError;
import org.openmrs.module.htmlformentry.action.FormSubmissionControllerAction;
import org.openmrs.module.htmlformentry.element.HtmlGeneratorElement;
import org.openmrs.module.kenyaemr.KenyaEmr;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.lab.LabManager;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 *
 */
public class LabTestsSubmissionElement implements HtmlGeneratorElement, FormSubmissionControllerAction {

	protected static final Log log = LogFactory.getLog(LabTestsSubmissionElement.class);

	private List<Obs> formObs = new ArrayList<Obs>();

	public LabTestsSubmissionElement(FormEntryContext context, Map<String, String> parameters) {

		LabManager labManager = KenyaEmr.getInstance().getLabManager();

		// Claim all relevant existing obs
		if (!FormEntryContext.Mode.ENTER.equals(context.getMode())) {
			for (Map.Entry<Concept, List<Obs>> existingForConcept : context.getExistingObs().entrySet()) {
				if (labManager.isLabTest(existingForConcept.getKey())) {
					formObs.addAll(existingForConcept.getValue());
				}
			}

			// Remove claimed obs so other tags can't bind to them
			for (Obs o : formObs) {
				context.removeExistingObs(o.getConcept(), (Concept) null);
			}
		}
	}

	@Override
	public String generateHtml(FormEntryContext formEntryContext) {
		StringBuilder sb = new StringBuilder();

		LabManager labManager = KenyaEmr.getInstance().getLabManager();

		sb.append("<table id=\"ke-labtests-table\" style=\"border: 0\">\n");
		sb.append("  <tbody></tbody>\n");
		sb.append("</table>\n");

		sb.append("<span class=\"ke-field-instructions\">Add new result for</span> ");
		sb.append("<select id=\"ke-labtests-testlist\">\n");

		for (String category : labManager.getCategories()) {
			sb.append("<optgroup label=\"" + category + "\">\n");

			for (Concept testConcept : labManager.getTests(category)) {
				sb.append("<option value=\"" + testConcept.getConceptId() + "\">" + testConcept.getPreferredName(MetadataConstants.LOCALE).getName() + "</option>\n");
			}

			sb.append("</optgroup>\n");
		}

		sb.append("</select>\n");
		sb.append("<input type=\"button\" value=\"Add\" id=\"ke-labtests-addnew\" />\n");
		sb.append("<script type=\"text/javascript\">\n");
		sb.append("  $j('#ke-labtests-addnew').click(function() {\n");
		sb.append("    var testConceptId = $j('#ke-labtests-testlist').val();\n");
		sb.append("    $j.get('/' + CONTEXT_PATH + '/kenyaemr/generateField.htm', { conceptId: testConceptId })\n");
		sb.append("    .done(function (html) {\n");
		sb.append("      $j('#ke-labtests-table tbody').append(html);\n");
		sb.append("    });\n");
		sb.append("  });\n");
		sb.append("</script>\n");

		return sb.toString();
	}

	@Override
	public Collection<FormSubmissionError> validateSubmission(FormEntryContext formEntryContext, HttpServletRequest request) {
		List<FormSubmissionError> errors = new ArrayList<FormSubmissionError>();

		for (Concept testConcept : getSubmittedTestConcepts(request)) {
			try {
				Object testValue = getSubmittedTestValue(request, testConcept);
			}
			catch (Exception ex) {
				String errorField = "ke-lab-test-error-" + testConcept.getConceptId();
				errors.add(new FormSubmissionError(errorField, ex.getMessage()));
			}
		}

		return errors;
	}

	@Override
	public void handleSubmission(FormEntrySession formEntrySession, HttpServletRequest request) {

		for (Concept testConcept : getSubmittedTestConcepts(request)) {

		}
	}

	/**
	 * Gets the test concepts submitted
	 * @param request the request
	 * @return the list of concepts
	 */
	private List<Concept> getSubmittedTestConcepts(HttpServletRequest request) {
		List<Concept> testConcepts = new ArrayList<Concept>();

		Map<String, String[]> params = request.getParameterMap();

		for (Map.Entry<String, String[]> entry : params.entrySet()) {
			String paramName = entry.getKey();

			if (paramName.startsWith("ke-lab-test-")) {
				String conceptId = paramName.split("-")[3];
				Concept testConcept = Context.getConceptService().getConcept(Integer.valueOf(conceptId));
				testConcepts.add(testConcept);
			}
		}

		return testConcepts;
	}

	/**
	 * Gets the value submitted for the given test concept
	 * @param request the request
	 * @return the test value
	 * @throws NumberFormatException
	 */
	private Object getSubmittedTestValue(HttpServletRequest request, Concept testConcept) throws NumberFormatException {
		String fieldName = "ke-lab-test-" + testConcept.getConceptId();
		String paramValue = request.getParameter(fieldName);

		if (testConcept.getDatatype().isNumeric()) {
			return Double.parseDouble(paramValue);
		}
		else if (testConcept.getDatatype().isCoded()) {
			return Context.getConceptService().getConcept(Integer.valueOf(paramValue));
		}

		return null;
	}
}