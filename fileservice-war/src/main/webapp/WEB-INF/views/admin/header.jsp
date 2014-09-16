<%@ page language="java" contentType="text/html; charset=UTF-8"
     session="false" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>Tiedostopalvelun hallinta</title>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/ext.ui.20130321.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/admin.20130321.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/js/apprise/apprise.min.css"/>">
<script type="text/javascript" src="<c:url value="/resources/js/jquery/jquery-1.9.1.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/apprise/apprise-1.5.min.js"/>"></script>
</head>
<body>
<div id="headerWrapper">

<div id="logo">
<img alt="Maanmittauslaitos" src="<c:url value="/resources/img/logo_fi.png"/>"> 
<h1>Tiedostopalvelu</h1>
</div>

     <div id="navigation">
       <ul class="main-menu"><c:forEach items="${nav.pages}" var="page"><c:choose><c:when test="${page.name eq param.page}"><li class="active"><span><a href="<c:url value="${page.path}"/>"><c:out value="${page.name}"/></a></span></li></c:when><c:otherwise><li class="menu-3"><span><a href="<c:url value="${page.path}"/>"><c:out value="${page.name}"/></a></span></li></c:otherwise></c:choose></c:forEach></ul>
</div>              
</div> 

<!-- 
<div class="mainMenuContainer">

<div class="menu">
<ul class="navi">
<c:forEach items="${nav.pages}" var="page">
	<c:choose>
		<c:when test="${page.name eq param.page}">
		<li><span class="active"><c:out value="${page.name}"/></span></li>	
		</c:when>
		<c:otherwise>
		<li><span class="passive"><a href="<c:url value="${page.path}"/>"><c:out value="${page.name}"/></a></span></li>
		
		</c:otherwise>
	</c:choose>
	
	<%--<li><span class="active"><c:out value="${page.name}"/></span></li> --%>
</c:forEach>
</ul>
</div> 
</div>-->

<%-- <div id="headerbar" class="headerbar">
 --%>

<div id="content">

<%-- <img alt="Maanmittauslaitos" src="<c:url value="/resources/img/mml_logo.png"/>">
<h1>Tiedostopalvelu</h1> --%>


