package eu.sqooss.webui.view;

import java.awt.Color;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleInsets;

import eu.sqooss.webui.Functions;
import eu.sqooss.webui.Metric;
import eu.sqooss.webui.Project;
import eu.sqooss.webui.Result;
import eu.sqooss.webui.SelectedSettings;
import eu.sqooss.webui.Metric.MetricActivator;
import eu.sqooss.webui.Metric.MetricType;
import eu.sqooss.webui.datatype.Developer;
import eu.sqooss.webui.settings.BaseDataSettings;

/**
 * The class <code>VersionDataView</code> renders an HTML sequence that
 * verbosely presents metric result that were evaluated on the project
 * developers in a single project.
 */
public class DeveloperDataView extends AbstractDataView {
    /*
     * Holds the list of selected resources (<i>a list of developer names</i>).
     */
    private List<String> selectedResources = new ArrayList<String>();

    /**
     * Instantiates a new <code>DeveloperDataView</code> object,
     * and initializes it with the given project object.
     * 
     * @param project the project object
     */
    public DeveloperDataView(Project project) {
        super();
        this.project = project;
    }

    /**
     * Sets the resources which this view will present as selected.
     * 
     * @param selected the array of selected resources
     *   (<i>a list of developer usernames</i>).
     */
    private void setSelectedResources(String[] selected) {
        if (selected != null)
            for (String resource : selected) {
                if ((selectedResources.contains(resource) == false)
                        && (project.getDevelopers().getDeveloperByUsername(resource) != null))
                    selectedResources.add(resource);
            }

        // Cleanup the corresponding session variable from invalid entries
        String[] validResources = selectedResources.toArray(
                new String[selectedResources.size()]);
        viewConf.setSelectedResources(validResources);
    }

    /**
     * Loads all the necessary information, that is associated with the
     * resources presented in this view.
     */
    private void loadData() {
        if ((project != null) && (project.isValid())) {
            // Pre-load the selected project developers
            project.getDevelopers();

            /*
             * Load the list of metrics that were evaluated on this project
             * and are related to the presented resource type
             */
            evaluated = project.getEvaluatedMetrics().getMetricMnemonics(
                    MetricActivator.DEVELOPER,
                    MetricType.PROJECT_WIDE);

            if (viewConf != null) {
                // Load the list of selected metrics
                setSelectedMetrics(viewConf.getSelectedMetrics());

                // Load the list of selected versions
                setSelectedResources(viewConf.getSelectedResources());
            }
        }
    }

