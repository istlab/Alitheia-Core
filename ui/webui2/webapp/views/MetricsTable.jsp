<jsp:useBean id="MetricsTableView" class="eu.sqooss.webui.MetricsTableView" scope="session"/>
<jsp:setProperty name="MetricsTableView" property="*"/>
<%

//MetricsTableView.setShowName(false);
MetricsTableView.setCellClass("metrics");
MetricsTableView.setTableClass("metrics");
out.println(MetricsTableView.getHtml());

%>

