<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="error_header.jsp"/>

<h2><spring:message code="error_internal_server"/></h2>

<p><spring:message code="error_internal_server_description"/></p>

<jsp:include page="error_footer.jsp"/>