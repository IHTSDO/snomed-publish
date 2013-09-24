<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!doctype html>
<%@page import="com.ihtsdo.snomed.model.refset.Refset, 
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
<body id="refset-edit">
  <%@include file="heading.jsp"%>
  <div class="return"><a href="/refsets">Return</a></div>
  <form:form method="post" action="/refset" modelAttribute="refset">
    <%@include file="refset.edit.inputs.jsp"%>
    <form:button value="create">create</form:button>
  </form:form>
</body>
<%@include file="footer.scripts.jsp"%>
</html>
