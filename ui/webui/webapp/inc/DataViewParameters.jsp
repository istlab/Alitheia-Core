<%
        /*
         * Retrieve the list of selected resources (if any).
         */
        if (request.getParameter("selResources") != null) {
            viewConf.setSelectedResources(
                    request.getParameterValues("selResources"));
        }

        /*
         * Check, if the user has just added a new resource to the
         * current resource selection.
         */
        if (request.getParameter("addResource") != null) {
            String addResource = request.getParameter("addResource");
            viewConf.addSelectedResource(addResource);
            viewConf.setHighlightedResource(addResource);
        }

        /*
         * Check, if the user has requested to see information about a
         * specific resource.
         */
        if (request.getParameter("showResource") != null) {
            // Retrieve the requested resource
            String showResource = request.getParameter("showResource");
            viewConf.setHighlightedResource(showResource);
        }

        /*
         * Retrieve the list of selected resource metrics (if any).
         */
        if (request.getParameter("selMetrics") != null) {
            viewConf.setSelectedMetrics(
                request.getParameterValues("selMetrics"));
        }

        /*
         * Check, if the user has selected to show or hide a certain panel.
         */
        if (request.getParameter("showInfoPanel") != null) {
            if (request.getParameter("showInfoPanel").equals("true"))
                viewConf.setInfoPanelState(true);
            else if (request.getParameter("showInfoPanel").equals("false"))
                viewConf.setInfoPanelState(false);
        }
        if (request.getParameter("showControlPanel") != null) {
            if (request.getParameter("showControlPanel").equals("true"))
                viewConf.setControlPanelState(true);
            else if (request.getParameter("showControlPanel").equals("false"))
                viewConf.setControlPanelState(false);
        }
        if (request.getParameter("showResultPanel") != null) {
            if (request.getParameter("showResultPanel").equals("true"))
                viewConf.setResultPanelState(true);
            else if (request.getParameter("showResultPanel").equals("false"))
                viewConf.setResultPanelState(false);
        }

        /*
         * Check, if the user has switched to another type for displaying
         * the metric evaluation results.
         */
        if (request.getParameter("chartType") != null) {
            Long chartType = strToLong(request.getParameter("chartType"));
            if (chartType != null)
                viewConf.setChartType(chartType.intValue());
        }

        /*
         * Force the tabular display of evaluation results in case the
         * resource selection is limited to a single resource.
         */
        if (viewConf.getSelectedResources().size() == 1)
            viewConf.setChartType(AbstractDataView.TABLE_CHART);

        /*
         * Keep showing the Info and Control panels, untill both metric AND
         * resource have been selected.
         */
        if ((viewConf.getSelectedResources().isEmpty())
                || (viewConf.getSelectedMetrics() == null)) {
            viewConf.setInfoPanelState(true);
            viewConf.setControlPanelState(true);
        }
%>