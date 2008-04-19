/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
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
import org.osgi.framework.Constants;

import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;

public class PACommandProvider implements CommandProvider {
    private PluginAdmin sobjPA = null;

    /* ===[ Constructors ]================================================ */

    public PACommandProvider (PluginAdmin pluginAdmin) {
        this.sobjPA = pluginAdmin;
    }

    public String getHelp() {
        StringBuffer help = new StringBuffer();
        
        help.append("---SQO-OSS Plug-ins Administration Commands---\n\r");
        help.append("\t install_plugin <id> - " +
                "calls the install method of plugin with this service ID\n\r");
        help.append("\t im <id> - shortcut for \"install_plugin\"\n\r");
        help.append("\t list_metrics - " +
        		"list all registered metric services\n\r");
        help.append("\t lm - shortcut for \"list_metrics\"\n\r");
        help.append("\t remove_plugin <id> - "
                        + "removes the plugin with this service ID\n\r");
        help.append("\t rm <id> - shortcut for \"remove_plugin\"\n\r");
        return help.toString();
    }

    /* ===[ Command shortcuts ]=========================================== */

    public void _ip (CommandInterpreter ci) {
        _install_plugin(ci);
    }

    public void _lp (CommandInterpreter ci) {
        _list_plugins(ci);
    }
    
    public void _rp (CommandInterpreter ci) {
        _remove_plugin(ci);
    }

    /* ===[ Command methods ]============================================= */

    public void _install_plugin (CommandInterpreter ci) {
        // Retrieve the service ID from the command's parameters list
        String serviceId = ci.nextArgument();
        if ((serviceId != null) && (sobjPA != null)){
            try {
                // Trigger install on the selected metric
                if (sobjPA.installPlugin(new Long(serviceId))) {
                    ci.println (
                            "[INFO]"
                            + " "
                            + "Install on plug-in with ID "
                            + serviceId + " was successfull.");
                }
                else {
                    ci.println (
                            "[ERROR]"
                            + " "
                            + "Install on plug-in with ID "
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

    public void _list_plugins (CommandInterpreter ci) {
        if (sobjPA == null) {
            ci.println("No PluginAdmin available!");
            return;
        }
        
        Collection<PluginInfo> metricsList = sobjPA.listPlugins();
        if ((metricsList == null) || (metricsList.isEmpty())) {
            ci.println("No plug-ins found!");
            return;
        }

        /* Iterate through the available metrics */
        Iterator<PluginInfo> listIterator = metricsList.iterator();
        while (listIterator.hasNext()) {
            PluginInfo nextMetric = listIterator.next();

            ci.println("\r\nService ID : "
                    + nextMetric.getServiceRef().getProperty(
                            Constants.SERVICE_ID));

            ci.println("  Registered by bundle\t: " + "["
                    + nextMetric.getServiceRef().getBundle().getBundleId()
                    + "] "
                    + nextMetric.getServiceRef().getBundle().getSymbolicName());

            Iterator<Class<? extends DAObject>> i = nextMetric.getActivationTypes().iterator();
            ci.print("  Activation type(s)\t: ");
            if(i.hasNext()) {
                ci.println(i.next().getName());
            }
            while (i.hasNext()) {
                ci.println("\t\t\t " + i.next().getName());
            }


            ci.println("  Installed? \t\t: "
                    + (nextMetric.installed ? "yes" : "no"));
            
            ci.println("  Plugin name\t\t: " + nextMetric.getPluginName());
            ci.println("  Plugin version\t: " + nextMetric.getPluginVersion());
            
            ci.println("  Supported Metrics\t: ");
            
        }
    }
    
    public void _remove_plugin(CommandInterpreter ci) {
        String serviceId = ci.nextArgument();
        if ((serviceId != null) && (sobjPA != null)){
           try {
            boolean rm = sobjPA.uninstallPlugin(new Long(serviceId));
            if(!rm) {
                ci.println("[ERROR] Uninstall of metric with service id" +
                        serviceId + " failed.");
            } else {
                ci.println("[ERROR] Uninstall of metric with service id" +
                        serviceId + " was successful.");
            }
           } catch (NumberFormatException e) {
               ci.println (
                       "[ERROR]"
                       + " "
                       + "The specified service ID is not a number");
           }
        }
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
