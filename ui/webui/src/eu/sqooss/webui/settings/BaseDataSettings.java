package eu.sqooss.webui.settings;

import eu.sqooss.webui.view.AbstractDataView;

public class BaseDataSettings {

    protected boolean displayInfoPanel      = true;
    protected boolean displayControlPanel   = true;
    protected boolean displayResultPanel    = true;

    protected String[] selectedMetrics      = null;
    protected String[] selectedResources    = null;

    private String highlightedMetric        = null;
    private String highlightedResource      = null;

    private int chartType            = AbstractDataView.TABLE_CHART;

    public BaseDataSettings() {
        super();
    }

    public boolean getInfoPanelState() {
        return displayInfoPanel;
    }

    public void setInfoPanelState(boolean display) {
        this.displayInfoPanel = display;
    }

    public boolean getControlPanelState() {
        return displayControlPanel;
    }

    public void setControlPanelState(boolean display) {
        this.displayControlPanel = display;
    }

    public boolean getResultPanelState() {
        return displayResultPanel;
    }

    public void setResultPanelState(boolean display) {
        this.displayResultPanel = display;
    }

    public String[] getSelectedMetrics() {
        return selectedMetrics;
    }

    public void setSelectedMetrics(String[] selected) {
        this.selectedMetrics = selected;
    }

    public String[] getSelectedResources() {
        return selectedResources;
    }

    public void setSelectedResources(String[] selected) {
        this.selectedResources = selected;
    }

    public String getHighlightedMetric() {
        return highlightedMetric;
    }

    public void setHighlightedMetric(String metric) {
        this.highlightedMetric = metric;
    }

    public String getHighlightedResource() {
        return highlightedResource;
    }

    public void setHighlightedResource(String resource) {
        this.highlightedResource = resource;
    }

    public int getChartType() {
        return chartType;
    }

    public void setChartType(int type) {
        this.chartType = type;
    }

}
