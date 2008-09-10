package eu.sqooss.webui.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.sqooss.webui.view.AbstractDataView;

public class BaseDataSettings {

    protected boolean displayInfoPanel      = true;
    protected boolean displayControlPanel   = true;
    protected boolean displayResultPanel    = true;

    protected String[] selectedMetrics      = null;
    protected ArrayList<String> selectedResources = new ArrayList<String>();

    private String highlightedMetric        = null;
    private String highlightedResource      = null;

    private int chartType            = AbstractDataView.TABLE_CHART;

    private boolean generatePDF             = false;

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

    public List<String> getSelectedResources() {
        return selectedResources;
    }

    public void setSelectedResources(String[] selected) {
        selectedResources = new ArrayList<String>(Arrays.asList(selected));
    }

    public void addSelectedResource(String resource) {
        if (isSelectedResource(resource) == false)
            selectedResources.add(resource);
    }

    public boolean isSelectedResource(String resource) {
        return selectedResources.contains(resource);
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

    public void enablePdfCreation() {
        generatePDF = true;
    }

    public void disablePdfCreation() {
        generatePDF = false;
    }

    public boolean createPDF() {
        return generatePDF;
    }
}
