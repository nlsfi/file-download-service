<%@ page language="java" contentType="text/html; charset=UTF-8"  session="false" 
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<jsp:include page="header.jsp">
	<jsp:param name="page" value="Tiedostot"/>
</jsp:include>

<div class="container">

<h2>Tiedostonhallinta</h2>

<ul class="breadcrumb">
<li><a href="<spring:url value="/hallinta/tiedostot/"/>">[alkuun]</a> <span class="divider">/</span>
<c:forEach items="${node.pathComponents}" var="path" varStatus="index">
	<c:set var="relpath" value="${relpath}/${path}" scope="request"/>
	<c:choose>
		<c:when test="${not index.last}">
		<li><a href="<spring:url value="/hallinta/tiedostot{path}"><spring:param name="path" value="${relpath}"/></spring:url>"><c:out value="${path}"/></a> <span class="divider">/</span> 
		</c:when>
		<c:otherwise><li class="active"><c:out value="${path}"/></li></c:otherwise>
	</c:choose>
</c:forEach>
</ul>

<div id="nodeinfo">

<h2><c:out value="${node.name}"/></h2>

<c:if test="${node.type == 'nt:file'}">
<div class="opbox">
<form class="inline" action="<spring:url value="/hallinta/lataus${node.path}"/>"><button class="action_btn button action">Lataa</button></form>

<%-- Deletion doesn't work due to ModeShape bug so we hide this for now..
<form class="inline" method="post" action="<spring:url value="/hallinta/tiedostot/delete${node.path}"/>">
<input type="hidden" name="_method" value="delete"/><button class="action_btn action delete">Poista</button></form>--%>
</div>
</c:if>

<c:if test="${not empty node.childNodes}">
<table class="listing">
<thead>
	<tr>
		<th class="firstcol"><spring:message code="ui.name"/></th>
		<th class="secondcol"><spring:message code="ui.lastModified"/></th>
		<th class="thirdcol"><spring:message code="ui.length"/></th>
		<th class="fourthcol"><spring:message code="ui.mimeType"/></th>
	</tr>
</thead>
<c:forEach var="childNode" varStatus="rs" items="${node.childNodes}" >
<tr class="${rs.index % 2 == 0 ? 'even' : 'odd'}">
 <c:choose>
 	<c:when test="${childNode.folder}">
 		<td class="name" colspan="4">
 		<a href="<spring:url value="/hallinta/tiedostot{path}"><spring:param name="path" value="${childNode.path}"/></spring:url>"><c:out value="${childNode.name}"/></a>
 		</td>
 	</c:when>
 	<c:otherwise>
 	<td class="name">
		 <a href="<spring:url value="/hallinta/tiedostot{path}"><spring:param name="path" value="${childNode.path}"/></spring:url>"><c:out value="${childNode.name}"/></a>
		 </td>
		 <td class="detail"><fmt:formatDate value="${childNode.lastModified}" pattern="yyyy-MM-dd HH:mm"/></td>
         <td class="detail"><c:out value="${childNode.lengthStr}"/></td>
         <td class="detail"><c:out value="${childNode.mimeType}"/></td>
	</c:otherwise> 	
 </c:choose>
</tr>
</c:forEach>
</table>
</c:if>

<%--
<li id="prow" class="rowtmpl">
<span class="attr_title">
	<select>
	<c:forEach items="${properties}" var="property">
		<option value="${property.key}"><c:out value="${property.value}"/></option>
	</c:forEach>
	</select>
</span>
<span class="attr_values">
	<input size="50" type="text">
	<a class="delete" href="${node.path}/${property.name}">poista</a>
</span>
</li>

<li id="multivaluedrow" class="rowtmpl">
	<span class="attr_title">&nbsp;</span>
	<span class="attr_values">
		<input size="50" type="text">
		<a class="delete" href="${node.path}/${property.name}">poista</a>
	</span>
</li>
--%>

<c:if test="${node.type eq 'nt:file'}">

<h3>Metatiedot</h3>

<ul class="no-style properties">
<li><span class="property_title">Koko:</span><span class="property_value"><c:out value="${node.lengthStr}" default="-"/></span></li>
<li><span class="property_title">Tiedostomuoto:</span><span class="property_value"><c:out value="${node.mimeType}" default="-"/></span></li>
<li><span class="property_title">Muokkaaja:</span><span class="property_value"><c:out value="${node.createdBy}" default="-"/></span></li>
<li><span class="property_title">Päivitetty:</span><span class="property_value"><fmt:formatDate value="${node.lastModified}" pattern="yyyy-MM-dd HH:mm"/></span></li>

	<c:forEach items="${node.properties}" var="property"  varStatus="rc">
			<c:choose>
				<c:when test="${property.multiple}">
				<c:forEach items="${property.values}" var="value" varStatus="rc2">
					<li><span class="property_title"><c:out value="${property.name}"/></span><span class="property_value"><c:out value="${value}"/></span>				
				</c:forEach>
				</c:when>
				<c:otherwise>
				<li><span class="property_title"><c:out value="${property.name}"/></span><span class="property_value"><c:out value="${property.value}"/></span>
						</c:otherwise>
			</c:choose>	
		</c:forEach>

