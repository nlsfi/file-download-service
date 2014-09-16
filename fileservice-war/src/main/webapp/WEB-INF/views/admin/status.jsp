<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" session="false" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!doctype html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title><spring:message code="tiedostopalvelu_title"/></title>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/ext.ui.20130321.css"/>">
<style>

.status_error {
 	background-color: #ff3300;
 	color: #fff;
}

.status_ok {
  background-color: #79B643;
  color: #fff;
}

</style>
</head>
<body>

<div id="content">

<div id="headerbar" class="headerbar">

<img alt="<spring:message code="organisation"/>" src="${pageContext.request.contextPath}/resources/img/<spring:message code="logo.image.file"/>"/>
<h1><spring:message code="tiedostopalvelu_title"/></h1>
</div>

<div class="container">

<h2>Status</h2>

<h3>System</h3>

<table><tr><td><c:out value="${status.osName}"/></td><td><c:out value="${status.osVersion}"/></td><td><c:out value="${status.osArch}"/></td></tr></table>


<h3>Java VM</h3>

<table>
<tr>
<tr><td>Java version</td><td><c:out value="${status.javaVersion}"/></td></tr>
<tr><td>Java vendor</td><td><c:out value="${status.javaVendor}"/></td></tr>
<tr><td>Free memory</td><td><c:out value="${status.freeMemoryStr}"/></td></tr>
<tr><td>Total memory</td><td><c:out value="${status.totalMemoryStr}"/></td></tr>
<tr><td>Max memory</td><td><c:out value="${status.maxMemoryStr}"/></td></tr>
<tr><td>Available CPUs</td><td><c:out value="${status.availableProcessors}"/></td></tr>
<tr><td>Current thread count</td><td><c:out value="${status.threadCount}"/></td></tr>
<tr><td>Daemon thread count</td><td><c:out value="${status.daemonThreadCount}"/></td></tr>
<tr><td>Peak thread count</td><td><c:out value="${status.peakThreadCount}"/></td></tr>
</table>

<h3>ModeShape</h3>

<table>
<tr>
<c:choose>
<c:when test="${status.modeShapeOk}"><td class="status_ok">OK: JCR query succeeded</td></c:when>
<c:otherwise><td class="status_error">ERROR: <c:out value="${status.modeShapeError}"/></td></c:otherwise>
</c:choose>
</tr></table>

<h3>Database</h3>

<table><tr>
<c:choose>
<c:when test="${status.dbServerOk}"><td class="status_ok">OK: Version "<c:out value="${status.dbServerVersion}"/>" running</td></c:when>
<c:otherwise><td class="status_error">ERROR: <c:out value="${status.jdbcError}"/></td></c:otherwise>
</c:choose>
</tr>
</table>

<%--

<h3>Environment</h3>

<ul>
<c:forEach items="${status.env}" var="entry">
<li><c:out value="${entry.key}"/> : <c:out value="${entry.value}"/></li>
</c:forEach>
</ul>

 --%>
 
</div>
</div>

</body>
</html>