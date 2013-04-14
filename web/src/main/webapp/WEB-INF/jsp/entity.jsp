<div class="entry">
  <div class="name">
    <a href="<c:url value="${showConcept.getSerialisedId()}"/>"><c:out value="${showConcept.getFullySpecifiedName()}"/><c:if test="${!showConcept.isPredicate()}"> (<c:out value="${showConcept.getType()}"/>)</c:if></a>
  </div>
  <div class="ids">[<c:out value="${showConcept.getSerialisedId()}"/>, <c:out value="${showConcept.getCtv3id()}"/>, <c:out value="${showConcept.getSnomedId()}"/>]</div>
  <div class="attributes"><c:out value="${showConcept.isPrimitive() ? 'Primitive' : 'Not primitive'}" />, Status <c:out value="${showConcept.getStatus()}"/></div>
</div>
