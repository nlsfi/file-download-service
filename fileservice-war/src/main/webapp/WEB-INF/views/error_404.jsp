<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="error_header.jsp"/>

<h2><spring:message code="error_page_not_found"/></h2>

<p><spring:message code="error_page_not_found_description"/></p>

<jsp:include page="error_footer.jsp"/>