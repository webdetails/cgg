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
