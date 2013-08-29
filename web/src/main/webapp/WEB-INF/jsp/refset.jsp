<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!doctype html>
<%@page import="com.ihtsdo.snomed.model.Refset, 
    java.text.DateFormat, 
    java.text.SimpleDateFormat, 
    java.text.DecimalFormat"%>

<%!
    DecimalFormat decimalFormat = new DecimalFormat("###,###.###");
%>

<html lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
  <%@include file="head.jsp"%>
</head>
<body id="refset">
  <%@include file="heading.jsp"%>
  <div class="return">
    <a href="/refsets">Return</a>
  </div>
  <table>
    <tr>
        <td class="heading">Title:</td>
        <td><c:out value="${refset.getTitle()}"/></td>
    </tr>
    <tr>
        <td class="heading">Description:</td>
        <td><c:out value="${refset.getDescription()}"/></td>
    </tr>
   <tr>
        <td class="heading">Public Id:</td>
        <td><c:out value="${refset.getPublicId()}"/></td>
    </tr>
  </table>
  <div class="edit">
    <a href='/refset/<c:out value="${refset.getPublicId()}"/>/edit'><input type="button" value="edit"/></a>
  </div>
</body>
<%@include file="footer.scripts.jsp"%>
</html>
