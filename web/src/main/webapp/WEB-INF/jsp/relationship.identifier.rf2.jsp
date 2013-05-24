<a class="tooltip" href="#">
  <c:choose>
      <c:when test="${showRelationship.getSerialisedId() == -1}">Not defined</c:when>
    <c:otherwise>
      <c:out value="${showRelationship.getSerialisedId()}"/>
    </c:otherwise>
  </c:choose>
  <span class="popup">
      <div class="clearfix">
        <div class="title">Characteristic</div>
        <div class="value">
            <c:out value="${showRelationship.getCharacteristicType()}"/>
        </div>
      </div>
      <div class="clearfix"><div class="title">Refinability</div><div class="value"><c:out value="${showRelationship.getRefinability() }"/></div></div>
      <div class="clearfix"><div class="title">Group</div><div class="value"><c:out value="${showRelationship.getGroupId() }"/></div></div>
  </span>
</a>

 