/*!
 * Copyright 2021 Webdetails, a Hitachi Vantara company.  All rights reserved.
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
