<%@ page language="java" contentType="text/html; charset=UTF-8" session="false" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<script type="text/javascript" src="<c:url value="/resources/js/jquery/jquery-1.9.1.min.js"/>"></script>
<link href="<c:url value="/resources/css/ext.ui.20130321.css"/>" rel="stylesheet" type="text/css"> 
<title><spring:message code="avoindata_tiedostopalvelu_title"/></title>
</head>
<body>

<div id="content" style="bowrrder: 1px solid #000;">

<div id="headerbar" class="headerbar">
<a href="<spring:message code="service_frontpage_url"/>">
<img alt="<spring:message code="organisation"/>" src="<c:url value="/resources/img/"/><spring:message code="logo.image.file"/>"/></a>
<h1><spring:message code="avoindata_tiedostopalvelu_title"/></h1>
</div>

<div class="container" style="borwrwder: 1px solid #000;">

<div id="language-choices">
<a class="${rc.locale.language == 'fi' ? 'current_lang' : ''}" href="?lang=fi">Suomeksi</a>
<a class="${rc.locale.language == 'sv' ? 'current_lang' : ''}"href="?lang=sv">På svenska</a>
<a class="${rc.locale.language == 'en' ? 'current_lang' : ''}"href="?lang=en">in English</a>
</div>

<h2><spring:message code="mtp_tilaus_otsikko"/></h2>

<p><spring:message code="mtp_tilaus_ohje1"/></p>

<p><spring:message code="mtp_tilaus_ohje2"/></p>

<h3><spring:message code="mtp_tilauslomake_otsikko"/></h3>

<c:if test="${error_msg != null}"><p class="form_error"><spring:message code="${error_msg}"/></p></c:if>

<form:form modelAttribute="customer">

<div class="formrow optional"><form:label path="firstName"><spring:message code="form.first_name"/></form:label> <form:input path="firstName" size="50"/></div>
<div class="formrow optional"><form:label path="lastName"><spring:message code="form.last_name"/></form:label> <form:input path="lastName" size="50"/></div>
<div class="formrow optional"><form:label path="organisation"><spring:message code="form.organisation"/></form:label> <form:input path="organisation" size="50"/></div>
<div class="formrow"><form:label cssErrorClass="form_error" path="email"><spring:message code="form.email"/></form:label> <form:input path="email" size="50" type="email" required="required"/>

<form:errors path="email"><span class="form_error"><spring:message code="sahkopostiosoite_virheellinen"/></span></form:errors></div>

<c:set var="checkboxlabel"><spring:message code="mtp_tilauslomake_kayttoehdot"/></c:set>
<div class="formsinglerow"><form:checkbox required="required" path="licenceAccepted" cssErrorClass="form_error" htmlEscape="false" label="${checkboxlabel}"/>
<form:errors path="licenceAccepted"><span class="form_error"><spring:message code="lisenssiehdot_hyvaksyttava"/></span></form:errors></div>
<div class="formrow"><input type="submit" value="<spring:message code="form.order"/>"/></div>

</form:form>

</div>
</div>

<script type="text/javascript">
$().ready(function() {
   $("#firstname").focus();
});
</script>

</body>
</html>