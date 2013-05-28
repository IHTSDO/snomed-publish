<div class="tooltip arrow">
  <a href="<c:url value="${showConcept.getSerialisedId()}"/>"><c:out value="${name}"/></a>
  <span class="popup">
      <div class="line">
        <div class="title">Module</div>
        <div class="value"><c:out value="${showConcept.getModule().getShortDisplayName()}"/></div>
        <c:choose>
          <c:when test="${showConcept.isActive()}">
            <div class="circle active small"></div>
          </c:when>
          <c:otherwise>
            <div class="circle inactive small"></div>
          </c:otherwise>
        </c:choose>        
      </div>
      <div class="line">
        <div class="title">Effective</div>
        <div class="value"><fmt:formatDate value="${showConcept.getParsedEffectiveTime()}" type="DATE" dateStyle="LONG" /></div>
      </div>
      <div class="line">
        <div class="title">Status</div>
        <div class="value"><c:out value="${showConcept.getStatus().getShortDisplayName()}"/></div>
      </div>            
  </span>  
</div>
<div class="properties">
  <div class="id">${showConcept.getSerialisedId()}</div>
</div>

