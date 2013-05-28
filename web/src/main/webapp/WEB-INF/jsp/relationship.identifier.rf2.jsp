<a class="tooltip arrow" href="../triple/<c:url value='${showRelationship.getSerialisedId()}'/>">
  <div class="number">
    <c:choose>
        <c:when test="${showRelationship.getSerialisedId() == -1}">Not defined</c:when>
      <c:otherwise><c:out value="${showRelationship.getSerialisedId()}"/></c:otherwise>
    </c:choose>
  </div>
  <span class="popup">
    <div class="clearfix line">
      <div class="title">Module</div>
      <div class="value"><c:out value="${showRelationship.getModule().getShortDisplayName()}"/></div>
    </div>
    <div class="clearfix line">
      <div class="title">Effective</div>
      <div class="value"><fmt:formatDate value="${showRelationship.getParsedEffectiveTime()}" type="DATE" dateStyle="LONG" /></div>
    </div>
    <div class="clearfix line">
      <div class="title">Modifier</div>
      <div class="value"><c:out value="${showRelationship.getModifier().getShortDisplayName()}"/></div>
    </div>    
    <div class="clearfix line">
      <div class="title">Characteristic</div>
      <div class="value"><c:out value="${showRelationship.getCharacteristicType().getShortDisplayName()}"/></div>
    </div>
  </span>
</a>