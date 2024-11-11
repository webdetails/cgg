/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package pt.webdetails.cgg;

import java.util.Locale;
import java.util.Map;

public class DrawParameters {

  private final String scriptFile;
  private final String scriptType;
  private final String outputType;
  private final int width;
  private final int height;
  private final boolean isMultiPage;
  private final Locale locale;
  private final Map<String, Object> genericParamMap;

  public DrawParameters( String scriptFile,
                         String scriptType,
                         String outputType,
                         int width,
                         int height,
                         Map<String, Object> genericParamMap ) {
    this( scriptFile, scriptType, outputType, width, height, false, genericParamMap );
  }

  public DrawParameters( String scriptFile,
                         String scriptType,
                         String outputType,
                         int width,
                         int height,
                         boolean isMultiPage,
                         Map<String, Object> genericParamMap ) {
    this( scriptFile, scriptType, outputType, width, height, isMultiPage, Locale.getDefault(), genericParamMap );
  }

  public DrawParameters( String scriptFile,
                         String scriptType,
                         String outputType,
                         int width,
                         int height,
                         boolean isMultiPage,
                         Locale locale,
                         Map<String, Object> genericParamMap ) {
    this.scriptFile = scriptFile;
    this.scriptType = scriptType;
    this.outputType = outputType;
    this.width = width;
    this.height = height;
    this.isMultiPage = isMultiPage;
    this.locale = locale;
    this.genericParamMap = genericParamMap;
  }

  public String getScriptFile() {
    return scriptFile;
  }

  public String getScriptType() {
    return scriptType;
  }

  public String getOutputType() {
    return outputType;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public boolean isMultiPage() {
    return isMultiPage;
  }

  public Locale getLocale() {
    return locale;
  }

  public Map<String, Object> getGenericParameters() {
    return genericParamMap;
  }
}
