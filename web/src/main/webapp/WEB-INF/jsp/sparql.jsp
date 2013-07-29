<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<!doctype html>
<%@page import="com.ihtsdo.snomed.model.Statement,
                com.ihtsdo.snomed.model.Description,
                com.ihtsdo.snomed.model.Concept"%>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta charset="utf-8">
  <script type="text/javascript" src="//use.typekit.net/yny4pvk.js"></script>
  <script type="text/javascript">try{Typekit.load();}catch(e){}</script>
  <title>Snomed SPARQL query</title>
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
    })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
  
    ga('create', 'UA-21180921-2', 'sparklingideas.co.uk');
    ga('send', 'pageview');
  
  </script>
</head>
<body id="sparql">
  <form:form method="post" action="sparql" modelAttribute="sparql">
    <div>
      <h1><form:label path="query">Enter SPARQL Query</form:label></h1>
      <div class="input"><form:textarea path="query" rows="10" cols="100"/></div>
      <div class="prefixes">
      <h3>Prefixes</h3>
        <table>
          <tr>
              <td>rdf</td>
              <td>http://www.w3.org/1999/02/22-rdf-syntax-ns#</td>
          </tr>
          <tr>
              <td>rdfs</td>
              <td>http://www.w3.org/2000/01/rdf-schema#</td>
          </tr>
          <tr>
              <td>c</td>
              <td>http://snomed.sparklingideas.co.uk/ontology/1/concept/</td>
          </tr>
          <tr>
              <td>d</td>
              <td>http://snomed.sparklingideas.co.uk/ontology/1/description/</td>
          </tr>
          <tr>
              <td>s</td>
              <td>http://snomed.sparklingideas.co.uk/ontology/1/statement/</td>
          </tr>
        </table>
      </div>
      <div class="attributes">
        <div class="rdf-reference"><a href="http://www.w3schools.com/rdf/rdf_reference.asp" target="_blank">RDF Reference</a></div>      
        <h4>Concept</h4>
        <ul>
            <li>rdf:type</li>
            <li>rdfs:label</li>
            <li>sn:description</li>
            <li>sn:module</li>
            <li>sn:status</li>
            <li>sn:active</li>
            <li>sn:effectiveTime</li>
        </ul>
        <h4>Triple (<a href="http://goo.gl/sEb4nB" target="_blank">reification</a>)</h4>
        <ul>
            <li>rdf:type</li>
            <li>rdf:subject</li>
            <li>rdf:predicate</li>
            <li>rdf:object</li>
            <li>sn:modifier</li>
            <li>sn:module</li>
            <li>sn:characteristictype</li>
            <li>sn:group</li>
            <li>sn:active</li>
            <li>sn:effectiveTime</li>
        </ul>
        <h4>Description</h4>
        <ul>
            <li>rdf:type</li>
            <li>rdfs:label</li>
            <li>sn:module</li>
            <li>sn:type</li>
            <li>sn:casesignificance</li>
            <li>sn:active</li>
            <li>sn:effectiveTime</li>
        </ul>
      </div>
    </div> 
    <div class="submit"><input type="submit" value="Run Query"/></div>
  </form:form>
  <c:if test="${results!=null}">
<!--     <div id="results" style="float: left;"> -->
      <table class="results">
        <c:forEach var="result" items="${results.getResults()}">
          <tr class="result"></tr>
            <c:forEach var="variable" items="${result.keySet()}">
              <tr>
                <td><c:out value="${variable}"/></td>
                <td>
                  <c:set var="b" value="${result.get(variable)}"/>
                  <c:set var="o" value="${result.get(variable).getSourceBackedObject()}"/>
                  <c:choose>
                    <c:when test="${b.isConcept()}">
                      <div class="concept binding">
                        <a href="concept/<c:url value="${o.getSerialisedId()}"/>"><c:out value="${o.getDisplayName()}"/></a><div class="id">[<c:out value="${o.getSerialisedId()}"/>]</div>
                      </div>
                    </c:when>
                    <c:when test="${b.isStatement()}">
                      <div class="statement  binding">
                        <a href="statement/<c:url value="${o.getSerialisedId()}"/>">Statement</a><div class="id">[<c:out value="${o.getSerialisedId()}"/>]</div>
                      </div>
                    </c:when>
                    <c:when test="${b.isDescription()}">
                      <div class="description  binding">
                        <a href="description/<c:url value="${o.getSerialisedId()}"/>"></a><c:out value="${o.getTerm()}"/></a><div class="id">[<c:out value="${o.getSerialisedId()}"/>]</div>
                      </div>
                    </c:when>                                    
                    <c:otherwise>
                      <div class="rdf"><c:out value='${o.toString().replaceAll("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf:").replaceAll("http://www.w3.org/2000/01/rdf-schema#", "rdfs:")}'/></div>                      
                    </c:otherwise>
                  </c:choose>
                </td>
              </tr>
            </c:forEach>
        </c:forEach>
      </table>
      
<!--         <div class="result" style="padding: 2em; border: 1px solid red; clear: both; float: left"> -->
<%--           <c:forEach var="variable" items="${result.keySet()}"> --%>
<!--             <div class="pair" style="border: 1px dotted blue"> -->
<%--               <div class="variable"><c:out value="${variable}"/>: </div> --%>
<!--               <div class="binding"> -->
<%--                 <c:set var="b" value="${result.get(variable)}"/> --%>
<%--                 <c:set var="o" value="${result.get(variable).getSourceBackedObject()}"/> --%>
<%--                 <c:choose> --%>
<%--                   <c:when test="${b.isConcept()}"> --%>
<!--                     <div class="concept"> -->
<%--                       <a href="concept/<c:url value="${concept.getSerialisedId()}"/>"><c:out value="${o.getDisplayName()}"/></a> --%>
<!--                     </div> -->
<%--                   </c:when> --%>
<%--                   <c:when test="${b.isStatement()}"> --%>
<!--                     <div class="statement"> -->
<%--                       <a href="statement/<c:url value="${statement.getSerialisedId()}"/>">Statement <c:out value="${o.getSerialisedId()}"/></a> --%>
<!--                     </div> -->
<%--                   </c:when> --%>
<%--                   <c:when test="${b.isDescription()}"> --%>
<!--                     <div class="description"> -->
<%--                       <span>Description:</span><a href="description/<c:url value="${o.getSerialisedId()}"/>"><c:out value="${o.getTerm()}"/></a> --%>
<!--                     </div> -->
<%--                   </c:when>                                     --%>
<%--                   <c:otherwise> --%>
<%--                     <div class="rdf"><c:out value="${o}"/></div> --%>
<%--                   </c:otherwise> --%>
<%--                 </c:choose> --%>
<!--               </div> -->
<!--             </div> -->
<%--           </c:forEach> --%>
<!--         </div> -->
<%--       </c:forEach> --%>
<!--     </div> -->
  </c:if>
</body>
</html>

