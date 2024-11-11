/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package pt.webdetails.cgg;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IPluginLifecycleListener;
import org.pentaho.platform.api.engine.PluginLifecycleException;

import pt.webdetails.cgg.output.DefaultOutputFactory;
import pt.webdetails.cgg.output.PngOutputHandler;
import pt.webdetails.cgg.output.SVGOutputHandler;

public class CggLifeCycleListener implements IPluginLifecycleListener {

	static Log logger = LogFactory.getLog(CggLifeCycleListener.class);

	@Override
	public void init() throws PluginLifecycleException {
		logger.debug("Init for CGG");
    
    DefaultOutputFactory.getInstance().register("svg", SVGOutputHandler.class);
    DefaultOutputFactory.getInstance().register("png", PngOutputHandler.class);

	}

	@Override
	public void loaded() throws PluginLifecycleException {
		logger.debug("Load for CGG");
	}

	@Override
	public void unLoaded() throws PluginLifecycleException {
		logger.debug("Unload for CGG");
	}

}

