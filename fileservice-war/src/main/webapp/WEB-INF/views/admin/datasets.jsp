<%@ page language="java" contentType="text/html; charset=UTF-8" session="false" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<jsp:include page="header.jsp">
	<jsp:param name="page" value="Tuotteet"/>
</jsp:include>

<div class="container">
	
<h2>Tuotteiden hallinta</h2>

<%--<ul class="breadcrumb">
<li class="active">tuotelistaus</li>
</ul>--%>
<div class="opbox">
<a class="action_btn button" href="<c:url value="/hallinta/tuotteet/lisaa"/>">Lisää tuote</a>
</div>

<h2>Tuotteet</h2>

<c:if test="${empty datasets}">Tuotemäärittelyitä ei ole vielä lisätty.</c:if>

<ul class="no-style properties">
<c:forEach var="dataset" items="${datasets}" varStatus="loopStatus">
<li class="published_${dataset.published} ${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
<span class="title published_${dataset.published}"><c:out value="${dataset.translatedTitles[rc.locale.language]}"/></span>

<span class="oplinks"><a href="<c:url value="/hallinta/tuotteet/${dataset.name}"/>">MUOKKAA</a>

<a title="<c:out value="${dataset.translatedTitles[rc.locale.language]}"/>" class="delete" href="<c:url value="/hallinta/tuotteet/delete/${dataset.name}"/>">POISTA</a></span>

<ul class="inner"><c:if test="${empty dataset.versions}"><li class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">Ei tuoteversioita</li></c:if>
<c:forEach var="datasetVersion" items="${dataset.versions}">
	<li class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}"><c:out value="${datasetVersion.translatedTitles[rc.locale.language]}"/><span class="oplinks"><a href="<c:url value="/hallinta/tuotteet/${dataset.name}/${datasetVersion.name}"/>">MUOKKAA</a>
	<a class="delete" title="<c:out value="${datasetVersion.translatedTitles[rc.locale.language]}"/>" href="<c:url value="/hallinta/tuotteet/delete/${dataset.name}/${datasetVersion.name}"/>">POISTA</a>
	</span> </li>
</c:forEach>
<li class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}"><a href="<c:url value="/hallinta/tuotteet/${dataset.name}/lisaa"/>">LISÄÄ TUOTEVERSIO</a>
</ul></li>
	
</c:forEach>
</ul>

</div>

<script>
$(document).ready(function() {

	$("a.delete").click(function(event) {
		event.preventDefault();
		var elem = $(this);
		apprise('Haluatko varmasti poistaa tuotteen ' + $(this).prop('title') + '?',
			{'verify': true, 'textYes' : 'Kyllä', 'textNo' : 'Ei'}, function(r) {
				if (r) {
					$.ajax({
						url: elem.attr('href'),
						data: { '_method' :'DELETE' },
						type: 'POST',
						success: function() {
							elem.parent().parent().hide();
						}
					});
				}
			});
	});

});

</script>

<jsp:include page="footer.jsp"/>