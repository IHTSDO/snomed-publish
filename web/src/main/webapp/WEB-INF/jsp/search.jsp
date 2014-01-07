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

<%@include file="relationship.identifier.jsp"%>
#parse('textSearch.vm')


<!-- Typekit -->
<script type="text/javascript" src="//use.typekit.net/yny4pvk.js"></script>
<script type="text/javascript">try{Typekit.load();}catch(e){}</script>

<!-- Google -->
<!--script type="text/javascript">try{Typekit.load();}catch(e){}</script>
<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//ssl.google-analytics.com/analytics.js','ga');

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
<script src="/static/js/textSearch.js"></script>


<script type="text/javascript">
  TextSearch.IndexRoute = Ember.Route.extend({
    setupController: function(controller, model) {
      console.log('In pagesRoute.setupController');
      this.controllerFor('pages').set('maxPageIndexesShown', 5);
    },
    actions:{
      click: function(concept){
        $.get("https://browser.snomedtools.com/version/1/concept/" + concept.id);
      }
    }
  });
</script>



</body>
</html>

