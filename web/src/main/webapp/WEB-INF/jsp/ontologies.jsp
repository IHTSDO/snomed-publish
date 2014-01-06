<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<%@page import="com.ihtsdo.snomed.model.Statement"%>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta charset="utf-8">
<script type="text/javascript" src="//use.typekit.net/yny4pvk.js"></script>
<script type="text/javascript">try{Typekit.load();}catch(e){}</script>
<title>Snomed ${concept.getSerialisedId()}</title>
<meta name="description" content="Snomed browser">
<meta name="author" content="Henrik Pettersen, Sparkling Ideas">
<link rel="stylesheet" href="/css/styles.css?v=1.0">
<script type="text/javascript">
function changeOntology(value) {
    var redirect;
    redirect = "/version/" + value + "/concept/<c:out value='${concept.getSerialisedId()}' />";
    document.location.href = redirect;
}
</script>
<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','http://www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-21180921-2', 'sparklingideas.co.uk');
  ga('send', 'pageview');

</script>
</head>
<body>

    <h1>Ontologies</h1>
    <ul style="width: 100%; clear: both;" class="ontologies">
        <c:forEach var="o" items="${ontologies}">
            <li>
                <span class="name"><a href="version/<c:out value='${o.getId()}'/>"><c:out value="${o.getName()}"/></a></span>
                <span class="delete"><a href="version/<c:out value='${o.getId()}'/>/delete"/>delete</span></span>
                <span class="delete"><a href="version/<c:out value='${o.getId()}'/>/export"/>export canonical form</span></span>
            </li>
        </c:forEach>
    </ul>

  <form:form modelAttribute="uploadItem" method="post" enctype="multipart/form-data">
    <fieldset>
      <legend>Upload Fields</legend>
      <p>
        <form:label for="name" path="name">Name</form:label>
        <br />
        <form:input path="name" />
      </p>
      <p>
        <form:label for="fileData" path="fileData">File</form:label>
        <br />
        <form:input path="fileData" type="file" />
      </p>
      <p>
        <input type="submit" />
      </p>
    </fieldset>
  </form:form>
</body>
</html>
