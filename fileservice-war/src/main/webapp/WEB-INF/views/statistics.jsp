<%@ page language="java" contentType="text/html; charset=UTF-8"  session="false" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<script type="text/javascript" src="<c:url value="/resources/js/jquery/jquery-1.7.1.min.js"/>"></script>
<link href="<c:url value="/resources/css/ext.ui.20130321.css"/>" rel="stylesheet" type="text/css"> 
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/admin.20130321.css"/>">
<title><spring:message code="avoindata_tiedostopalvelu_title"/></title>
</head>
<body>

<div id="content">

<div id="headerbar" class="headerbar">
<a href="<spring:message code="service_frontpage_url"/>">
<img alt="<spring:message code="organization"/>" src="<c:url value="/resources/img/"/><spring:message code="logo.image.file"/>"/></a>
<h1><spring:message code="tiedostopalvelu_title"/></h1>
</div>

<div class="container">

<h2>Lataustilastot</h2>
<%--
<p>Tuota päivittäin jaoteltu lataustilastoraportti Excel-taulukkona</p>

<form action="<c:url value="/tilastot/raportti.xlsx"/>">
</form>
--%>

<h3>Tilaukset yhteensä</h3>

<table class="listing">
<tbody>
<c:forEach items="${orders}" var="order" varStatus="rs">
<tr class="${rs.index % 2 == 0 ? 'odd' : 'even'}"><td><spring:message code="${order.key}"/></td><td><c:out value="${order.value}"/></td><td>
</c:forEach>
</tbody>
</table>

<h3>Ladatut tiedostot tuotteittain</h3>

<table class="listing">
<tbody>
<thead>
<tr><th>Tuote</th><th>Tuoteversio</th><th>Tiedostoja</th><th>Tavuja</th></tr>
</thead>
<c:forEach items="${statistics}" var="stat" varStatus="rs">
<tr class="${rs.index % 2 == 0 ? 'odd' : 'even'}"><td><c:out value="${stat.datasetTitle}"/></td><td><c:out value="${stat.datasetVersionTitle}"/></td>
<td><c:out value="${stat.totalDownloads}"/></td><td><c:out value="${stat.formattedBytes}"/></td></tr>
</c:forEach>
</tbody>
</table>

<p><img alt="XSLX" src="<c:url value="/resources/img/file_csv.png"/>"> <a href="<c:url value="/tilastot/kaikki.xlsx"/>">Lataa Excel-taulukkona</a></p>

</div>

</div>

</body>
</html>