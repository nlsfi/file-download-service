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

<%-- don't use <c:url because that rewrites the url with JSESSIONID.. --%>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/ext.ui.20130321.css"/>
</head>
<body>

<div id="content">

<div id="headerbar" class="headerbar">
<a href="<spring:message code="service_frontpage_url"/>">
<img alt="<spring:message code="organization"/>" src="${pageContext.request.contextPath}/resources/img/<spring:message code="logo.image.file"/>"/></a>
<h1><spring:message code="tiedostopalvelu_title"/></h1>
</div>