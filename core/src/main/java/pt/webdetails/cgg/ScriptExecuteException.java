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

public class ScriptExecuteException extends Exception {

  private static final long serialVersionUID = 6952799693226978407L;

  public ScriptExecuteException() {
  }

  public ScriptExecuteException( final String message ) {
    super( message );
  }

  public ScriptExecuteException( final String message, final Throwable cause ) {
    super( message, cause );
  }

  public ScriptExecuteException( final Throwable cause ) {
    super( cause );
  }
}