    @Override
    public String getHtml(long in) {
        if ((project == null) || (project.isValid() == false))
            return(sp(in) + Functions.error("Invalid project!"));

        // Hold the accumulated HTML content
        StringBuilder b = new StringBuilder("");

        // Load the various resource data
        loadData();

        if (project.getDevelopersCount() < 1) {
            b.append(sp(in)
                    + Functions.error("This project has no developers!"));
        }
        else if ((selectedMetrics.isEmpty()) || (selectedResources.isEmpty())) {
            b.append(sp(in)
                    + "Select one or more metrics and developers.");
        }
        else {
            //----------------------------------------------------------------
            // Cleanup procedures
            //----------------------------------------------------------------
            /*
             * Clear the highlighted metric variable, in case the selected
             * metrics list is narrowed to a single metric only.
             */
            if (selectedMetrics.size() == 1)
                highlightedMetric = null;

            //----------------------------------------------------------------
            // Assemble the results dataset
            //----------------------------------------------------------------
            /*
             * Data set format:
             * < metric_mnemonic < username, evaluation_value > >
             */
            SortedMap<String, SortedMap<String, String>> data =
                new TreeMap<String, SortedMap<String,String>>();
            // Prepare the data set
            for (Long metricId : selectedMetrics) {
                Metric metric =
                    project.getEvaluatedMetrics().getMetricById(metricId);
                if (metric != null)
                    data.put(
                            metric.getMnemonic(),
                            new TreeMap<String, String>());
            }
            // Fill the data set
            for (String resource : selectedResources) {
                Developer resourceObj =
                    project.getDevelopers().getDeveloperByUsername(resource);
                if (resourceObj != null) {
                    resourceObj.setTerrier(terrier);
                    HashMap<String, Result> verResults =
                        resourceObj.getResults(
                                evaluated.values(),
                                resourceObj.getId());
                    for (Long metricId : selectedMetrics) {
                        Metric metric = project.getEvaluatedMetrics()
                            .getMetricById(metricId);
                        if (metric != null) {
                            Result result = verResults.get(
                                    metric.getMnemonic());
                            if (result != null) {
                                result.setSettings(settings);
                                data.get(metric.getMnemonic()).put(
                                        resource,
                                        result.getHtml(0));
                            }
                            else {
                                data.get(metric.getMnemonic()).put(
                                        resource, null);
                            }
                        }
                    }
                }
            }

            //----------------------------------------------------------------
            // Display the results in the selected form
            //----------------------------------------------------------------
            String chartFile = null;
            switch (chartType) {
            case TABLE_CHART:
                b.append(tableChart(in, data));
                break;
            case BAR_CHART:
                /*
                 * Generate the results chart.
                 */
                if ((highlightedMetric != null)
                        && (data.containsKey(highlightedMetric)))
                    chartFile = barChart(
                            data.subMap(highlightedMetric, highlightedMetric +"\0"));
                else
                    chartFile = barChart(data);
                /*
                 * Display the generated results chart.
                 */
                if (chartFile != null) {
                    chartFile = "/tmp/" + chartFile;
                    b.append(sp(in++) + "<table"
                            + " style=\"margin-top: 0;\">\n");
                    /*
                     * Display the aggregation chart's option, only if results
                     * for at least two metrics are available. Otherwise
                     * display only a single metric's option.
                     */
                    String leadOption = "ALL";
                    if (selectedMetrics.size() == 1) {
                        leadOption = data.firstKey();
                    }
                    b.append(sp(in++) + "</tr>\n");
                    if ((highlightedMetric != null)
                            && (data.containsKey(highlightedMetric)))
                        b.append(sp(in) + "<td class=\"vfv_chart_title\">"
                                + "<a href=\"" 
                                + getServletPath()
                                + "\">"
                                + "ALL" + "</a>"
                                + "</td>\n");
                    else
                        b.append(sp(in) + "<td"
                                + " class=\"vfv_chart_title_selected\">"
                                + leadOption
                                + "</td>\n");
                    /*
                     * Display the chart cell
                     */
                    int chartRowSpan = 2;
                    if (data.size() > 1)
                        chartRowSpan += data.size();
                    b.append(sp(in) + "<td"
                            + " class=\"vfv_chart_image\""
                            + " rowspan=\"" + chartRowSpan + "\">"
                            + "<a class=\"vfvchart\""
                            + " href=\"/fullscreen.jsp?"
                            + "chartfile=" + chartFile + "\">"
                            + "<img src=\"" + chartFile + "\">"
                            + "</a>"
                            + "</td>\n");
                    b.append(sp(--in) + "</tr>\n");
                    /*
                     * Display a chart option for each of the selected metrics,
                     * unless only one metric is selected.
                     */
                    if (selectedMetrics.size() > 1) {
                        for (String mnemonic : data.keySet()) {
                            b.append(sp(in++) + "<tr>\n");
                            if ((highlightedMetric != null)
                                    && (highlightedMetric.equals(mnemonic)))
                                b.append(sp(in) + "<td"
                                        + " class=\"vfv_chart_title_selected\">"
                                        + mnemonic
                                        + "</td>\n");
                            else
                                b.append(sp(in) + "<td"
                                        + " class=\"vfv_chart_title\">"
                                        + "<a href=\"" 
                                        + getServletPath()
                                        + "?vfvsm=" + mnemonic
                                        + "\">"
                                        + mnemonic + "</a>"
                                        + "</td>\n");
                            b.append(sp(--in) + "</tr>\n");
                        }
                    }
                    /*
                     * Display an empty transparent cell to align the options
                     * row with the chart row. 
                     */
                    b.append(sp(in++) + "<tr>\n");
                    b.append(sp(in) + "<td"
                            + " class=\"vfv_chart_title_empty\">"
                            + "&nbsp;"
                            + "</td>\n");
                    b.append(sp(--in) + "</tr>\n");
                    b.append(sp(--in) + "</table>\n");
                }
                else
                    b.append(Functions.information(
                            "Inapplicable results."));
                break;
            default:
                b.append(tableChart(in, data));
                break;
            }

        }
        return b.toString();
    }

