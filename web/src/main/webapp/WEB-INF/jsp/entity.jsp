<a href="<c:url value="${showConcept.getSerialisedId()}"/>"><c:out value="${name}"/></a>
<div class="properties">
  <div class="ids">[${showConcept.getSerialisedId()}, ${showConcept.getCtv3id()}, ${showConcept.getSnomedId()}]</div>
  <span class="primitive"><c:out value="${showConcept.isPrimitive() ? 'Primitive' : 'Not primitive'}" /></span>, 
  <span class="status">Status <c:out value="${showConcept.getStatus()}"/></span>
</div>

