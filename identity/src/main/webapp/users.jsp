<%-- //[START all]--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.clife.identity.security.SecurityContext" %>
<%@ page import="com.clife.identity.domain.User" %>

<%@ page import="java.util.List" %>
<%@ page import="org.apache.commons.lang3.builder.ToStringBuilder" %>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <link type="text/css" rel="stylesheet" href="/stylesheets/main.css"/>
</head>

<body>

<%
    User user = SecurityContext.getCurrentUser();
    if (user != null) {
        pageContext.setAttribute("user", user);
%>

<p>Hello, ${user.fullName}! (You can
    <a href="<%= SecurityContext.getLogoutUrl(request) %>">sign out</a>.)</p>
<%
} else {
%>
<p>Hello!
    <a href="<%= SecurityContext.getLoginUrl(request) %>">Sign in</a>
</p>
<%
    }
%>

</body>
</html>
<%-- //[END all]--%>
