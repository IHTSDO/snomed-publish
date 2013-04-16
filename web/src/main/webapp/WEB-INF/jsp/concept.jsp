<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<!doctype html>
<%@page import="com.ihtsdo.snomed.canonical.model.RelationshipStatement"%>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta charset="utf-8">
<title>Concept ${concept.getSerialisedId()}</title>
<meta name="description" content="Snomed browser">
<meta name="author" content="Henrik Pettersen">
<link rel="stylesheet" href="/css/styles.css?v=1.0">

<script type="text/javascript">
function changeOntology(value) {
    var redirect;
    redirect = <c:if test="${(pageContext.request.contextPath != null) && (!pageContext.request.contextPath.isEmpty())}">"/<c:out value='${pageContext.request.contextPath}'/>/" + </c:if>"<c:out value='${servletPath}'/>/ontology/" + value + "/concept/<c:out value='${concept.getSerialisedId()}' />";
    document.location.href = redirect;
}
</script>

<!--[if lt IE 9]>
    <script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
</head>
<body>
  <!--     <script src="js/scripts.js"></script>     -->
  <div id="heading" class="clearfix">
    <div id="ontology">
      <form>
        <select id="ontology" onchange="changeOntology(this.value)">
          <c:forEach var="o" items="${ontologies}">
            <option ${o.getId()==ontologyId ? "selected=\"selected\"" : ""} value="<c:out value="${o.getId()}"/>"><c:out value="${o.getName()}"/></option>
          </c:forEach>
        </select>
      </form>
    </div>
    <h2><c:out value="${concept.isPredicate() ? 'Role' : 'Concept'}" /></h2>
    <h3 class="clearfix"><c:if test="${!concept.isPredicate()}"><c:out value="${((concept.getType() == null) || concept.getType().isEmpty()) ? 'Not specified' : concept.getType()}" /></c:if></h3>
    <div id="title" class="clearfix">
      <h1>${concept.getFullySpecifiedName()}</h1>
      <div class="ids">[${concept.getSerialisedId()}, ${concept.getCtv3id()}, ${concept.getSnomedId()}]</div>
    </div>
    <div class="attributes"><c:out value="${concept.isPrimitive() ? 'Primitive' : 'Not primitive'}" />, Status <c:out value="${concept.getStatus()}"/></div>
  </div>
  
  <c:if test="${!subjectOf.isEmpty()}">
    <div class="section top clearfix">
      <h3>Subject of triple(s)</h3>
      <div class="line clearfix">
        <div class="relationship">
          <h4>Triple</h4>
        </div>
        <div class="concept left">
          <h4>Role</h4>
        </div>
        <div class="concept right">
          <h4>Object</h4>
        </div>
      </div>
      <c:forEach var="r" items="${subjectOf}"> 
        <c:if test="${!r.isKindOfRelationship()}">
          <div class="line clearfix">
            <div class="relationship identifier">
              <c:set var="showRelationship" value="${r}" />
              <%@include file="relationship.identifier.jsp"%>
            </div>        
            <div class="concept left">
              <c:set var="showConcept" value="${r.getPredicate()}" />
              <%@include file="entity.jsp"%>
            </div>
            <div class="concept right">
              <c:set var="showConcept" value="${r.getObject()}" />
              <%@include file="entity.jsp"%>
            </div>
          </div>
        </c:if>
      </c:forEach>
    </div>
  </c:if>
  <c:if test="${!objectOf.isEmpty()}">
    <div class="section clearfix double">
      <h3>Object of triple(s)</h3>  
      <div class="line clearfix">
        <div class="relationship">
          <h4>Triple</h4>
        </div>      
        <div class="concept left">
          <h4>Subject</h4>
        </div>
        <div class="concept right">
          <h4>Role</h4>
        </div>
      </div>
      <c:forEach var="r" items="${objectOf}">
        <c:if test="${!r.isKindOfRelationship()}">
          <div class="line clearfix">
            <div class="relationship identifier">
              <c:set var="showRelationship" value="${r}" />
              <%@include file="relationship.identifier.jsp"%>
            </div>  
            <div class="concept left">
              <c:set var="showConcept" value="${r.getSubject()}" />
              <%@include file="entity.jsp"%>
            </div>
            <div class="concept right">
              <c:set var="showConcept" value="${r.getPredicate()}" />
              <%@include file="entity.jsp"%>
            </div>
          </div>
        </c:if>
      </c:forEach>    
    </div>
  </c:if>
  <c:if test="${!predicateOf.isEmpty()}">
    <div class="section clearfix double">
      <h3>Role of triple(s)</h3>  
      <div class="line clearfix">
        <div class="relationship">
          <h4>Triple</h4>
        </div>
        <div class="concept left">
          <h4>Subject</h4>
        </div>
        <div class="concept right">
          <h4>Object</h4>
        </div>
      </div>
      <c:forEach var="r" items="${predicateOf}">
          <div class="line clearfix">
            <div class="relationship identifier">
              <c:set var="showRelationship" value="${r}" />
              <%@include file="relationship.identifier.jsp"%>
            </div>  
            <div class="concept left">
              <c:set var="showConcept" value="${r.getSubject()}" />
              <%@include file="entity.jsp"%>
            </div>
            <div class="concept right">
              <c:set var="showConcept" value="${r.getObject()}" />
              <%@include file="entity.jsp"%>
            </div>
          </div>
      </c:forEach>
    </div>
  </c:if>
  <div class="block clearfix">
    <c:if test="${!concept.getKindOfs().isEmpty()}">
      <div class="section clearfix single left">
        <h3>Parent concept(s)</h3>
        <c:forEach var="ct" items="${concept.getKindOfs()}">
          <div class="concept">
            <c:set var="showConcept" value="${ct}" />
            <%@include file="entity.jsp"%>
          </div>
        </c:forEach> 
      </div>
    </c:if>
    <c:if test="${!concept.getParentOf().isEmpty()}">
      <div class="section clearfix single right">
        <h3>Child concept(s)</h3>
        <c:forEach var="ct" items="${concept.getParentOf()}">
          <div class="concept">
            <c:set var="showConcept" value="${ct}" />
            <%@include file="entity.jsp"%>
          </div>
        </c:forEach> 
      </div>
    </c:if>
  </div>
  <div class="section clearfix flow">
    <h3>All primitive supertype(s)</h3>
    <c:forEach var="ct" items="${concept.getAllKindOfPrimitiveConcepts(true)}">
      <div class="concept">
        <c:set var="showConcept" value="${ct}" />
        <%@include file="entity.jsp"%>
      </div>
    </c:forEach> 
  </div>
  
  
</body>
</html>
