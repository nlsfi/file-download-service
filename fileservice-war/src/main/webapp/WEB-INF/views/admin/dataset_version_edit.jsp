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
<a data-field=".format_template" class="delete" href="#">POISTA</a>
</div>
</c:if>

<a style="display: block; clear: both;" class="add" id="add_format" href="#">LISÄÄ TIEDOSTOMUOTO</a>

</fieldset>

<fieldset id="crs_selection"><legend>Koordinaattijärjestelmät ja karttalehtijaot</legend>
<c:forEach items="${datasetVersion.gridDefinitions}" var="gridDef" varStatus="rc" begin="0">
<div class="crs_template formrow">
    <form:label class="crs" path="gridDefinitions[${rc.index}].crs">Koordinaattijärjestelmä :</form:label>

<select class="crs" id="gridDefinitions${rc.index}.crs" name="gridDefinitions[${rc.index}].crs"
    data-selected="${gridDef.crs}">
</select>

<form:label class="gridSize" path="gridDefinitions[${rc.index}].gridSize">Lehtijaon mittakaava :</form:label>
<select class="gridSize" data-selected="${gridDef.gridSize}" id="gridDefinitions${rc.index}.gridSize" name="gridDefinitions[${rc.index}].gridSize">
</select>
<a data-field=".crs_template" class="delete" href="#">POISTA</a>
</div>
</c:forEach>
<c:if test="${empty datasetVersion.gridDefinitions}">
    <div class="crs_template formrow">
        <form:label class="crs" path="gridDefinitions[0].crs">Koordinaattijärjestelmä :</form:label>

    <form:select class="crs" path="gridDefinitions[0].crs">
    </form:select>

    <form:label class="gridSize" path="gridDefinitions[0].gridSize">Lehtijaon mittakaava :</form:label>
    <form:select class="gridSize" path="gridDefinitions[0].gridSize">
    </form:select>
    <a data-field=".crs_template" class="delete" href="#">POISTA</a>
    </div>
</c:if>

<a style="display: block; clear: both;" class="add" id="add_crs" href="#">LISÄÄ KOORDINAATTIJÄRJESTELMÄ</a>
</fieldset>

<div class="submitrow"><input type="submit" value="Tallenna"/></div>
</form:form>

</div>

<script type="text/javascript">
$(document).ready(function() {
		
    var gridDefinitionsJSON = '<c:out escapeXml="false" value="${crsDefinitionsJSON}"/>';
    var gridDefinitions = JSON.parse(gridDefinitionsJSON);
    
    var crsElements = $('select.crs');
    $.each(crsElements, function(i, crs) {
        var jCrs = $(crs);
        var currentCrs = jCrs.data('selected');
        var options = jCrs.find('option');
        if (options.length == 0) {
            $.each(gridDefinitions, function(i, gd) {
                var option = $('<option>' + gd.inspireLabel + '</option>');
                option.attr('value', gd.crsId);
                if (gd.crsId == currentCrs) {
                    option.attr('selected','selected');
                }
                jCrs.append(option);
                options.push(option);
            });
        }
           
        if (!currentCrs) {
            currentCrs = options[0].attr('value');
        }
        
        var grids = gridDefinitions[currentCrs].grids;
        var gridSelect = jCrs.nextAll("select");
        var currentGrid = gridSelect.data('selected');
        for (var p in grids) {
   	        if (grids.hasOwnProperty(p)) {
                var option = $('<option>' + grids[p].label + '</option>');
                option.attr('value', grids[p].gridSize);
                if (grids[p].gridSize == currentGrid) {
                    option.attr('selected','selected');
                } 
                gridSelect.append(option);
            }    
        }      
    });
    
    var addCrsLink = $('#add_crs');
    addCrsLink.data('disable-on', Object.keys(gridDefinitions).length);
    if (crsElements.length >= addCrsLink.data('disable-on')) {
        addCrsLink.hide();
    }
   
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
            var addLink = $(this).parents('fieldset').find('a.add');
			$(this).parent().remove();
            
            if (addLink) {
                var disableOn = addLink.data('disable-on');
                if (disableOn && numElems - 1 < disableOn) {
                    addLink.show();
                }
            }
        }
	});
    
    $("#crs_selection").on('change', "select.crs", function() {
        var grids = gridDefinitions[this.value].grids;
        var gridSelect = $(this).nextAll("select");
        gridSelect.empty();
        for (var p in grids) {
   	        if (grids.hasOwnProperty(p)) {
                var option = $('<option>' + grids[p].label + '</option>');
                option.attr('value', grids[p].gridSize); 
                gridSelect.append(option);
            }    
        }      
    });
    
    $("#add_crs").click(function(event) {
		event.preventDefault();
		var copy = $('.crs_template:first').clone();
		var name = $(".crs").length;
		var nextNum = new Number(name);

		var select = copy.find("select.crs");
		select.prop('name','gridDefinitions[' + nextNum + ']\.crs');
		select.prop('id','gridDefinitions' + nextNum + '\.crs');
		copy.find("label.crs").prop('for','gridDefinitions' + nextNum + '\.crs');
        
		var selectGrid = copy.find("select.gridSize");
		selectGrid.prop('name','gridDefinitions[' + nextNum + ']\.gridSize');
		selectGrid.prop('id','gridDefinitions' + nextNum + '\.gridSize');
		copy.find("label.gridSize").prop('for','gridDefinitions' + nextNum + '\.gridSize');
        
        $(this).before(copy);
        
        if (nextNum >= $(this).data('disable-on')) {
            $(this).hide();
        }
	
	});
		
});
</script>


<jsp:include page="footer.jsp"/>