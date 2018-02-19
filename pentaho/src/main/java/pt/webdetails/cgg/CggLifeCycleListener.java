/*!
* Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company.  All rights reserved.
* 
* This software was developed by Webdetails and is provided under the terms
* of the Mozilla Public License, Version 2.0, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
* the license for the specific language governing your rights and limitations.
*/

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

