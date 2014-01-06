<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
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
<link rel="stylesheet" href="/static/css/styles.css?v=1.0">
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
  })(window,document,'script','http://www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-21180921-2', 'sparklingideas.co.uk');
  ga('send', 'pageview');

</script>
</head>
<body>
    <div id="ontology" class="clearfix">
      <form>
        <select onchange="changeOntology(this.value)">
          <c:forEach var="o" items="${ontologies}">
            <option ${o.getId()==ontologyId ? "selected=\"selected\"" : ""} value="<c:out value="${o.getId()}"/>"><c:out value="${o.getName()}"/></option>
          </c:forEach>
        </select>
      </form>
    </div>
  <div id="heading" class="clearfix">
    <h1>${displayName} <c:if test="${type!=null}"><span class="type">(${type})</span></c:if></h1>
    <div class="properties">
        <div class="ids">[${concept.getSerialisedId()}, ${concept.getCtv3id()}, ${concept.getSnomedId()}]</div>
        <span class="primitive"><c:out value="${concept.isPrimitive() ? 'Primitive' : 'Not primitive'}" /></span>, 
        <span class="status">Status <c:out value="${concept.getStatusId()}"/></span>
    </div>
  </div>
  
  <!-- VALUE OF -->
  <c:if test="${!subjectOf.isEmpty()}">
    <h3 class="top triples">Value of</h3>  
    <table class="triples">
      <tr>
        <th>Triple</th>
        <th>Attribute</th>
        <th>Value</th>
        <th></th>
      </tr>
      <c:set var="lastGroup" value="-1"/>
      <c:forEach var="r" items="${subjectOf}">
        <tr class="group-<c:out value="${r.getGroupId()}"/>">
          <td class="statement">
            <c:set var="showRelationship" value="${r}" />
            <%@include file="relationship.identifier.jsp"%>
          </td>
          <td class="concept left">
            <c:set var="showConcept" value="${r.getPredicate()}" />
            <c:set var="name" value="${showConcept.getDisplayName()}"/>
            <%@include file="entity.jsp"%>          
          </td>
          <td class="concept right">
            <c:set var="showConcept" value="${r.getObject()}" />
            <c:set var="name" value="${showConcept.getDisplayName()}"/>
            <%@include file="entity.jsp"%>          
          </td>
          <td class="group">
            <c:if test="${r.getGroupId() != lastGroup}" >
              <c:choose>
                  <c:when test="${r.getGroupId() == 0}">No group</c:when>
                <c:otherwise>
                  Group <c:out value="${r.getGroupId()}"/>
                </c:otherwise>
              </c:choose>
              <c:set var="lastGroup" value="${r.getGroupId()}"/>
            </c:if>
          </td>
        </tr>
      </c:forEach>
    </table>
  </c:if>
  
  <!-- OBJECT OF -->
  <c:if test="${!objectOf.isEmpty()}">
    <h3 class="triples">Object of</h3>  
    <table class="triples">
      <tr>
        <th class="statement">Triple</th>
        <th class="concept left">Object</th>
        <th class="concept right">Attribute</th>
        <th class="group"></th>
      </tr>
      <c:set var="lastGroup" value="-1"/>
      <c:forEach var="r" items="${objectOf}">
        <tr class="group-<c:out value="${r.getGroupId()}"/>">
          <td class="statement">
            <c:set var="showRelationship" value="${r}" />
            <%@include file="relationship.identifier.jsp"%>
          </td>
          <td class="concept left">
            <c:set var="showConcept" value="${r.getSubject()}" />
            <c:set var="name" value="${showConcept.getDisplayName()}"/>
            <%@include file="entity.jsp"%>          
          </td>
          <td class="concept right">
            <c:set var="showConcept" value="${r.getPredicate()}" />
            <c:set var="name" value="${showConcept.getDisplayName()}"/>
            <%@include file="entity.jsp"%>          
          </td>
          <td class="group">
            <c:if test="${r.getGroupId() != lastGroup}" >
              <c:choose>
                  <c:when test="${r.getGroupId() == 0}">No group</c:when>
                <c:otherwise>
                  Group <c:out value="${r.getGroupId()}"/>
                </c:otherwise>
              </c:choose>
              <c:set var="lastGroup" value="${r.getGroupId()}"/>
            </c:if>
          </td>
        </tr>
      </c:forEach>
    </table>
  </c:if>
    
  <!-- PREDICATE OF -->
  <c:if test="${!predicateOf.isEmpty()}">
    <h3 class="triples">Attribute of</h3>  
    <table class="triples">
      <tr>
        <th>Triple</th>
        <th>Object</th>
        <th>Value</th>
        <th></th>
      </tr>
      <c:set var="lastGroup" value="-1"/>
      <c:forEach var="r" items="${predicateOf}">
        <tr class="group-<c:out value="${r.getGroupId()}"/>">
          <td class="statement">
            <c:set var="showRelationship" value="${r}" />
            <%@include file="relationship.identifier.jsp"%>
          </td>
          <td class="concept left">
            <c:set var="showConcept" value="${r.getSubject()}" />
            <c:set var="name" value="${showConcept.getDisplayName()}"/>
            <%@include file="entity.jsp"%>          
          </td>
          <td class="concept right">
            <c:set var="showConcept" value="${r.getObject()}" />
            <c:set var="name" value="${showConcept.getDisplayName()}"/>
            <%@include file="entity.jsp"%>          
          </td>
          <td class="group">
            <c:if test="${r.getGroupId() != lastGroup}" >
              <c:choose>
                  <c:when test="${r.getGroupId() == 0}">No group</c:when>
                <c:otherwise>
                  Group <c:out value="${r.getGroupId()}"/>
                </c:otherwise>
              </c:choose>
              <c:set var="lastGroup" value="${r.getGroupId()}"/>
            </c:if>
          </td>
        </tr>
      </c:forEach>
    </table>
  </c:if>  
  
  <c:if test="${!concept.getKindOfs().isEmpty()}">
    <h3>Parent concept(s)</h3>
    <div class="hierarchy clearfix">
      <c:forEach var="c" items="${concept.getKindOfs()}">
        <div class="concept">
          <c:set var="showConcept" value="${c}" />
          <c:choose>
            <c:when test="${c.getDisplayName().length() < 85}">
              <c:set var="name" value="${c.getDisplayName()}"/>
            </c:when>
            <c:otherwise>
              <c:set var="name" value="${c.getDisplayName().substring(0,81).trim()}${'...'}"/>
            </c:otherwise>
          </c:choose>          
          <%@include file="entity.jsp"%>
        </div>
      </c:forEach>  
    </div>
  </c:if>

  <c:if test="${!concept.getParentOf().isEmpty()}">
    <h3>Child concept(s)</h3>
    <div class="hierarchy clearfix">
      <c:forEach var="c" items="${concept.getParentOf()}">
        <div class="concept">
          <c:set var="showConcept" value="${c}" />
          <c:choose>
            <c:when test="${c.getDisplayName().length() < 85}">
              <c:set var="name" value="${c.getDisplayName()}"/>
            </c:when>
            <c:otherwise>
              <c:set var="name" value="${c.getDisplayName().substring(0,81).trim()}${'...'}"/>
            </c:otherwise>
          </c:choose>
          <%@include file="entity.jsp"%>
        </div>
      </c:forEach>
    </div>  
  </c:if>
  

  
    <h3>All primitive supertype(s)</h3>
    <div class="hierarchy clearfix">
      <c:forEach var="c" items="${concept.getAllKindOfPrimitiveConcepts(true)}">
        <div class="concept">
          <c:set var="showConcept" value="${c}" />
          <c:choose>
            <c:when test="${c.getDisplayName().length() < 85}">
              <c:set var="name" value="${c.getDisplayName()}"/>
            </c:when>
            <c:otherwise>
              <c:set var="name" value="${c.getDisplayName().substring(0,81).trim()}${'...'}"/>
            </c:otherwise>
          </c:choose>          
          <%@include file="entity.jsp"%>
        </div>
      </c:forEach>
    </div> 
  
  
  
</body>
</html>
