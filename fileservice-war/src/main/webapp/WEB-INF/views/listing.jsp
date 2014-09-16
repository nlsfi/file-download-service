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
<title><spring:message code="ui.title"/> | <c:out value="${node.path}"/></title>
<script type="text/javascript" src="<c:url value="/resources/js/nls/showHide.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/ext.ui.20130321.css"/>"/>
</head>
<body>

<%--<c:if test="${credentials.commonName}"><div id="login-info"><c:out value="${credentials.commonName}"/></div></c:if> --%>

<div id="content">

<div id="headerbar" class="headerbar">

<img alt="<spring:message code="organization"/>" src="<c:url value="/resources/img/"/><spring:message code="logo.image.file"/>"/>
<h1><spring:message code="tiedostopalvelu_title"/></h1>

</div>

<div class="container">

<h2><c:out value="${node.name}"/></h2>

<ul class="breadcrumb">
<li><%-- <span class="divider">/</span>--%>
<c:forEach items="${node.pathComponents}" var="path" varStatus="index">
	<c:set var="relpath" value="${relpath}/${path}" scope="request"/>
	<c:choose>
		<c:when test="${not index.last}">
		<li><a href="<spring:url value="/lataus{path}"><spring:param name="path" value="${relpath}"/></spring:url>"><c:out value="${path}"/></a> <span class="divider">></span> 
		</c:when>
		<c:otherwise><li class="active"><c:out value="${path}"/></li></c:otherwise>
	</c:choose>
</c:forEach>
</ul>

<c:if test="${not empty node.childNodes}">
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
<c:forEach var="childNode" items="${node.childNodes}" varStatus="rs">
<tr class="${rs.index % 2 == 0 ? 'odd' : 'even'}">
 <c:choose>
 	<c:when test="${childNode.folder}">
 		<td class="name" colspan="5">
 		<a href="<spring:url value="/lataus${childNode.path}"/>"><c:out value="${childNode.name}"/>/</a>
 		</td>
 	</c:when>
 	<c:otherwise>
 	<td class="name"><a href="<spring:url value="/lataus${childNode.path}"/>"><c:out value="${childNode.name}"/></a></td>
		<td class="detail"><fmt:formatDate value="${childNode.lastModified}" pattern="yyyy-MM-dd HH:mm"/></td>
        <td class="detail"><c:out value="${childNode.lengthStr}"/></td>
        <td class="detail"><c:out value="${childNode.mimeType}"/></td>
        <td class="detail">
			<c:if test="${not childNode.folder && not empty childNode.properties}">
			<a href="javascript:toggleMetaVisibility('${childNode.name}')"><spring:message code="metatiedot"/></a></c:if></td>
	</c:otherwise> 	
 </c:choose>
</tr>
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

<div id="language-choices">
<a class="${rc.locale.language == 'fi' ? 'current_lang' : ''}" href="?lang=fi">Suomeksi</a>
<a class="${rc.locale.language == 'sv' ? 'current_lang' : ''}" href="?lang=sv">På svenska</a>
<a class="${rc.locale.language == 'en' ? 'current_lang' : ''}" href="?lang=en">in English</a></div>
</div> 

</div>

</body>
</html>