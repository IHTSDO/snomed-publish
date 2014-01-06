<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!doctype html>
<%@page import="com.ihtsdo.snomed.model.Statement, java.text.DateFormat, java.text.SimpleDateFormat"%>

<html lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta charset="utf-8">
<script type="text/javascript" src="//use.typekit.net/yny4pvk.js"></script>
<script type="text/javascript">try{Typekit.load();}catch(e){}</script>
<title>${description.getTerm()}</title>
<meta name="description" content="Snomed browser">
<meta name="author" content="Henrik Pettersen, Sparkling Ideas">
<link rel="stylesheet" href="/css/styles.css?v=1.0">
<script type="text/javascript">
function changeOntology(value) {
    var redirect;
    redirect = "/ontology/" + value + "/concept/<c:out value='${concept.getSerialisedId()}' />";
    document.location.href = redirect;
}
</script>
<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-21180921-2', 'sparklingideas.co.uk');
  ga('send', 'pageview');

</script>
</head>
<body id="description-details">
  <div id="global" class="clearfix">
    <h2>Description</h2>
    <div id="ontology">
      <form>
        <select onchange="changeOntology(this.value)">
          <c:forEach var="o" items="${ontologies}">
            <option ${o.getId()==ontologyId ? "selected=\"selected\"" : ""} value="<c:out value="${o.getId()}"/>"><c:out value="${o.getName()}"/></option>
          </c:forEach>
        </select>
      </form>
    </div>
  </div>
  <div id="heading" class="clearfix">
    <c:choose>
      <c:when test="${description.isActive()}">
        <div class="circle active large tooltip">
          <span class="popup">Active</span>
        </div>
      </c:when>
      <c:otherwise>
        <div class="circle inactive large tooltip">
          <span class="popup">Inactive</span>
        </div>
      </c:otherwise>
    </c:choose>
    <h1>${description.getTerm()}</h1>
    
    <div class="properties">
      <div class="property">
        <div class="title">id</div>
        <div class="value"><c:out value="${description.getSerialisedId()}"/></div>
      </div> 
      <div class="property">
        <div class="title">about</div>
        <div class="value"><a href="../concept/<c:url value="${description.getAbout().getSerialisedId()}"/>"><c:out value="${description.getAbout().getShortDisplayName()}"/> [<c:out value="${description.getAbout().getSerialisedId()}"/>]</a></div>
      </div>
      <div class="property">
        <div class="title">Effective Time</div>
        <div class="value"><fmt:formatDate value="${description.getParsedEffectiveTime()}" type="DATE" dateStyle="LONG" /></div>
      </div>
      <div class="property">
        <div class="title">Type</div>
        <div class="value"><a href="<c:url value="${description.getType().getSerialisedId()}"/>"><c:out value="${description.getType().getShortDisplayName()}"/></a></div>
      </div>
      <div class="property">
        <div class="title">Case Significance</div>
        <div class="value"><a href="<c:url value="${description.getCaseSignificance().getSerialisedId()}"/>"><c:out value="${description.getCaseSignificance().getShortDisplayName()}"/></a></div>
      </div>
      <div class="property">
        <div class="title">Module</div>
        <div class="value"><a href="<c:url value="${description.getModule().getSerialisedId()}"/>"><c:out value="${description.getModule().getShortDisplayName()}"/></a></div>
      </div>
    </div>
</body>
</html>
