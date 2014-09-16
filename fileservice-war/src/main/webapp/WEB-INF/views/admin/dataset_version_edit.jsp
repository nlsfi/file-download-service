<%@ page language="java" contentType="text/html; charset=UTF-8"  session="false" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<jsp:include page="header.jsp">
	<jsp:param name="page" value="Tuotteet"/>
</jsp:include>

<div class="container">

<h2>Tuotteiden hallinta</h2>

<ul class="breadcrumb">
<li><a href="<c:url value="/hallinta/tuotteet"/>">tuotelistaus</a> <span class="divider">/</span></li>
<li><a href="<c:url value="/hallinta/tuotteet/${datasetName}"/>">${dataset.translatedTitles['fi']}</a> <span class="divider">/</span></li>
<li class="active">${datasetVersion.translatedTitles['fi']}</li>
</ul>

<h2><c:out value="${dataset.translatedTitles['fi']}"/><c:if test="${datasetVersion.name != null}">, <c:out value="${datasetVersion.translatedTitles['fi']}"/></c:if><c:if test="${datasetVersion.name == null}"> - Lisää tuoteversio</c:if></h2>

<c:url value='/hallinta/tuotteet/${datasetName}/versio' var="updateUrl"/>
<form:form modelAttribute="datasetVersion" action="${updateUrl}">
<fieldset><legend></legend>
<div class="formrow"><form:label path="name">Tunniste:</form:label><form:input pattern="([a-z]|[0-9]|[_-])+" required="required" path="name" size="50"/></div>
<p class="infolabel">Käytä tunnisteena tuoteversion hakemiston nimeä, esim "kaikki".</p>
<form:errors cssClass="form_error" path="name"/>
</fieldset>

<fieldset><legend>Käyttöliittymässä näytettävä nimi</legend>
<div class="formrow"><form:label path="translatedTitles[fi]">Nimi (suomeksi):</form:label> <form:input path="translatedTitles[fi]" required="required" size="50"/></div>
<div class="formrow"><form:label path="translatedTitles[sv]">Nimi (ruotsiksi):</form:label> <form:input path="translatedTitles[sv]" size="50"/></div>
<div class="formrow"><form:label path="translatedTitles[en]">Nimi (englanniksi):</form:label> <form:input path="translatedTitles[en]" size="50"/></div>
</fieldset>

<%--
<fieldset><legend>Karttatason tiedot</legend>
<div class="formrow"><form:label cssClass="optional" path="wmsLayer">WMS karttataso</form:label><form:input path="wmsLayer" size="20"/></div>
<div class="formrow"><form:label cssClass="optional" path="wmsMinScale">WMS minScale</form:label><form:input type="number" path="wmsMinScale" size="20"/></div>
<div class="formrow"><form:label cssClass="optional" path="wmsMaxScale">WMS maxScale</form:label><form:input type="number" path="wmsMaxScale" size="20"/></div>
</fieldset>
--%>

<fieldset><legend>Tiedostomuodot</legend>



<c:forEach items="${datasetVersion.formats}" var="format" varStatus="rc" begin="0">
<div class="format_template formrow"><form:label path="formats[${rc.index}]">Tiedostomuoto :</form:label>
<form:select class="format" path="formats[${rc.index}]">
	<form:options items="${distributionFormats}"/>
</form:select>

<a data-field=".format" class="delete" href="#">POISTA</a>
</div>
</c:forEach>

<c:if test="${empty datasetVersion.formats}">
<div class="format_template formrow"><form:label path="formats[0]">Tiedostomuoto :</form:label>
<form:select class="format" path="formats[0]">
	<form:options items="${distributionFormats}"/>
</form:select>
<a data-field=".format" class="delete" href="#">POISTA</a>
</div>
</c:if>

<a style="display: block; clear: both;" id="add_format" href="#">LISÄÄ TIEDOSTOMUOTO</a>

</fieldset>

<fieldset><legend>Koordinaattijärjestelmä ja karttalehtijako</legend>
<div class="formrow"><form:label path="gridDefs[0].crs">Koordinaattijärjestelmä :</form:label>

<form:select path="gridDefs[0].crs"><form:option name="etrs-tm35fin" value="etrs-tm35fin"/></form:select></div>

<div class="formrow">
<form:label path="gridDefs[0].gridSize">Lehtijaon mittakaava :</form:label>
<form:select path="gridDefs[0].gridSize">
<form:option value="None" label="Ei lehtijakoa"/>
<form:option value="3x3" label="3 km x 3 km (1:5000)"/>
<form:option value="6x6" label="6 km x 6 km (1:10000)"/>
<form:option value="12x12" label="12 km x 12 km (1:25000)"/>
<form:option value="12x24" label="12 km x 24 km (1:25000)"/>
<form:option value="24x24" label="24 km x 24 km (1:50000)"/>
<form:option value="24x48" label="24 km x 48 km (1:50000)"/>
<form:option value="48x48" label="48 km x 48 km (1:100000)"/>
<form:option value="48x96" label="48 km x 96 km (1:100000)"/>
<form:option value="96x96" label="96 km x 96 km (1:200000)"/>
<form:option value="96x192" label="96 km x 192 km (1:200000)"/>
</form:select></div>
<%--
<div class="formrow"><form:label path="gridDefs[0].gridScale">Lehtijaon mittakaava :</form:label> <form:input path="gridDefs[0].gridScale"/></div>
<div class="formsinglerow"><form:checkbox path="singleFile" label="Koko Suomi yhdessä tiedostossa"/></div> --%>
</fieldset>

<div class="submitrow"><input type="submit" value="Tallenna"/></div>
</form:form>

</div>

<script type="text/javascript">
$(document).ready(function() {
		
    $("#name").focus();
	
	$("#add_format").click(function(event) {
		event.preventDefault();
		var copy = $('.format_template:first').clone();
		var name = $(".format").length;
		var nextNum = new Number(name);
		var select = copy.find("select");
		select.prop('name','formats[' + nextNum + ']');
		select.prop('id','formats[' + nextNum + ']');
		copy.find("label").prop('for','formats' + nextNum);
		$(this).before(copy);
	});
 	
	$('body').on('click', 'a.delete', function(event) {
		event.preventDefault();
		var elem = $(this).data('field');
		var numElems = $(elem).length;
		if (numElems > 1) {
			$(this).parent().remove();
		}
	});
		
});
</script>


<jsp:include page="footer.jsp"/>