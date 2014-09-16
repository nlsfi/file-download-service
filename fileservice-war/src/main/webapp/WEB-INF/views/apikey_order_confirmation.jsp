<%@ page language="java" contentType="text/html; charset=UTF-8" session="false" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<link href="<c:url value="/resources/css/ext.ui.20130321.css"/>" rel="stylesheet" type="text/css"> 
<title><spring:message code="avoindata_tiedostopalvelu_title"/></title>
</head>
<body>

<div id="content">

<div id="headerbar" class="headerbar">
<a href="<spring:message code="service_frontpage_url"/>">
<img alt="<spring:message code="organisation"/>" src="<c:url value="/resources/img/"/><spring:message code="logo.image.file"/>"/></a>
<h1><spring:message code="avoindata_tiedostopalvelu_title"/></h1>

</div>

<div class="container">

<%--<div id="language-choices">
<a class="${rc.locale.language == 'fi' ? 'current_lang' : ''}" href="?lang=fi">Suomeksi</a>
<a class="${rc.locale.language == 'sv' ? 'current_lang' : ''}"href="?lang=sv">På svenska</a>
<a class="${rc.locale.language == 'en' ? 'current_lang' : ''}"href="?lang=en">in English</a>
</div> --%>

<h2><spring:message code="mtp_tilaus_otsikko"/></h2>

<p><spring:message code="mtp_tilaus_vahvistus"/></p>

<p><a href="<spring:message code="mtp_rajapintakuvaus_url"/>"><spring:message code="mtp_rajapintakuvaus_otsikko"/></a></p>

</div>

</div>

</body>
</html>