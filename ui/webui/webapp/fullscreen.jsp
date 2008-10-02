<%@ include file="/inc/init.jsp"
%><%@ page session="true"
%><%
//============================================================================
// Check is there is a connection with the SQO-OSS framework
//============================================================================
if (terrier.isConnected()) {
    title = "Results";
%><?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <title><%= appName + " : " + title %></title>
    <link rel="stylesheet" type="text/css" href="/style/screen.css" />
    <link rel="stylesheet" type="text/css" href="/style/webui.css" />
    <!--[if IE]><link rel="stylesheet" type="text/css" href="/style/ie.css" /><![endif]-->
  </head>

  <body>
    <div id="wrap">
      <div id="header">
        <h1><%= title %></h1>
        <img src="/style/sqo-oss.png" alt="SQO-OSS Logo"/>
      </div>
      <div id="main">
<%
    // Check, if the user has expanded/collapsed a directory
    if (request.getParameter("chartfile") != null) {
        String chartFile = request.getParameter("chartfile");
%>      <div class="fullscreen">
        <a class="fullscreen" href="javascript: history.go(-1);">
          <img src=<%= chartFile %>>
        </a>
      </div>
<%
    }
%>      </div>
      <div id="footer">
        <p>Copyright 2007-2008 by members of the <a href="http://www.sqo-oss.org/">SQO-OSS consortium</a>.</p>
      </div>
    </div>
  </body>
</html>
<%
}
//============================================================================
//Can not establish a connection with the SQO-OSS framework
//============================================================================
else {
%><%@ include file="/inc/Disconnected.jsp"
%><%
}
%>
