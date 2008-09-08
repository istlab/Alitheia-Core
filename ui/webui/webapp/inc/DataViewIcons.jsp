<%
        // Toolbar icon - Info panel
        WinIcon icoInfoPanel = new WinIcon();
        icoInfoPanel.setPath(request.getServletPath());
        icoInfoPanel.setParameter("showInfoPanel");
        icoInfoPanel.setValue("" + !viewConf.getInfoPanelState());
        if (viewConf.getInfoPanelState()) {
            icoInfoPanel.setAlt("Hide info panel");
            icoInfoPanel.setStatus(false);
        }
        else {
            icoInfoPanel.setAlt("Show info panel");
        }
        icoInfoPanel.setImage("/img/icons/16x16/infopanel.png");
        winDataView.addToolIcon(icoInfoPanel);

        // Toolbar icon - Control panel
        WinIcon icoControlPanel = new WinIcon();
        icoControlPanel.setPath(request.getServletPath());
        icoControlPanel.setParameter("showControlPanel");
        icoControlPanel.setValue("" + !viewConf.getControlPanelState());
        if (viewConf.getControlPanelState()) {
            icoControlPanel.setAlt("Hide control panel");
            icoControlPanel.setStatus(false);
        }
        else {
            icoControlPanel.setAlt("Show control panel");
        }
        icoControlPanel.setImage("/img/icons/16x16/controlpanel.png");
        winDataView.addToolIcon(icoControlPanel);

        // Toolbar icon - Result panel
        WinIcon icoResultPanel = new WinIcon();
        icoResultPanel.setPath(request.getServletPath());
        icoResultPanel.setParameter("showResultPanel");
        icoResultPanel.setValue("" + !viewConf.getResultPanelState());
        if (viewConf.getResultPanelState()) {
            icoResultPanel.setAlt("Hide results panel");
            icoResultPanel.setStatus(false);
        }
        else {
            icoResultPanel.setAlt("Show results panel");
        }
        icoResultPanel.setImage("/img/icons/16x16/resultpanel.png");
        winDataView.addToolIcon(icoResultPanel);

        // Put a separator
        winDataView.addToolIcon(icoSeparator);

        // Results icon - tabular display
        WinIcon icoTabular = new WinIcon();
        icoTabular.setPath(request.getServletPath());
        icoTabular.setParameter("chartType");
        icoTabular.setValue("" + AbstractDataView.TABLE_CHART);
        icoTabular.setAlt("Show results in a table");
        icoTabular.setImage("/img/icons/16x16/tablechart.png");
        if (viewConf.getChartType() == AbstractDataView.TABLE_CHART)
            icoTabular.setStatus(false);
        winDataView.addToolIcon(icoTabular);

        // Results icon - line chart display
        WinIcon icoLineChart = new WinIcon();
        if ((dataView.supportedCharts & AbstractDataView.LINE_CHART) > 0) {
            icoLineChart.setPath(request.getServletPath());
            icoLineChart.setParameter("chartType");
            icoLineChart.setValue("" + AbstractDataView.LINE_CHART);
            icoLineChart.setAlt("Show results as a line chart");
            icoLineChart.setImage("/img/icons/16x16/linechart.png");
            if (viewConf.getChartType() == AbstractDataView.LINE_CHART)
                icoLineChart.setStatus(false);
            winDataView.addToolIcon(icoLineChart);
        }

        // Results icon - bar chart display
        WinIcon icoBarChart = new WinIcon();
        if ((dataView.supportedCharts & AbstractDataView.BAR_CHART) > 0) {
            icoBarChart.setPath(request.getServletPath());
            icoBarChart.setParameter("chartType");
            icoBarChart.setValue("" + AbstractDataView.BAR_CHART);
            icoBarChart.setAlt("Show results as a bar chart");
            icoBarChart.setImage("/img/icons/16x16/bar-chart.png");
            if (viewConf.getChartType() == AbstractDataView.BAR_CHART)
                icoBarChart.setStatus(false);
            winDataView.addToolIcon(icoBarChart);
        }

        // Results icon - pie chart display
        WinIcon icoPieChart = new WinIcon();
        if ((dataView.supportedCharts & AbstractDataView.PIE_CHART) > 0) {
            icoPieChart.setPath(request.getServletPath());
            icoPieChart.setParameter("chartType");
            icoPieChart.setValue("" + AbstractDataView.PIE_CHART);
            icoPieChart.setAlt("Show results as a pie chart");
            icoPieChart.setImage("/img/icons/16x16/pie-chart.png");
            if (viewConf.getChartType() == AbstractDataView.PIE_CHART)
                icoPieChart.setStatus(false);
            winDataView.addToolIcon(icoPieChart);
        }

        /*
         * Disable the results display buttons, depending on the number of
         * selected resources and metrics.
         */
        if ((viewConf.getSelectedResources().isEmpty())
                || (viewConf.getSelectedMetrics() == null)) {
            icoTabular.setStatus(false);
            icoLineChart.setStatus(false);
            icoBarChart.setStatus(false);
            icoPieChart.setStatus(false);
        }
        else if (viewConf.getSelectedResources().size() == 1) {
            icoLineChart.setStatus(false);
            icoBarChart.setStatus(false);
            icoPieChart.setStatus(false);
        }

        icoCloseWin.setParameter(null);
        winDataView.addTitleIcon(icoCloseWin);
%>