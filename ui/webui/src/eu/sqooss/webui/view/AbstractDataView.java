package eu.sqooss.webui.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.webui.ListView;
import eu.sqooss.webui.Project;
import eu.sqooss.webui.SelectedSettings;
import eu.sqooss.webui.settings.BaseDataSettings;

public abstract class AbstractDataView extends ListView {
    /*
     * Holds the selected project's object.
     */
    protected Project project;

    /*
     * Holds the view specific session settings.
     */
    BaseDataSettings viewConf;

    /*
     * Holds the current metric selection (as a list of metric Ids).
     */
    protected List<Long> selectedMetrics = new ArrayList<Long>();

    /*
     * Holds the mnemonic name of the currently highlighted metric.
     */
    protected String highlightedMetric = null;

    /*
     * Holds the list of metrics that were evaluated on the displayed
     * resource type (<i> like file, version, developer, etc.</i>).
     */
    protected Map<Long, String> evaluated = new HashMap<Long, String>();

    /*
     * Holds the selected display type
     */
    protected int chartType = TABLE_CHART;
    /*
     * Definitions of the various result display types
     */
    public static final int TABLE_CHART = 2;
    public static final int LINE_CHART = 4;
    public static final int PIE_CHART = 8;
    public static final int BAR_CHART = 16;
    /*
     * Defines all charts which are supported by this view.
     */
    public int supportedCharts = TABLE_CHART;

    /**
     * Sets the user settings for this session.
     * 
     * @param settings the new settings
     */
    public void setSettings(SelectedSettings settings, int subset) {
        this.settings = settings;
        this.viewConf = settings.getDataSettings(subset);
    }

    /**
     * Sets the current selection of metrics evaluated on the displayed
     * resource type, that will be presented in this view.
     * 
     * @param metrics the array of selected metrics (<i>their metric Ids</i>).
     */
    public void setSelectedMetrics(String[] metrics) {
        if (metrics != null)
            for (String metricId : metrics) {
                try {
                    Long value = new Long(metricId);
                    if (selectedMetrics.contains(value) == false)
                        selectedMetrics.add(value);
                }
                catch (NumberFormatException ex) { /* Do nothing */ }
            }
    }

    /**
     * Sets the mnemonic name of the metric that will be highlighted in the
     * generated <b>Results</b> panel.
     * 
     * @param mnemonic the metric's mnemonic name
     */
    public void setHighlightedMetric(String mnemonic) {
        this.highlightedMetric = mnemonic;
    }

    /**
     * Sets the type of the display, which will be presenting the evaluation
     * results in the generated <b>Results</b> panel.
     * 
     * @param chartType one of the display types supported by this view
     */
    public void setChartType(int chartType) {
        this.chartType = chartType;
    }
}
