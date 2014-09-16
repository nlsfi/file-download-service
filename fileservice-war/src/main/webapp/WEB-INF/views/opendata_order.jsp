<%@ page language="java" contentType="text/html; charset=UTF-8"
     session="false" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!doctype html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title><spring:message code="avoindata_tiedostopalvelu_title"/></title>
<script type="text/javascript" src="<c:url value="/resources/js/nls/showHide.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/ext.ui.20130321.css"/>">
</head>

<body>

<div id="content">

<div id="headerbar" class="headerbar">

<a href="<spring:message code="service_frontpage_url"/>">
<img alt="Maanmittauslaitos" src="<c:url value="/resources/img/"/><spring:message code="logo.image.file"/>"/></a>
<h1><spring:message code="avoindata_tiedostopalvelu_title"/></h1>
</div>

<div class="container">

<p><a href="<spring:message code="opendata_licence_url"/>"><spring:message code="opendata_licence"/></a></p>

<c:forEach var="dataset" items="${datasets}" varStatus="loopStatus">
	<h2><c:out value="${dataset.translatedTitles[rc.locale.language]}"/></h2>
		<c:if test="${not empty dataset.versions}">
			<c:forEach var="datasetVersion" items="${dataset.versions}"> 
<c:if test="${not empty datasetVersion.nodes}">

<h3><c:out value="${datasetVersion.translatedTitles[rc.locale.language]}"/></h3>

<table class="listing">
<thead>
<tr>
	<th class="firstcol"><spring:message code="ui.name"/></th>
	<th class="secondcol"><spring:message code="ui.lastModified"/></th>
	<th class="thirdcol"><spring:message code="ui.length"/></th>
	<th class="fourthcol"><spring:message code="ui.mimeType"/></th>
	<th class="fifthcol"><spring:message code="ui.metadata"/></th>
</tr>
</thead>
<tbody>		
				
				<c:forEach var="childNode" items="${datasetVersion.nodes}" varStatus="rs">
					<tr class="${rs.index % 2 == 0 ? 'odd' : 'even'}">
							<td class="name"><a href="<spring:url value="/tilauslataus${childNode.path}"><spring:param name="token" value="${tokenVariable}"/></spring:url>"><c:out value="${childNode.name}"/></a></td>
							<td class="detail"><fmt:formatDate value="${childNode.lastModified}" pattern="yyyy-MM-dd HH:mm"/></td>
					        <td class="detail"><c:out value="${childNode.lengthStr}"/></td>
					        <td class="detail"><c:out value="${childNode.mimeType}"/></td>
					        <td class="detail">
					      		<c:if test="${not childNode.folder && not empty childNode.properties}">
					      			<a href="javascript:toggleMetaVisibility('${childNode.name}')"><spring:message code="metatiedot"/></a></c:if></td>
					</tr>
					
					    <c:if test="${not empty childNode.relatedNodes}">
					       <c:forEach var="relatedFiles" items="${childNode.relatedNodes}">
					       <tr class="${rs.index % 2 == 0 ? 'odd' : 'even'}">
					        	<td class="name"><a href="<spring:url value="/tilauslataus${relatedFiles.path}"><spring:param name="token" value="${tokenVariable}"/></spring:url>"><c:out value="${relatedFiles.name}"/></a></td>
								<td class="detail"><fmt:formatDate value="${relatedFiles.lastModified}" pattern="yyyy-MM-dd HH:mm"/></td>
					      		<td class="detail"><c:out value="${relatedFiles.lengthStr}"/></td>
					      		<td class="detail"><c:out value="${relatedFiles.mimeType}"/></td>
					      		<td class="detail"></td>
					      	</tr>       
					      	</c:forEach>
					    </c:if>
					<c:if test="${not childNode.folder && not empty childNode.properties}">
						<tr id="${childNode.name}" style="display:none;">
							<td class="${rs.index % 2 == 0 ? 'odd' : 'even'} noborder" colspan="5">
							<div> 
	    						<ul class="no_top_border no-style properties ${rs.index % 2 == 0 ? 'odd' : 'even'}">
	                  					<c:forEach items="${childNode.properties}" var="property">
	                        					<li><span class="property_title"><spring:message code="${property.name}" text="${property.name}"/></span><span class="property_value="><c:out value="${property.value}"/></span></li>
	                					</c:forEach>
                					
                				</ul>
                				</div>
							</td>
						</tr>
					</c:if>
					
				</c:forEach>
				
				</tbody>
</table>
</c:if>		

			</c:forEach>
		</c:if>
		
	

	
</c:forEach>

</div>

<div id="language-choices">
<a class="${rc.locale.language == 'fi' ? 'current_lang' : ''}" href="?lang=fi">Suomeksi</a>
<a class="${rc.locale.language == 'sv' ? 'current_lang' : ''}" href="?lang=sv">På svenska</a>
<a class="${rc.locale.language == 'en' ? 'current_lang' : ''}" href="?lang=en">in English</a>
</div>

</div>
</body>
</html>