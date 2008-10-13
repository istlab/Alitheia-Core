<%@ page import="eu.sqooss.webui.*"
%><%@ page import="eu.sqooss.webui.util.*"
%><%@ page import="eu.sqooss.webui.widgets.*"
%><%
//============================================================================
// Display the selected project's timeline
//============================================================================
if (selectedProject.isValid()) {
%>                <div id="timelineview">
<%
    // Indentation depth
    in = 9;
    
    /*
     * Construct the window's content
     */
    Window winTimelineView = new Window();
    winTimelineView.setTitle("Timeline of project: " + selectedProject.getName());
    
    /*
     * Initialise the data view's object
     */
    
    /*
     * Construct the window icons
     */
    icoCloseWin.setPath("/");
    icoCloseWin.setParameter(null);
    winTimelineView.addTitleIcon(icoCloseWin);
    
    /*
     * Construct the toolbar icons
     */
    
    /*
     * Display the window
     */
     winTimelineView.setContent("Under construction!");
    out.print(winTimelineView.render(in));
}
//============================================================================
// Let the user choose a project, if none was selected
//============================================================================
else {
%><%@ include file="/inc/SelectProject.jsp"
%><%
}
%>                </div>
