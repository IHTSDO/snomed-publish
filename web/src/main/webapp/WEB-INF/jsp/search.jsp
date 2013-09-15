<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<!doctype html>
<%@page import="com.ihtsdo.snomed.model.Statement,
                com.ihtsdo.snomed.model.Description,
                com.ihtsdo.snomed.model.Concept"%>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>Snomed Search</title>
  
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <meta name="description" content="Snomed Search Engine">
  <meta name="author" content="Henrik Pettersen, Sparkling Ideas">

  <script type="text/javascript">
    function changeOntology(value) {
        var redirect;
        redirect = "/ontology/" + value + "/sparql";
        document.location.href = redirect;
    }
  </script>
  
  <link rel="stylesheet" href="/static/css/styles.css">
  <link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap-theme.min.css"/>
  <link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap.min.css"/>
</head>
<body id="search">

<div id="page-header" class="clearfix">
  <div id="logout"><small>You are logged in as <a href='/auth/logout'> ${user.firstname} ${user.lastname} ${user.prefix}</a></small></div>
  <img class="logo img-responsive" src="/static/img/logo.symbol.png" alt="Logo"/>
  <h1>SNOMED Clinical Terms</h1>
</div>

<script type="text/x-handlebars">
  {{outlet}}
</script>

<script type="text/x-handlebars" data-template-name="index">
  {{render "textSearch"}}
  <div class="footer">
    <small>
      <div class="author">Authored by <span class="name">Henrik Pettersen</span> @ Sparkling Ideas</div>
      <div class="link"><img src="/static/img/pointing_finger_thumb.png"/><a target="_blank" href="http://sparklingideas.co.uk">http://sparklingideas.co.uk</a></div>
    </small>
  </footer>
</script>

<script type="text/x-handlebars" data-template-name="textSearch">
  <div class="text-search clearfix">
    <div class="concept-input clearfix">
      {{render "searchInput"}}
    </div>
    {{#if controllers.searchResults.model.total}}
      <div class="concept-results clearfix">
        {{render "searchResults"}}
      </div>
      <div class="pages clearfix">
        {{render "pages"}}
      </div>
    {{/if}}
  </div>
</script>

<script type="text/x-handlebars" data-template-name="searchInput">
    <div class="syntax">
      <small>
        <a href="http://lucene.apache.org/core/3_5_0/queryparsersyntax.html" target="_blank">Help</a>
      </small>
    </div>
    {{view Bootstrap.Forms.TextField valueBinding="query" label="Snomed" placeholder="Search" }}
    <div class="total">{{controllers.searchResults.model.total}}</div>

</script>

<script type="text/x-handlebars" data-template-name="searchResults" >
  <ul class="list-group">
    {{#each model.concepts}}
      <li class="list-group-item">
        <div class="result">
          <a {{action 'click' this}} href="#">{{title}}</a><span class="identifier">({{id}})</span>
          <div class="result-property">
            <label>{{#if active}}Active{{else}}Not active{{/if}},</label>
            <label>Effective</label>
            <span class="value">{{effectiveTime}}</span>
          </div>
        </div>
      </li> 
    {{/each}}
  </ul>
</script>

<script type="text/x-handlebars" data-template-name="pages">
  {{#if shouldDisplayNavigation}}
    <ul class="pagination"  {{bind-attr style=displayWidthStyling}}>
      <li class="inactive"><a href="#" {{action "firstPage"}} style="border-bottom-left-radius: 4px;border-top-left-radius: 4px;margin-left: 0;"><small>&lt;&lt;</a></small></li>
      <li class="inactive"><a href="#" {{action "previousPage"}}><small>&lt;</small></a></li>
      {{#each page in model itemViewClass="Em.View"}}
        {{#if page.active}}
          <li class="active"><a href="#" {{action "pageRequest" page}}>{{page.index}}</a></li>
        {{else}}
          <li class="inactive"><a href="#" {{action "pageRequest" page}}>{{page.index}}</a></li>
        {{/if}}
      {{/each}}
      <li class="inactive"><a href="#" {{action "nextPage"}}><small>&gt;</small></a></li>
      <li class="inactive"><a href="#" style="border-bottom-right-radius: 4px; border-top-right-radius: 4px;" {{action "lastPage"}}><small>&gt;&gt;</small></a></li>
    </ul>
  {{/if}}
</script>


<!-- Typekit -->
<script type="text/javascript" src="//use.typekit.net/yny4pvk.js"></script>
<script type="text/javascript">try{Typekit.load();}catch(e){}</script>

<!-- Google -->
<!--script type="text/javascript">try{Typekit.load();}catch(e){}</script>
<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-21180921-2', 'sparklingideas.co.uk');
  ga('send', 'pageview');
</script-->

<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="//code.jquery.com/jquery.js"></script>

<!-- Bootstrap and Bootbox -->
<script src="//netdna.bootstrapcdn.com/bootstrap/3.0.0/js/bootstrap.min.js"></script>
<script src="/static/js/bootbox.js"></script>

<!-- Ember, Handlebars -->
<script src="/static/js/libs/handlebars-1.0.0.js"></script>
<script src="/static/js/libs/ember-1.0.0.js"></script>

<!-- Ember Bootstrap -->
<script src="/static/js/libs/ember-bootstrap.js"></script>

<!-- Application -->
<script src="/static/js/browser.js"></script>

<!-- Text Search -->
<script src="/static/js/textsearch.js"></script>





</body>
</html>

