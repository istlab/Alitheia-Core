/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package eu.sqooss.impl.service.pa;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.MetricInfo;

public class PACommandProvider implements CommandProvider {
    private PluginAdmin sobjPA = null;

    /* ===[ Constructors ]================================================ */

    public PACommandProvider (PluginAdmin pluginAdmin) {
        this.sobjPA = pluginAdmin;
    }

    public String getHelp() {
        StringBuffer help = new StringBuffer(32);
        help.append(
                "---SQO-OSS Plug-ins Administration Commands---\n\r");
        help.append(
                "\t" + "install_metric <id>"
                + " - " + "calls the install method of metric with"
                + "this service ID\n\r");
        help.append(
                "\t" + "im <id>"
                + " - " + "shortcut for \"install_metric\"\n\r");
        help.append(
                "\t" + "list_metrics"
                + " - " + "list all registered metric services\n\r");
        help.append(
                "\t" + "lm"
                + " - " + "shortcut for \"list_metrics\"\n\r");
        return help.toString();
    }

    /* ===[ Command shortcuts ]=========================================== */

    public void _im (CommandInterpreter ci) {
        _install_metric(ci);
    }

    public void _lm (CommandInterpreter ci) {
        _list_metrics(ci);
    }

    /* ===[ Command methods ]============================================= */

    public void _install_metric (CommandInterpreter ci) {
        // Retrieve the service ID from the command's parameters list
        String serviceId = ci.nextArgument();
        if ((serviceId != null) && (sobjPA != null)){
            try {
                // Trigger install on the selected metric
                if (sobjPA.installMetric(new Long(serviceId))) {
                    ci.println (
                            "[INFO]"
                            + " "
                            + "Install on metric with ID "
                            + serviceId + " was successfull.");
                }
                else {
                    ci.println (
                            "[ERROR]"
                            + " "
                            + "Install on metric with ID "
                            + serviceId + " was unsuccessfull!");
                }
            } catch (NumberFormatException e) {
                ci.println (
                        "[ERROR]"
                        + " "
                        + "The specified service ID is not a number");
            }
        }
    }

    public void _list_metrics (CommandInterpreter ci) {
        if (sobjPA != null) {
            // Dump a formated list of registered metrics, if any where found
            Collection<MetricInfo> metricsList = sobjPA.listMetrics();
            if ((metricsList != null) && (metricsList.isEmpty() == false)) {
                // Iterate through the available metrics
                Iterator<MetricInfo> listIterator = metricsList.iterator();
                while ( listIterator.hasNext()) {
                    MetricInfo nextMetric = listIterator.next();

                    ci.println(
                            "\r\nService ID : "
                            + nextMetric.getServiceID());

                    ci.println(
                            "  Registered by\t\t: "
                            + "[" + nextMetric.getBundleID() + "]"
                            + " " + nextMetric.getBundleName());

                    String[] metricClasses = nextMetric.getObjectClass();
                    if (metricClasses.length > 0) {
                        if (metricClasses.length == 1) {
                            ci.print("  Service class\t\t: ");
                        }
                        else {
                            ci.println("  Service classes\t: ");
                        }

                        for (int nextClass = 0;
                        nextClass < metricClasses.length;
                        nextClass++) {
                            if (metricClasses.length == 1) {
                                ci.println(metricClasses[nextClass]);
                            }
                            else {
                                ci.println("\t\t" + metricClasses[nextClass]);
                            }
                        }
                    }

                    ci.println (
                            "  Install performed\t: "
                            + (nextMetric.installed ? "yes" : "no"));

                    if (nextMetric.getMetricName() != null) {
                        ci.println(
                                "  Metric name\t\t: "
                                + nextMetric.getMetricName());
                    }

                    if (nextMetric.getMetricVersion() != null) {
                        ci.println(
                                "  Metric version\t: "
                                + nextMetric.getMetricVersion());
                    }
                }
            }
            else {
                //
                ci.println("No metrics found!");
            }
        }
        else {
            //
            ci.println("No PluginAdmin available!");
        }
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
