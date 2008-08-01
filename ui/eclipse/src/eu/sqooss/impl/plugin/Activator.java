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

package eu.sqooss.impl.plugin;

import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Properties;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import eu.sqooss.impl.plugin.util.Constants;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    public static Properties configurationProperties;
    
	// The plug-in ID
	public static final String PLUGIN_ID = "SQO_OSS";

	// The shared instance
	private static Activator plugin;
	
	private static final String CONFIGURATION_FILE_NAME = "/OSGI-INF/configuration/plugin.properties";
	
	private static final String ICONS_FOLDER_NAME = "/OSGI-INF/configuration/icons/";
	
	private BundleContext bc;
	private Hashtable<String, ImageDescriptor> imageDescriptors;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		this.bc = context;
		plugin = this;
		initProperties();
		initializeImages();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		configurationProperties = null;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns the image descriptor for the given key.
	 * 
	 * @param key - image descriptor's key
	 * 
	 * @return - the image descriptor or null if the key is missing
	 */
	public ImageDescriptor getImageDescriptor(String key) {
	    return imageDescriptors.get(key);
	}

	private void initializeImages() {
	    imageDescriptors = new Hashtable<String, ImageDescriptor>(1);
        createImageDescriptor(Constants.IMG_OBJ_REPOSITORY);
    }
	
	private void createImageDescriptor(String key) {
	    URL url = bc.getBundle().getEntry(ICONS_FOLDER_NAME + key);
	    ImageDescriptor desc = ImageDescriptor.createFromURL(url);
	    imageDescriptors.put(key, desc);
	}
	
    private void initProperties() {
	    if (configurationProperties == null) {
	        URL propsUrl = bc.getBundle().getEntry(CONFIGURATION_FILE_NAME);
	        if (propsUrl != null) {
	            configurationProperties = new Properties();
	            try {
	                configurationProperties.load(propsUrl.openStream());
	            } catch (IOException e) {
	                configurationProperties = null;
	            }
	        }
	    }
	}
	
}

//vi: ai nosi sw=4 ts=4 expandtab
