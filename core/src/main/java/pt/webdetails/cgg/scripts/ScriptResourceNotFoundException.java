/*!
* Copyright 2002 - 2018 Webdetails, a Hitachi Vantara company.  All rights reserved.
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

package pt.webdetails.cgg.scripts;

public class ScriptResourceNotFoundException extends Exception {
  private static final long serialVersionUID = 5478744219228413516L;

  public ScriptResourceNotFoundException() {
  }

  public ScriptResourceNotFoundException( final String message ) {
    super( message );
  }

  public ScriptResourceNotFoundException( final String message, final Throwable cause ) {
    super( message, cause );
  }

  public ScriptResourceNotFoundException( final Throwable cause ) {
    super( cause );
  }

  public ScriptResourceNotFoundException( final String message, final Throwable cause, final boolean enableSuppression,
      final boolean writableStackTrace ) {
    super( message, cause, enableSuppression, writableStackTrace );
  }
}
