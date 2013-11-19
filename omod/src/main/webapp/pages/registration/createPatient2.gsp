<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	ui.includeJavascript("kenyaemr", "controllers/patient.js")

	def menuItems = [
			[ label: "Back to home", iconProvider: "kenyaui", icon: "buttons/back.png", label: "Back to previous step", href: ui.pageLink("kenyaemr", "registration/createPatient1") ]
	]
%>
<div class="ke-page-sidebar">
	${ ui.includeFragment("kenyaui", "widget/panelMenu", [ heading: "Create Patient", items: menuItems ]) }

	<% if (!person) { %>
	<div class="ke-panel-frame" id="ng-similarpatients" ng-controller="SimilarPatients" ng-init="init('${ currentApp.id }')">
		<script type="text/javascript">
			jQuery(function() {
				jQuery('input[name="personName.givenName"], input[name="personName.familyName"]').change(function() {
					var givenName = jQuery('input[name="personName.givenName"]').val();
					var familyName = jQuery('input[name="personName.familyName"]').val();

					var scope = angular.element(document.getElementById('ng-similarpatients')).scope();
					scope.\$apply(function() {
						scope.givenName = givenName;
						scope.familyName = familyName;
						scope.refresh();
					});
				});
			});
		</script>
		<div class="ke-panel-heading">Similar Patients</div>
		<div class="ke-panel-content">
			<div class="ke-stack-item" ng-repeat="patient in results">
				{{ patient.name }}
			</div>
		</div>
	</div>
	<% } %>
</div>

<div class="ke-page-content">
	${ ui.includeFragment("kenyaemr", "patient/editPatient", [ person: person ]) }
</div>