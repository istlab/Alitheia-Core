

<%
if (terrier.getUserMessageOfTheDay() != null) {
%>
<div id="news" class="group">
<% out.println(terrier.getUserMessageOfTheDay()); %>
</div>
<%
}
%>
