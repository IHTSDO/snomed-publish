<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!doctype html>
<%@page import="com.ihtsdo.snomed.model.Refset, 
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

<title>Refsets</title>
<meta name="description" content="Refset Builder">
<meta name="author" content="Henrik Pettersen, Sparkling Ideas">
<link rel="stylesheet" href="/static/css/styles.css?v=1.0">
</head>
<body id="refsets">
  <div id="company" class="clearfix">
    <img class="logo" src="/static/img/logo.symbol.png"/>
    <h1>SNOMED Clinical Terms</h1>
  </div>
  <div id=refsets>
    <table>
      <thead>
          <td>Title</td>
      </thead>
      <tbody>
        #foreach($refset in $refsets)
          <tr>
            <td><a href="/refset/$refset.publicId">$refset.title</a></td>
          </tr>
        #end
      </tbody>
  </table>
  </div>
  <div class="new">
    <form name="refset" action="/refset/new" method="get">
        <input type="submit" value="new refset" />
    </form>
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

