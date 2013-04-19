<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<tr id="ke-lab-test-holder-${ concept.conceptId }">
	<td>${ concept.name.name }</td>

<c:choose>
	<c:when test="${ concept.datatype.numeric }" >
	<td><input id="ke-lab-test-val-${ concept.conceptId }" name="ke-lab-test-${ concept.conceptId }" type="text" onblur="checkNumber(this, 'ke-lab-test-error-${ concept.conceptId }', ${ concept.precise ? 'true' : 'false' }, ${ concept.lowAbsolute != null ? concept.lowAbsolute : null }, ${ concept.hiAbsolute != null ? concept.hiAbsolute : null })" /></td>
	<td>${ concept.units }</td>
	</c:when>
	<c:when test="${ concept.datatype.coded }" >
	<td>
		<select id="ke-lab-test-val-${ concept.conceptId }" name="ke-lab-test-${ concept.conceptId }">
		<c:forEach items="${ concept.answers }" var="answer">
			<option value="${ answer.answerConcept.conceptId }">${ answer.answerConcept.name.name }</option>
		</c:forEach>
		</select>
	</td>
	<td></td>
	</c:when>
</c:choose>
	<td><span id="ke-lab-test-error-${ concept.conceptId }" class="error field-error" style="display: none"></span></td>
<tr>