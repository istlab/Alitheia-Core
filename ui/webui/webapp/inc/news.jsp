

<%
String motd = terrier.getUserMessageOfTheDay();
if (motd != null) {
%>
<div id="news" class="group"><% out.println(motd); %></div>
<%
}
%>
