<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Login to Snomed Browser</title>
</head>
<body style="background-image: url('/img/village.jpg')'">
<div style="background-image: url('/img/village.jpg')'; background-repeat: repeat; background-position: left top;">
<c:url var="openIDLoginUrl" value="/j_spring_openid_security_check" />
<h1 style="padding-bottom: 1em">Snomed Browser</h1>
<h3>Please login using your Google / @ihtsdo.org account:</h3>
<form action="${openIDLoginUrl}" method="post">
    <input name="openid_identifier" type="hidden" value="https://www.google.com/accounts/o8/id"/>
    <input type="image" width="200px" src="/img/signin.with.google.png"/>
</form>
</div>
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