<%@ page language="java" contentType="text/html; charset=UTF-8" session="false" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<jsp:include page="header.jsp">
	<jsp:param name="page" value="Tuotteet"/>
</jsp:include>

<div class="container">

<h2>Tuotteiden hallinta</h2>

<ul class="breadcrumb">
<li><a href="<c:url value="/hallinta/tuotteet"/>">tuotelistaus</a> <span class="divider">/</span></li>
<li class="active">${dataset.translatedTitles['fi']}</li>
</ul>

<%-- <div id="subcontent"> --%>

<h2><c:if test="${dataset.name == null}">Lisää uusi tuote</c:if><c:out value="${dataset.translatedTitles['fi']}"/></h2>

<c:if test="${dataset.name != null}">
<div class="opbox">
<a href="<c:url value="/hallinta/tuotteet/${dataset.name}/lisaa"/>">LISÄÄ TUOTEVERSIO</a>
</div>
</c:if>

<c:url value='/hallinta/tuotteet/' var="updateUrl"/>
<form:form modelAttribute="dataset" action="${updateUrl}">

<fieldset><legend>Tekniset tiedot</legend>
 	
<c:if test="${dataset.name != null}">
<div class="formrow"><span class="txtlabel">Tunniste:</span> <span class="txtvalue"><c:out value="${dataset.name}"/></span></div>
<form:hidden path="name"/> 
</c:if>

<div class="formrow"><form:label path="path">Hakemistopolku:</form:label>
<form:input required="required" pattern="(/([a-z]|[A-Z]|[-_]|[0-9])+)+" size="50" path="path"/>
</div>
<form:errors cssClass="form_error" path="path"/>
<p class="infolabel">Tuotteen hakemisto, esim. /tuotteet/maastotietokanta</p>

<div class="formrow"><form:label cssClass="optional" path="fileIdentifier">Metatietotunniste:</form:label>
  <form:input placeHolder="xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx" size="50" path="fileIdentifier"/>
</div>  
<p class="infolabel">Tuotteen yksilöivä tunniste paikkatietohakemistossa (fileIdentifier)</p>

<form:errors cssClass="form_error" path="fileIdentifier"/>

</fieldset>

<fieldset><legend>Käyttöliittymässä näytettävä nimi</legend>

<div class="formrow"><form:label path="translatedTitles[fi]">Nimi (suomeksi):</form:label>
<form:input path="translatedTitles[fi]" required="required" size="50"/>
</div>
<div class="formrow"><form:label path="translatedTitles[sv]">Nimi (ruotsiksi):</form:label>
<form:input path="translatedTitles[sv]" size="50"/>
</div>
<div class="formrow"><form:label path="translatedTitles[en]">Nimi (englanniksi):</form:label>
<form:input path="translatedTitles[en]" size="50"/>
</div>
</fieldset>

<fieldset><legend>INSPIRE-latauspalvelun tiedot</legend>

<c:forEach items="${dataset.spatialObjectTypes}" var="spatialObjectTypes" varStatus="rc" begin="0">
<div class="sot_template formrow"><form:label cssClass="optional" path="spatialObjectTypes[${rc.index}].uri">Kohdeluokka (URI):</form:label>
<form:input type="url" path="spatialObjectTypes[${rc.index}].uri" size="60"/>
<a data-field=".sot_template" class="delete" href="#">POISTA</a>
</div>
</c:forEach>

<c:if test="${empty dataset.spatialObjectTypes}">
<div class="sot_template formrow"><form:label path="spatialObjectTypes[0].uri">Kohdeluokka (URI):</form:label>
<form:input type="url" cssClass="optional"  path="spatialObjectTypes[0].uri" size="60"/>
<a data-field=".sot_template" class="delete" href="#">POISTA</a>
</div>
</c:if>

<a style="display: block; clear: both;" id="add_sot" href="#">LISÄÄ KOHDELUOKKA</a>
<p class="infolabel">INSPIRE-latauspalvelussa näytettävät tietotuotteen kohdeluokat.</p>

<div class="formrow"><form:label cssClass="optional" path="spatialDatasetIdentifierCode">Resurssitunniste:</form:label>
<form:input path="spatialDatasetIdentifierCode" size="60"/>
</div>

<div class="formrow"><form:label cssClass="optional" path="spatialDatasetIdentifierNamespace">Nimiavaruus:</form:label>
<form:input value="http://www.maanmittauslaitos.fi" type="url" path="spatialDatasetIdentifierNamespace" size="60"/>
</div>

<p class="infolabel">Tietotuotteen yksilöivä resurssitunniste (Spatial Dataset Identifier).</p>
</fieldset>

<fieldset><legend>Julkaisu</legend>
<div class="formrow">
<form:label path="licence">Lisenssi:</form:label>
<form:select path="licence">
<form:options items="${lisences}" itemLabel="description"/>
</form:select>
</div>

</fieldset>

<div class="formsinglerow"><form:checkbox disabled="${empty dataset.versions}" path="published" label="Julkaistu"/></div>
 <p class="infolabel">Tuote voidaan julkaista, kun sille on määritelty vähintään yksi tuoteversio.</p>

<div class="submitrow"><input type="submit" value="Tallenna"/></div>

</form:form>

</div>

<script type="text/javascript">
$(document).ready(function() {

    $("#path").focus();
	
 	$("#add_sot").click(function(event) {
		event.preventDefault();
		var copy = $('.sot_template:first').clone();
		var name = $(".sot_template").length;
		var nextNum = new Number(name);
		var select = copy.find('input');
		select.prop('value','');
		select.prop('name','spatialObjectTypes[' + nextNum + '].uri');
		select.prop('id','spatialObjectTypes' + nextNum + '.uri');
		copy.find("label").prop('for','spatialObjectTypes' + nextNum + '.uri');
		$(this).before(copy);
		
	});
 		
	$('body').on('click', 'a.delete', function(event) {
		event.preventDefault();
		var elem = $(this).data('field');
		var numElems = $(elem).length;
		if (numElems == 1) {
			$(this).parent().find('input').prop('value','');	
		} else if (numElems > 1) {
			$(this).parent().remove();
		}
	});
			
});
</script>


<jsp:include page="footer.jsp"/>