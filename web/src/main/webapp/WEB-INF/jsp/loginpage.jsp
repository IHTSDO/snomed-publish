<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Login to Snomed Tools</title>
</head>
<body style="background: url(/static/img/mountain.jpg) no-repeat scroll 0% 0% / cover transparent;">

<c:url var="openIDLoginUrl" value="/j_spring_openid_security_check" />
<h1 style="font-size: 5em; color: lightgrey; margin: 0.25em 0 0 0.5em; font-family: click-clack-1, click-clack-2; font-weight: normal">Snomed Tools</h1>
<p style="float: left; font-family: museo-sans-1; color: lightgrey; margin: 0 0 0 2.5em">authored by 
<a target="_blank" style="color: white; text-decoration: none" href="http://uk.linkedin.com/in/sparklingideas">Henrik</a>
<a style="color: white; text-decoration: none" href="mailto:henrik@sparklingideas.co.uk">@</a> <a target="_blank" style="color: white; text-decoration: none" href="http://sparklingideas.co.uk">Sparkling Ideas</a>
<a target="_blank" style="color: white; text-decoration: none" href="http://snomed.sparklingideas.co.uk">: Project Info &nbsp;:</a>
<!-- <a target="_blank" style="color: white; text-decoration: none" href="http://snomed.sparklingideas.co.uk">: @Twitter</a> -->
</p>
<div style="margin: -0.5em 0 0 27.25em">
  <a href="http://twitter.com/isparklingideas" target="_blank"><img style="height: 30px;" src="/static/img/twitter-bird-dark-bgs.png"/></a></div>
<a style="position: absolute; right: 3em; top: 1.5em; font-size: 2em; font-weight: bold; font-family : museo-sans-1,museo-sans-2, Georgia, Verdana, Tahoma, Geneva, Arial, Sans-serif; color: yellow; text-decoration: none;"href="#" onclick="document.getElementById('login').submit();">login</a>
<form action="${openIDLoginUrl}" method="post" id="login">
    <input name="openid_identifier" type="hidden" value="https://www.google.com/accounts/o8/id"/>
<!--     <input type="image" width="200px" src="/static/img/signin.with.google.2.png"/> -->
</form>
<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="//code.jquery.com/jquery.js"></script>
<script type="text/javascript" src="//use.typekit.net/yny4pvk.js"></script>
<script type="text/javascript">try{Typekit.load();}catch(e){}</script>
</body>
</html>

<!-- 
<body>

<h1>Login</h1>
<div id="login-error">${error}</div>

<c:url var="logoUrl" value="/resources/openidlogosmall.png" />
<p><img src="${logoUrl}"></img>Login with OpenID:</p>
<c:url var="openIDLoginUrl" value="/j_spring_openid_security_check" />
<form action="${openIDLoginUrl}" method="post" >
    <label for="openid_identifier">OpenID Login</label>:
    <input id="openid_identifier" name="openid_identifier" type="text"/>
    <input  type="submit" value="Login"/>                               
</form>

<hr/>

<c:url var="googleLogoUrl" value="/resources/google-logo.png" />
<img src="${googleLogoUrl}"></img>
<form action="${openIDLoginUrl}" method="post">
       For Google users:
      <input name="openid_identifier" type="hidden" value="https://www.google.com/accounts/o8/id"/>
      <input type="submit" value="Sign with Google"/>
</form>

</body>

 -->