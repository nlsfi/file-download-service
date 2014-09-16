<%@ page language="java" contentType="text/html; charset=UTF-8" session="false"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<jsp:include page="header.jsp">
	<jsp:param name="page" value="Oikeudet"/>
</jsp:include>

<div class="container">

<h2>Käyttöoikeuksien hallinta</h2>

<c:if test="${not empty errorMessage}">
<div class="form_error"><p><c:out value="${errorMessage}"/></p></div>
</c:if>

<form method="get"> 
<label for="tunnus">Hae käyttäjätunnuksella:</label>
<input id="tunnus" name="name" placeholder="käyttäjätunnus" required type="text">
<input type="submit" value="Hae"/>
</form>

<c:if test="${not empty userInformation}">
<h3>Käyttäjätiedot</h3>


<form:form method="POST" class="permissions" modelAttribute="permissions">
<form:hidden path="uid"/>

<table class="listing">
<tbody>
<c:forEach items="${userInformation}" var="attr" varStatus="idx">
	<tr class="${idx.index % 2 == 0 ? 'odd' : 'even'}">
	 <td><spring:message code="ldap.${attr.key}"/></td>
	 <td><c:out value="${attr.value}"/></td>
	 </tr>
</c:forEach>
</tbody>
</table>

<h3>Tiedostopalvelun käyttöoikeudet</h3>

	<div id="prow" class="formrow rowtmpl">
	<input type="text" size="70">
	<input type="hidden" value="READ">
	<a class="delete" href="#">POISTA</a>
	</div>

<c:forEach items="${permissions.permissions}" var="p" varStatus="idx">
  <div id="prow${idx.index}" class="formrow">
    <form:input path="permissions[${idx.index}].path" size="70"/>
    <a class="delete" href="#">POISTA</a>
  </div>
</c:forEach>

<a class="clear-block" id="add" href="#">LISÄÄ KÄYTTÖOIKEUS</a>

<p>
<div class="formrow">
<%-- <input type="submit" value="Peruuta"> --%>
<input type="submit" value="Tallenna">
</div>

</form:form>
</c:if>

</div>

<script>
$(document).ready(function() {
	
	$("#tunnus").focus();
	
	$('body').on('click', 'a.delete', function(event) {
		event.preventDefault();
		
		// id of removed row and parse int prow0 -> 0
		var currentId = $(this).parent().attr('id');
		currentId = currentId.replace('prow','');

		var rowcount=$('.row').length;
		$(this).parent().remove();
			
	 	for (i=currentId;i<rowcount;i++) {
	 		    var newpos = i;
	 		    newpos--;
	 			 		
				$('#prow'+i).prop('id','prow'+newpos).prop('class','formrow');
				$('#permissions'+i+'\\.perm').prop('id','permissions'+newpos+'.perm').prop('name','permissions['+newpos+'].perm');   
				$('#permissions'+i+'\\.path').prop('id','permissions'+newpos+'.path').prop('name','permissions['+newpos+'].path');

		}

	});
	
    $("#add").click(function(event) {
        event.preventDefault();
        var num = $('.formrow').length;
        var nextNum = new Number(num);
        var newRow = $('#prow').clone().prop('id', 'prow' + nextNum);
        newRow.find('select').prop('id','permissions' + nextNum + ".perm").attr('name','permissions[' + nextNum + "].perm");
        newRow.find('input[type=text]').val('').prop('id','permissions' + nextNum + ".path").prop('name','permissions[' + nextNum + "].path");
        newRow.find('select').find('option:first').prop('selected', 'selected');
        newRow.removeClass('rowtmpl');
        $('#add').before(newRow);
    });

});
</script>

<jsp:include page="footer.jsp"/>