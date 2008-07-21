<%
String motd = terrier.getUserMessageOfTheDay();
if (motd != null) {
%>                <div id="news">
                  <p>This is the demo installation of the Alitheia Core software.</p>
                  <p>You can find more about the SQO-OSS project from our <a href="http://www.sqo-oss.org">website</a></p>
                </div>
<%
}
%><!-- news.jsp -->
