<a class="tooltip" href="">
  <c:out value="${showRelationship.getSerialisedId()}"/>
  <span class="relationship-property">
      <div class="line"><div class="title">Characteristic</div><div class="value"><c:out value="${showRelationship.getCharacteristicType()}"/> (<c:out value="${showRelationship.isDefiningCharacteristic() ? 'Defining' : 'Not defining'}"/>)</div></div>
      <div class="line"><div class="title">Refinability</div><div class="value"><c:out value="${showRelationship.getRefinability() }"/></div></div>
      <div class="line"><div class="title">Group</div><div class="value"><c:out value="${showRelationship.getGroup() }"/></div></div>
  </span>
</a>
<div class="group">[<c:out value="${showRelationship.getGroup()}"/>]</div>
 