</ul>

</c:if>
	<%--
<div class="opbox">
<a class="button" href="<c:url value="/admin/aineistot/lisaa"/>">Lataa tiedostoja</a>
<form method="post" action="<c:url value="/api/sharepaths"/>"><input type="hidden" name="path" value="${node.path}"/>
<button data-name="path" data-value="${node.path}" id="addshare">Luo latauslinkki</button>
</form> 
<!--  <a class="button" id="addshare" href="<c:url value="/api/files/sharepaths"/>">Luo latauslinkki</a> -->
</div> --%>
</div>

</div>

	<%--
<script type="text/javascript">
$(document).ready(function() {
		
	$("button.delete").click(function(event) {
		event.preventDefault();
		var path = $(this).parent().attr('action');
		apprise('Haluatko varmasti poistaa tiedoston <span class="dialog.name">' + path + '</span>?',
			{'verify': true, 'textYes' : 'Kyllä', 'textNo' : 'Ei'}, function(r) {
				if (r) {
					deleteItem(path, function() {
						$("#nodeinfo").hide();
					}); 
				}
			});
	});
	
	function displayError(message) {
		$("#ajaxError").html(message).show();
	}
	
	function deleteItem(itemPath, successFunction) {
		$.ajax({
			type: 'POST',
			url : itemPath,
			data: { '_method' : 'delete' },
			success : successFunction,
			error : function(jqXHR, textStatus, errorThrown) {
				displayError(errorThrown);
				return false;
			}
		});
	}
	

	$("body").on('click','a.delete', function(event) {
		event.preventDefault();
		if ($(this).prev().data('isnew') != true) {
			var href = $(this).attr('href');
			var container = $(this).parent().parent();
			apprise('Haluatko varmasti poistaa attribuutin <span class="dialog.name">' + href + '</span>?',
				{'verify': true, 'textYes' : 'Kyllä', 'textNo' : 'Ei'}, function(r) {
					if (r) {
						deleteItem(href, function() {
							container.fadeOut('slow').remove();
						});
					}
				});
		} else {
			deleteItem($(this).attr('href'), $(this).parents('div.row'));
		}
		
	});
	
	$("#add").click(function(event) {
		event.preventDefault();
		var nextNum = $('form > li').length;		
		var newRow = $('#prow').clone().attr('id', 'prow' + nextNum).attr('class','row');
		newRow.find('input[type=text]').val('').attr('name','properties[' + nextNum + '].values[0]').attr('data-isnew', 'true');
		newRow.find('select').attr('name','properties[' + nextNum + '].name');
		$('#add').parent().before(newRow);
	});
	
	$("a.add").live('click', function(event) {
		event.preventDefault();
		var index = $(this).parents('li').attr('data-pname');
		var valueIndex = $("li[data-pname="+ index + "]").length;
		var newRow = $('#multivaluedrow').clone().attr('id', '').attr('class','row');
		newRow.find('input[type=text]').val('').attr('name','properties[' + index + '].values[' + valueIndex + ']').attr('data-isnew', 'true');
		$('li[data-pname="' + index + '"]').last().after(newRow);
	});
	
	$("#addshare").click(function(event) {
		event.preventDefault();
		var paramName = $(this).attr('data-name');
		var paramValue = $(this).attr('data-value');
		var url = $(this).parent().attr('action');
		$.ajax({
			url: url,
			data: { 'path' : paramValue },
			type: 'POST',
			success : function(data, textStatus, jqXHR) {
				var linkHeader = jqXHR.getResponseHeader('Link');
				var re = /<(.*)>/gi;
				var match = re.exec(linkHeader);
				if (match != null) {
					var url = match[1];
					var box = $('#actionbox');
					box.find('.url').attr('href',url).html(url);
					box.toggle();
				}
			}
		});
	
	});
	
	$("#createshare").click(function() {
		var xhr = $.ajax({
			url: '<spring:url value="/api/createshare"/>',
			type: 'POST',
			success: function(data) {
				var url = xhr.getResponseHeader('Link');
				$(".url").html(url);
				$("#uppath").attr('value',data);
				$(".notification").toggle();
				$("#hallinta_link").attr('href', '/modeshape/admin/metadata' + data);
				$("#upload_form").toggle();
			},
			error: function(jqXHR,textStatus,errorThrown) {
				$(".notification").addClass('error').html('<p>' + errorThrown + '</a></p>');
			}
		});
	}); 
});
</script>
--%>

<jsp:include page="footer.jsp"/> 