<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!doctype html>
<%@page import="com.ihtsdo.snomed.model.Statement, 
    java.text.DateFormat, 
    java.text.SimpleDateFormat, 
    java.text.DecimalFormat"%>

<%!
    DecimalFormat decimalFormat = new DecimalFormat("###,###.###");
%>

<html lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta charset="utf-8">
<script type="text/javascript">
<!--
    function toggle_visibility(id) {
       var e = document.getElementById(id);
       if(e.style.display == 'block')
          e.style.display = 'none';
       else
          e.style.display = 'block';
    }
//-->
</script>


<title>${displayName}</title>
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
</head>
<body>
  <div id="company" class="clearfix">
    <img class="logo" src="/img/logo.symbol.png"/>
    <h1>SNOMED Clinical Terms</h1>
    <div id="global-navigation">
      <div id="ontology">
        <form>
          <select onchange="changeOntology(this.value)">
            <c:forEach var="o" items="${ontologies}">
              <option ${o.getId()==ontologyId ? "selected=\"selected\"" : ""} value="<c:out value="${o.getId()}"/>"><c:out value="${o.getName()}"/></option>
            </c:forEach>
          </select>
        </form>
      </div>
      <div id="find-concept" class="clearfix">
          <form onsubmit="location.href=document.getElementById('conceptid').value; return false;">
              <button type="submit">Go</button>
              <input type="text" id="conceptid" value="${concept.getSerialisedId()}"></input>
          </form>          
      </div>
    </div>
  </div>
  <div id="global" class="clearfix">
    <h2><a href="<c:url value="${concept.getModule().getSerialisedId()}"/>"><c:out value="${concept.getModule().getShortDisplayName()}"/></a></h2>
  </div>
  <div id="heading" class="clearfix">
    <div class="title">
      <h1>${displayName} <c:if test="${type!=null}"><span class="type">(${type})</span></c:if></h1>
      <div class="isactive">
        <c:choose>
          <c:when test="${concept.isActive()}">
              <div class="active tooltip left id">
                  <div class="number">${concept.getSerialisedId()}</div>
                  <span class="popup">Active</span>
            </div>
          </c:when>
          <c:otherwise>
              <div class="inactive tooltip left id">
                  <div class="number">${concept.getSerialisedId()}</div>
                  <span class="popup">Inactive</span>
              </div>
          </c:otherwise>
        </c:choose> 
      </div>
    </div>
    <div class="properties">
        <div class="status"><a href="<c:url value="${concept.getStatus().getSerialisedId()}"/>"><c:out value="${concept.getStatus().getShortDisplayName()}"/></a></div>
        <div class="effectiveTime"><fmt:formatDate value="${concept.getParsedEffectiveTime()}" type="DATE" dateStyle="LONG" /></div> 
    </div>
  </div>
  
  <!--  DESCRIPTIONS -->
  <h3 class="descriptions clearfix">Descriptions
    <span class="show-hide">
      <a id="hide-descriptions" style="display:none" href="#" onclick="toggle_visibility('descriptions');toggle_visibility('hide-descriptions');toggle_visibility('show-descriptions');">Hide</a>
      <a id="show-descriptions" style="display:block" href="#" onclick="toggle_visibility('descriptions');toggle_visibility('hide-descriptions');toggle_visibility('show-descriptions');">Show</a>  
    </span>
  </h3>
  <div id="descriptions" class="clearfix section" style="display:none">
    <table class="descriptions">
      <c:forEach var="d" items="${descriptions}">
          <tr>
              <td class="isactive">
                <c:choose>
                  <c:when test="${d.isActive()}">
                    <div class="circle active small tooltip">
                      <span class="popup">Active</span>
                    </div>
                  </c:when>
                  <c:otherwise>
                    <div class="circle inactive small tooltip">
                      <span class="popup">Inactive</span>
                    </div>
                  </c:otherwise>
                </c:choose>
              </td>
              <td class="id">
                <%@include file="description.identifier.rf2.jsp"%>
              </td>
              <td class="type">
                <c:choose>
                  <c:when test="${d.isFullySpecifiedName()}">FSN</c:when>
                  <c:otherwise><c:out value="${d.getType().getShortDisplayName()}"/></c:otherwise>
                </c:choose>
              </td>
              <td class="term"><c:out value="${d.getTerm().trim()}"/></td>
          </tr>
      </c:forEach>
    </table>
  </div>
  
  <!-- OBJECT OF -->
  <c:if test="${!objectOf.isEmpty()}">
    <div class="triples">
      <h3>Object of
        <span class="show-hide">
          <a id="hide-valueof" style="display:block" href="#" onclick="toggle_visibility('valueof');toggle_visibility('hide-valueof');toggle_visibility('show-valueof');">Hide</a>
          <a id="show-valueof" style="display:none" href="#" onclick="toggle_visibility('valueof');toggle_visibility('hide-valueof');toggle_visibility('show-valueof');">Show</a>  
        </span>    
      </h3>
      <div id="valueof" class="clearfix section" style="display:block">
        <table>
          <thead>
            <tr>
              <th></th>
              <th></th>
              <th>Attribute</th>
              <th>Value</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <c:set var="lastGroup" value="-1"/>
            <c:forEach var="r" items="${objectOf}">
              <tr class="group-<c:out value="${r.getGroupId()}"/>">
                <td class="isactive">
                  <c:choose>
                    <c:when test="${r.isActive()}">
                      <div class="circle active small tooltip">
                        <span class="popup">Active</span>
                      </div>
                    </c:when>
                    <c:otherwise>
                      <div class="circle inactive small tooltip">
                        <span class="popup">Inactive</span>
                      </div>
                    </c:otherwise>
                  </c:choose>
                </td>        
                <td class="statement id">
                  <c:set var="showRelationship" value="${r}" />
                  <%@include file="relationship.identifier.rf2.jsp"%>
                </td>
                <c:set var="showConcept" value="${r.getPredicate()}" />
                <td class="concept left <c:choose><c:when test='${showConcept.isActive()}'>active</c:when><c:otherwise>inactive</c:otherwise></c:choose>">
                    <c:set var="name" value="${showConcept.getDisplayName()}"/>
                    <%@include file="entity.rf2.jsp"%>
                </td>
                <c:set var="showConcept" value="${r.getObject()}" />
                <td class="concept right <c:choose><c:when test='${showConcept.isActive()}'>active</c:when><c:otherwise>inactive</c:otherwise></c:choose>">
                    <c:set var="name" value="${showConcept.getDisplayName()}"/>
                    <%@include file="entity.rf2.jsp"%>
                </td>
                <td class="group <c:if test='${lastGroup==-1}'>top</c:if>">
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
          </tbody>
        </table>
        <c:if test="${objectOfCount < objectOfTotal}">
          <div class="pagination">
            <div class="navigation left">
                <a class="start" href="${concept.getSerialisedId()}${objectOfFirstParams}">&lt;&lt;</a>
                <a class="previous" href="${concept.getSerialisedId()}${objectOfPreviousParams}">&lt;</a>
            </div>
            <div class="text">Displaying triples <fmt:formatNumber value="${objectOfStartIndex + 1}" type="NUMBER"/> through <fmt:formatNumber value="${objectOfTotal < objectOfCount + objectOfStartIndex ? objectOfTotal : objectOfStartIndex + objectOfCount}" type="NUMBER"/> out of <fmt:formatNumber value="${objectOfTotal}" type="NUMBER"/></div> total            
            <div class="navigation right">
                <a class="next" href="${concept.getSerialisedId()}${objectOfNextParams}">&gt;</a>
                <a class="last" href="${concept.getSerialisedId()}${objectOfLastParams}">&gt;&gt;</a>        
            </div>
          </div>  
        </c:if>
      </div>
    </div>
  </c:if>

  <!-- VALUE OF -->
  <c:if test="${!valueOf.isEmpty()}">
    <div class="triples">
      <h3>Value of
        <span class="show-hide">
          <a id="hide-objectOf" style="display:block" href="#" onclick="toggle_visibility('objectOf');toggle_visibility('hide-objectOf');toggle_visibility('show-objectOf');">Hide</a>
          <a id="show-objectOf" style="display:none" href="#" onclick="toggle_visibility('objectOf');toggle_visibility('hide-objectOf');toggle_visibility('show-objectOf');">Show</a>  
        </span>     
      </h3>
      <div id="objectOf" class="clearfix section" style="display:block">    
        <table>
          <thead>
            <th></th>
            <th></th>
            <th>Object</th>
            <th>Attribute</th>
            <th></th>
          </thead>
          <tbody>
            <c:set var="lastGroup" value="-1"/>
            <c:forEach var="r" items="${valueOf}">
              <tr class="group-<c:out value="${r.getGroupId()}"/>">
                <td class="isactive">
                  <c:choose>
                    <c:when test="${r.isActive()}">
                      <div class="circle active small tooltip">
                        <span class="popup">Active</span>
                      </div>
                    </c:when>
                    <c:otherwise>
                      <div class="circle inactive small tooltip">
                        <span class="popup">Inactive</span>
                      </div>
                    </c:otherwise>
                  </c:choose>
                </td>          
                <td class="statement">
                  <c:set var="showRelationship" value="${r}" />
                  <%@include file="relationship.identifier.rf2.jsp"%>
                </td>
                <c:set var="showConcept" value="${r.getSubject()}" />
                <td class="concept left <c:choose><c:when test='${c.isActive()}'>active</c:when><c:otherwise>inactive</c:otherwise></c:choose>">
                    <c:set var="name" value="${showConcept.getDisplayName()}"/>
                    <%@include file="entity.rf2.jsp"%>
                </td>
                <c:set var="showConcept" value="${r.getPredicate()}" />
                <td class="concept right <c:choose><c:when test='${showConcept.isActive()}'>active</c:when><c:otherwise>inactive</c:otherwise></c:choose>">
                  <c:set var="name" value="${showConcept.getDisplayName()}"/>
                  <%@include file="entity.rf2.jsp"%>
                </td>
                <td class="group <c:if test='${lastGroup==-1}'>top</c:if>">
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
          </tbody>
        </table>
        <c:if test="${valueOfCount < valueOfTotal}">
          <div class="pagination">
            <div class="navigation left">
                <a class="start" href="${concept.getSerialisedId()}${valueOfFirstParams}">&lt;&lt;</a>
                <a class="previous" href="${concept.getSerialisedId()}${valueOfPreviousParams}">&lt;</a>
            </div>
            <div class="text">Displaying triples <fmt:formatNumber value="${valueOfStartIndex + 1}" type="NUMBER"/> through <fmt:formatNumber value="${valueOfTotal < valueOfCount + valueOfStartIndex ? valueOfTotal : valueOfStartIndex + valueOfCount}" type="NUMBER"/> out of <fmt:formatNumber value="${valueOfTotal}" type="NUMBER"/></div> total
            <div class="navigation right">
                <a class="next" href="${concept.getSerialisedId()}${valueOfNextParams}">&gt;</a>
                <a class="last" href="${concept.getSerialisedId()}${valueOfLastParams}">&gt;&gt;</a>        
            </div>
          </div>  
        </c:if>
      </div>    
    </div>
  </c:if>
    
  <!-- ATTRIBUTE OF -->
  <c:if test="${!attributeOf.isEmpty()}">
    <div class="triples">
      <h3>Attribute of
        <span class="show-hide">
          <a id="hide-predicateOf" style="display:block" href="#" onclick="toggle_visibility('predicateOf');toggle_visibility('hide-predicateOf');toggle_visibility('show-predicateOf');">Hide</a>
          <a id="show-predicateOf" style="display:none" href="#" onclick="toggle_visibility('predicateOf');toggle_visibility('hide-predicateOf');toggle_visibility('show-predicateOf');">Show</a>  
        </span>     
      </h3> 
      <div id="predicateOf" class="clearfix section" style="display:block"> 
        <table>
          <thead>
            <tr>
              <th></th>
              <th></th>
              <th>Object</th>
              <th>Value</th>
              <th></th>
            </tr>
          </thead>
          <c:set var="lastGroup" value="-1"/>
          <tbody>
            <c:forEach var="r" items="${attributeOf}">
              <tr class="group-<c:out value="${r.getGroupId()}"/>">
                <td class="isactive">
                  <c:choose>
                    <c:when test="${r.isActive()}">
                      <div class="circle active small tooltip">
                        <span class="popup">Active</span>
                      </div>
                    </c:when>
                    <c:otherwise>
                      <div class="circle inactive small tooltip">
                        <span class="popup">Inactive</span>
                      </div>
                    </c:otherwise>
                  </c:choose>
                </td>
                <td class="statement">
                  <c:set var="showRelationship" value="${r}" />
                  <%@include file="relationship.identifier.rf2.jsp"%>
                </td>
                <c:set var="showConcept" value="${r.getSubject()}" />
                <td class="concept left <c:choose><c:when test='${showConcept.isActive()}'>active</c:when><c:otherwise>inactive</c:otherwise></c:choose>">
                  <c:set var="name" value="${showConcept.getDisplayName()}"/>
                  <%@include file="entity.rf2.jsp"%>
                </td>
                <c:set var="showConcept" value="${r.getObject()}" />
                <td class="concept right <c:choose><c:when test='${showConcept.isActive()}'>active</c:when><c:otherwise>inactive</c:otherwise></c:choose>">
                  <c:set var="name" value="${showConcept.getDisplayName()}"/>
                  <%@include file="entity.rf2.jsp"%>
                </td>
                <td class="group <c:if test='${lastGroup==-1}'>top</c:if>">
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
          </tbody>
        </table>
        <c:if test="${attributeOfCount < attributeOfTotal}">
          <div class="pagination">
            <div class="navigation left">
                <a class="start" href="${concept.getSerialisedId()}${attributeOfFirstParams}">&lt;&lt;</a>
                <a class="previous" href="${concept.getSerialisedId()}${attributeOfPreviousParams}">&lt;</a>
            </div>
            <div class="text">Displaying triples <fmt:formatNumber value="${attributeOfStartIndex + 1}" type="NUMBER"/> through <fmt:formatNumber value="${attributeOfTotal < attributeOfCount + attributeOfStartIndex ? attributeOfTotal : attributeOfStartIndex + attributeOfCount}" type="NUMBER"/> out of <fmt:formatNumber value="${attributeOfTotal}" type="NUMBER"/></div> total
            <div class="navigation right">
                <a class="next" href="${concept.getSerialisedId()}${attributeOfNextParams}">&gt;</a>
                <a class="last" href="${concept.getSerialisedId()}${attributeOfLastParams}">&gt;&gt;</a>        
            </div>
          </div>  
        </c:if>      
      </div>      
    </div>
  </c:if>  
  
  <c:if test="${!concept.getKindOfs().isEmpty()}">
    <h3>Parent concept(s)
      <span class="show-hide">
        <a id="hide-parents" style="display:block" href="#" onclick="toggle_visibility('parents');toggle_visibility('hide-parents');toggle_visibility('show-parents');">Hide</a>
        <a id="show-parents" style="display:none" href="#" onclick="toggle_visibility('parents');toggle_visibility('hide-parents');toggle_visibility('show-parents');">Show</a>  
      </span>     
    </h3>
    <div id="parents" class="clearfix section" style="display:block">    
      <div class="hierarchy clearfix">
        <c:forEach var="c" items="${kindOfs}">
          <div class="concept <c:choose><c:when test='${c.isActive()}'>active</c:when><c:otherwise>inactive</c:otherwise></c:choose>">
            <c:set var="showConcept" value="${c}" />
            <c:choose>
              <c:when test="${c.getDisplayName().length() < 85}">
                <c:set var="name" value="${c.getDisplayName()}"/>
              </c:when>
              <c:otherwise>
                <c:set var="name" value="${c.getDisplayName().substring(0,81).trim()}${'...'}"/>
              </c:otherwise>
            </c:choose>          
            <%@include file="entity.rf2.jsp"%>
          </div>
        </c:forEach>  
      </div>
    </div>
  </c:if>

  <c:if test="${!concept.getParentOf().isEmpty()}">
    <h3>Child concept(s)
      <span class="show-hide">
        <a id="hide-children" style="display:block" href="#" onclick="toggle_visibility('children');toggle_visibility('hide-children');toggle_visibility('show-children');">Hide</a>
        <a id="show-children" style="display:none" href="#" onclick="toggle_visibility('children');toggle_visibility('hide-children');toggle_visibility('show-children');">Show</a>  
      </span>     
    </h3>
    <div id="children" class="clearfix section" style="display:block">
      <div class="hierarchy clearfix">
        <c:forEach var="c" items="${parentOf}">
          <div class="concept <c:choose><c:when test='${c.isActive()}'>active</c:when><c:otherwise>inactive</c:otherwise></c:choose>">
            <c:set var="showConcept" value="${c}" />
            <c:choose>
              <c:when test="${c.getDisplayName().length() < 85}">
                <c:set var="name" value="${c.getDisplayName()}"/>
              </c:when>
              <c:otherwise>
                <c:set var="name" value="${c.getDisplayName().substring(0,81).trim()}${'...'}"/>
              </c:otherwise>
            </c:choose>
            <%@include file="entity.rf2.jsp"%>
          </div>
        </c:forEach>
      </div>
    </div>  
  </c:if>
  
  <h3>All primitive supertype(s)
    <span class="show-hide">
      <a id="hide-allPrimitiveParents" style="display:block" href="#" onclick="toggle_visibility('allPrimitiveParents');toggle_visibility('hide-allPrimitiveParents');toggle_visibility('show-allPrimitiveParents');">Hide</a>
      <a id="show-allPrimitiveParents" style="display:none" href="#" onclick="toggle_visibility('allPrimitiveParents');toggle_visibility('hide-allPrimitiveParents');toggle_visibility('show-allPrimitiveParents');">Show</a>  
    </span>     
  </h3>
  <div id="allPrimitiveParents" class="clearfix section"  style="display:block">
    <div class="hierarchy clearfix">
      <c:forEach var="c" items="${allPrimitiveSupertypes}">
        <div class="concept <c:choose><c:when test='${c.isActive()}'>active</c:when><c:otherwise>inactive</c:otherwise></c:choose>">
          <c:set var="showConcept" value="${c}" />
          <c:choose>
            <c:when test="${c.getDisplayName().length() < 85}">
              <c:set var="name" value="${c.getDisplayName()}"/>
            </c:when>
            <c:otherwise>
              <c:set var="name" value="${c.getDisplayName().substring(0,81).trim()}${'...'}"/>
            </c:otherwise>
          </c:choose>          
          <%@include file="entity.rf2.jsp"%>
        </div>
      </c:forEach>
    </div>
  </div>
</body>
<script type="text/javascript" src="//use.typekit.net/yny4pvk.js"></script>
<script type="text/javascript">try{Typekit.load();}catch(e){}</script>
<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-21180921-2', 'sparklingideas.co.uk');
  ga('send', 'pageview');
</script>
</html>