    /**
     * Renders an info panel related to the selected project developers
     * 
     * @param in the indentation depth
     * 
     * @return The generated HTML content.
     */
    public String getInfo (long in) {
        if ((project == null) || (project.isValid() == false))
            return(sp(in) + Functions.error("Invalid project!"));

        // Hold the accumulated HTML content
        StringBuilder b = new StringBuilder("");

        // Load the various resource data
        loadData();

        Developer highlighted = null;
        if (viewConf.getHighlightedResource() != null)
            highlighted = project.getDevelopers().getDeveloperByUsername(
                    viewConf.getHighlightedResource());

        if (project.getDevelopersCount() < 1) {
            b.append(sp(in)
                    + Functions.error("This project has no developers!"));
        }
        else if (highlighted == null) {
            b.append(sp(in++) + "<table>\n");

            // Project name
            b.append(sp(in) + "<tr>"
                    + "<td><b>Project</b></td>"
                    + "<td>" + project.getName() + "</td>"
                    + "</tr>\n");

            // Developers number
            b.append(sp(in) + "<tr>"
                    + "<td><b>Developers</b></td>"
                    + "<td>" + project.getDevelopersCount() + "</td>"
                    + "</tr>\n");

            b.append(sp(--in) + "</table>\n");
        }
        else {
            b.append(sp(in++) + "<table>\n");

            // Full name
            b.append(sp(in) + "<tr>"
                    + "<td><b>Real name</b></td>"
                    + "<td>"
                    + (highlighted.getName() != null
                            ? highlighted.getName()
                            : "N/A")
                    + "</td>"
                    + "</tr>\n");

            // User name
            b.append(sp(in) + "<tr>"
                    + "<td><b>Username</b></td>"
                    + "<td>"
                    + highlighted.getUsername()
                    + "</td>"
                    + "</tr>\n");

            // Email address
            b.append(sp(in) + "<tr>"
                    + "<td><b>Email</b></td>"
                    + "<td>"
                    + (highlighted.getEmail() != null
                            ? highlighted.getEmail() 
                            : "N/A")
                    + "</td>"
                    + "</tr>\n");

            b.append(sp(--in) + "</table>\n");
        }

        return b.toString();
    }

    /**
     * Renders a control panel, that can be used for controlling various
     * rendering features of this view.
     * 
     * @param in the indentation depth
     * 
     * @return The generated HTML content.
     */
    public String getControls (long in) {
        if ((project == null) || (project.isValid() == false))
            return(sp(in) + Functions.error("Invalid project!"));

        // Hold the accumulated HTML content
        StringBuilder b = new StringBuilder("");

        // Load the various resource data
        loadData();

        if (project.getDevelopersCount() < 1) {
            b.append(sp(in)
                    + Functions.error("This project has no developers!"));
        }
        else {
            b.append(sp(in++) + "<form>\n");

            /*
             * Render the list of metrics that were evaluated on this project
             * and are related to the presented resource type
             */
            b.append(sp(in++) + "<div class=\"vvvmid\">\n");
            b.append(sp(in) + "<div class=\"vvvtitle\">Metrics</div>\n");
            b.append(sp(in++) + "<select class=\"vvvmid\""
                    + " name=\"selMetrics\""
                    + " multiple"
                    + " size=\"5\""
                    + ((evaluated.isEmpty()) ? " disabled" : "")
                    + ">\n");
            for (String mnemonic : evaluated.values()) {
                Metric metric = project.getEvaluatedMetrics()
                    .getMetricByMnemonic(mnemonic);
                if (metric != null)
                    b.append(sp(in) + "<option class=\"vvvmid\""
                            + ((selectedMetrics.contains(metric.getId()))
                                    ? " selected" : "")
                            + " value=\"" + metric.getId() + "\">"
                            + "" + mnemonic
                            + "</option>\n");
            }
            b.append(sp(--in) + "</select>\n");
            b.append(sp(--in) + "</div>\n");

            /*
             * Render the list of resources that were selected by the user.
             */
            b.append(sp(in++) + "<div class=\"vvvvid\">\n");
            b.append(sp(in) + "<div class=\"vvvtitle\">Developers</div>\n");
            b.append(sp(in++) + "<select class=\"vvvvid\""
                    + " name=\"selResources\""
                    + " multiple"
                    + " size=\"5\""
                    + ((selectedResources.size() < 1) ? " disabled" : "")
                    + ">\n");
            for (String resource : selectedResources) {
                b.append(sp(in) + "<option class=\"vvvvid\""
                        + " selected"
                        + " value=\"" + resource + "\">"
                        + resource
                        + "</option>\n");
            }
            b.append(sp(--in) + "</select>\n");

            b.append(sp(--in) + "</div>\n");
            b.append(sp(in++) + "<div style=\"position: relative; clear: both; padding-top: 5px; border: 0; text-align: center;\">\n");
            b.append(sp(in) + "<input type=\"submit\" value=\"Apply\">\n");
            b.append(sp(--in)+ "</div>\n");
            b.append(sp(--in) + "</form>\n");
        }

        return b.toString();
    }

