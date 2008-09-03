package eu.sqooss.webui.view;

import java.util.ArrayList;
import java.util.List;

import eu.sqooss.webui.Functions;
import eu.sqooss.webui.Metric;
import eu.sqooss.webui.Project;
import eu.sqooss.webui.SelectedSettings;
import eu.sqooss.webui.Metric.MetricActivator;
import eu.sqooss.webui.Metric.MetricType;
import eu.sqooss.webui.datatype.Developer;
import eu.sqooss.webui.settings.BaseDataSettings;

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
     * @param selected the array of selected resources (<i>their
     *   unique identifier</i>).
     */
    public void setSelectedResources(String[] selected) {
        if (selected != null)
            for (String resource : selected) {
                if ((selectedResources.contains(resource) == false)
                        && (project.getDevelopers().getDeveloperByUsername(resource) != null))
                    selectedResources.add(resource);
            }

        // Cleanup the session variable from invalid entries
        String[] validResources = selectedResources.toArray(
                new String[selectedResources.size()]);
        viewConf.setSelectedResources(validResources);
    }

    /**
     * Loads all the necessary information, that is associated with the
     * resources presented in this view.
     */
    private void loadData () {
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

            BaseDataSettings viewConf = settings.getDataSettings(
                    SelectedSettings.DEVELOPERS_DATA_SETTINGS);
            if (viewConf != null) {
                // Load the list of selected metrics
                setSelectedMetrics(viewConf.getSelectedMetrics());

                // Load the list of selected versions
                setSelectedResources(viewConf.getSelectedResources());
            }
        }
    }

    @Override
    public String getHtml(long indentationDepth) {
        // TODO Auto-generated method stub
        return null;
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
}
