<jsp:useBean id="FileListView" class="eu.sqooss.webui.FileListView" scope="session"/>
<jsp:setProperty name="FileListView" property="*"/>

<% // Let's list all projects.

out.println(FileListView.getHtml());

%>
