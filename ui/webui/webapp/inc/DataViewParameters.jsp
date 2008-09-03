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
            List<String> selResources = new ArrayList<String>();
            if (viewConf.getSelectedResources() != null)
                selResources = new ArrayList(
                    Arrays.asList(viewConf.getSelectedResources()));

            // Retrieve the added resource (if any)
            String addResource = request.getParameter("addResource");
            if (selResources.contains(addResource) == false)
                selResources.add(addResource);

            viewConf.setSelectedResources(
                selResources.toArray(new String[selResources.size()]));
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
            /*
             * Show the Info and Control panels after the user has switched
             * (or forced from the code above) to a tabular display of
             * evaluation results.
             */
            if (chartType == AbstractDataView.TABLE_CHART) {
                viewConf.setInfoPanelState(true);
                viewConf.setControlPanelState(true);
            }
        }

        /*
         * Force the tabular display of evaluation results in case the
         * resource selection is limited to a single resource.
         */
        if ((viewConf.getSelectedResources() != null)
                && (viewConf.getSelectedResources().length == 1)
                && (viewConf.getChartType() == AbstractDataView.LINE_CHART)) {
            viewConf.setChartType(AbstractDataView.TABLE_CHART);
            if ((viewConf.getInfoPanelState() == false)
                    && (viewConf.getControlPanelState() == false)) {
                viewConf.setInfoPanelState(true);
                viewConf.setControlPanelState(true);
            }
        }

        /*
         * By default, force the Info and Control panels hide, when the
         * chart display type is selected, unless the user had explicitly
         * requested their visualisation.
         */ 
        if ((viewConf.getChartType() == AbstractDataView.LINE_CHART)
                && (request.getParameter("showControlPanel") == null)
                && (request.getParameter("showInfoPanel") == null)) {
            viewConf.setInfoPanelState(false);
            viewConf.setControlPanelState(false);
        }

        /*
         * Keep showing the Info and Control panels, untill both metric AND
         * resource have been selected.
         */
        if ((viewConf.getSelectedResources() == null)
                || (viewConf.getSelectedMetrics() == null)) {
            viewConf.setInfoPanelState(true);
            viewConf.setControlPanelState(true);
        }
%>