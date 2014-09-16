<%@ page language="java" contentType="text/html; charset=UTF-8" session="false" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<!-- 
 FOR IE11 Compatibility
 <meta http-equiv="X-UA-Compatible" content="IE=edge"> -->
<meta http-equiv="X-UA-Compatible" content="IE=10">

        <!-- module: firebug lite
        <script type="text/javascript" src="https://getfirebug.com/firebug-lite.js">
                {
                    overrideConsole: false,
                    startInNewWindow: true,
                    startOpened: true,
                    enableTrace: true
                }
        </script>
        -->

        <link href="/map-application-framework/lib/ext-4.0.7-gpl/resources/css/ext-all-gray.css" rel="stylesheet" type="text/css">
        <link href="/map-application-framework/lib/ext-4.0.7-gpl/examples/ux/css/CheckHeader.css" rel="stylesheet" type="text/css">
        <link href="/map-application-framework/resource/css/map-portlet-styles.css" rel="stylesheet" type="text/css">
        <link href="/map-application-framework/resource/css/overrided-styles.css" rel="stylesheet" type="text/css">
        <link href="/map-application-framework/resource/css/esko.css" rel="stylesheet" type="text/css">
        <link href="/map-application-framework/resource/css/openlayers-modifications.css" rel="stylesheet" type="text/css">

		<link href="<c:url value="/resources/css/ext.ui.20130321.css"/>" rel="stylesheet" type="text/css"> 

        <!-- module: Oskari clazz and module support -->
        <script type="text/javascript" src="/map-application-framework/src/mapframework/bundle/bundle.js"></script>

        <!-- module: openlayers with proj4js -->
        <script type="text/javascript" src="/map-application-framework/lib/proj4js-1.0.1/lib/proj4js-compressed.js"></script>
        <script type="text/javascript" src="/map-application-framework/lib/proj4js-1.0.1/lib/defs/EPSG3067.js" ></script>
        <script type="text/javascript" src="/map-application-framework/lib/OpenLayers/OpenLayers-2.13.1.js"></script>

        <!--  module: jquery -->
        <script type="text/javascript" src="/map-application-framework/lib/jquery/jquery-1.7.1.min.js"></script>

        <!-- module: extjs4 -->
        <script type="text/javascript" src="/map-application-framework/lib/ext-4.0.7-gpl/ext-all.js"></script>
        <script type="text/javascript" src="/map-application-framework/lib/ext-4.0.7-gpl/examples/portal/classes/PortalColumn.js"></script>
        <script type="text/javascript" src="/map-application-framework/lib/ext-4.0.7-gpl/examples/portal/classes/PortalPanel.js"></script>
        <script type="text/javascript" src="/map-application-framework/lib/ext-4.0.7-gpl/examples/portal/classes/PortalDropZone.js"></script>
        <script type="text/javascript" src="/map-application-framework/lib/ext-4.0.7-gpl/examples/portal/classes/Portlet.js"></script>
        <script type="text/javascript" src="/map-application-framework/lib/ext-4.0.7-gpl/examples/ux/statusbar/StatusBar.js"></script>
        <script type="text/javascript" src="/map-application-framework/lib/ext-4.0.7-gpl/examples/ux/data/PagingMemoryProxy.js"></script>

        <script type="text/javascript" src="/map-application-framework/src/mapframework/include-function.js"></script>
        <script type="text/javascript" src="/map-application-framework/src/mapframework/include-mmld.js"></script>

        <!-- module: app configuration -->
        <script type="text/javascript" src="/mmld/conf.js"></script>

        <!-- module: app startup -->
        <script type="text/javascript" src="/mmld/index.js"></script>
	
<title><spring:message code="avoindata_tiedostopalvelu_title"/></title>
<style type="text/css">
/* override */
#content {
	max-width: 1002px;
}
</style>
</head>
<body>

<div id="content">

<div id="headerbar" class="headerbar">
<a href="<spring:message code="service_frontpage_url"/>">
<img alt="<spring:message code="organization"/>" src="<c:url value="/resources/img/"/><spring:message code="logo.image.file"/>"/></a>
<h1><spring:message code="avoindata_tiedostopalvelu_title"/></h1>

</div>

<div id="language-choices">
<a class="${rc.locale.language == 'fi' ? 'current_lang' : 'option'}" href="?lang=fi">Suomeksi</a>
<a class="${rc.locale.language == 'sv' ? 'current_lang' : 'option'}" href="?lang=sv">På svenska</a>
<a class="${rc.locale.language == 'en' ? 'current_lang' : 'option'}" href="?lang=en">in English</a>
</div>

<%-- <div id="map-full" style="width:100%;height:100%;padding-bottom:30px; border: 1px solid #000;"></div> --%>
<div id="map-full" style="margin-top: 20px;"></div>

</div>
</body>
</html>