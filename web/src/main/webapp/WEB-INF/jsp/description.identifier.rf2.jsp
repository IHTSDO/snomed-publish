<div class="tooltip arrow">
  <a href="../description/<c:url value='${d.getSerialisedId()}'/>"><c:out value="${d.getSerialisedId()}"/></a>
  <span class="popup">
      <div class="clearfix line">
        <div class="title">Module</div>
        <div class="value"><c:out value="${d.getModule().getShortDisplayName()}"/></div>
      </div>
      <div class="clearfix line">
        <div class="title">Effective</div>
        <div class="value"><fmt:formatDate value="${d.getParsedEffectiveTime()}" type="DATE" dateStyle="LONG" /></div>
      </div>
      <div class="clearfix line">
        <div class="title">Case Policy</div>
        <div class="value"><c:out value="${d.getCaseSignificance().getShortDisplayName()}"/></div>
      </div>            
  </span>
 </div>