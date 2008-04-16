

<%
if (terrier.getMOTD() != null) {
%>
<div id="news" class="group">
<% out.println(terrier.getMOTD()); %>
</div>
<%
}
%>