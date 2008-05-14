      </div>
    </div>
<%@ include file="/inc/menu.jsp" %>
    <div id="footer">
      <p>Copyright 2007-2008 by members of the
        <a href="http://www.sqo-oss.eu/">SQO-OSS consortium</a>.</p>

    </div>
  </div>
  <div id="statusbar">
<%
    out.println("&nbsp<strong>Errors:</strong>;<font color=\"red\">" + terrier.getError() + "</font>");
    terrier.flushError();
%>
  </div>
  </body>
</html>