    private String tableChart (
            long in,
            SortedMap<String, SortedMap<String, String>> values) {
        // Hold the accumulated HTML content
        StringBuilder b = new StringBuilder("");

        b.append(sp(in++) + "<table"
                + " style=\"width: " + (80 + 80*(values.size()))+ "px;\""
                + ">\n");

        //--------------------------------------------------------------------
        // Table header
        //--------------------------------------------------------------------
        b.append(sp(in++) + "<thead>\n");
        b.append(sp(in++) + "<tr>\n");
        b.append(sp(in) + "<td class=\"def_invisible\""
                + " style=\"width: 80px;\">"
                + "&nbsp;</td>\n");
        for (String mnemonic : values.keySet()) {
            Metric metric = project.getEvaluatedMetrics()
                    .getMetricByMnemonic(mnemonic);
            b.append(sp(in) + "<td class=\"def_head\""
                    + " style=\"width: 80px;\""
                    + " title=\"" + metric.getDescription() + "\">"
                    + mnemonic
                    + "</td>\n");
        }
        b.append(sp(--in) + "</tr>\n");
        b.append(sp(--in) + "</thead>\n");

        //--------------------------------------------------------------------
        // Display all available results per selected metric and resource
        //--------------------------------------------------------------------
        for (String resource : selectedResources) {
            b.append(sp(in++) + "<tr>\n");
            b.append(sp(in) + "<td class=\"def_head\">"
                    + resource 
                    + "</td>\n");
            for (String mnemonic : values.keySet()) {
                String result = null;
                if (values.get(mnemonic).get(resource) != null) {
                    result = values.get(mnemonic).get(resource).toString();
                    try {
                        NumberFormat localise = 
                            NumberFormat.getNumberInstance(
                                    settings.getUserLocale());
                        result = localise.format(new Double(result));
                    }
                    catch (NumberFormatException ex) { /* Do nothing */ }
                    catch (IllegalArgumentException ex) { /* Do nothing */ }
                }
                b.append(sp(in) + "<td class=\"def_right\">"
                        + ((result != null) ? result : "N/A")
                        + "</td>\n");
            }
            b.append(sp(--in) + "</tr>\n");
        }
        b.append(sp(--in) + "</table>\n");

        return b.toString();
    }

    private String barChart (SortedMap<String, SortedMap<String, String>> values) {
        // Construct the chart's dataset
        DefaultCategoryDataset data = new DefaultCategoryDataset();
        for (String nextMnemonic : values.keySet()) {
            SortedMap<String, String> subValues = values.get(nextMnemonic);
            for (String nextResource : subValues.keySet()) {
                try {
                    Double result = new Double(subValues.get(nextResource));
                    data.addValue(result, nextMnemonic, nextResource);
                }
                catch (NumberFormatException ex) {
                    return null;
                }
            }
        }

        // Generate the chart
        if (data.getColumnCount() > 0) {
            JFreeChart chart;
            chart = ChartFactory.createBarChart3D(
                    null, null, "Evaluation Results",
                    data, PlotOrientation.VERTICAL,
                    true, true, false);
            chart.setBackgroundPaint(new Color(0, 0, 0, 0));
            chart.setPadding(RectangleInsets.ZERO_INSETS);

            // Save the chart into a temporary file
            try {
                java.io.File tmpFile = java.io.File.createTempFile(
                        "img", ".png", settings.getTempFolder());
                ChartUtilities.saveChartAsPNG(tmpFile, chart, 640, 480);
                return tmpFile.getName();
            }
            catch (IOException e) { /* Do nothing */ }
        }

        return null;
    }
}
