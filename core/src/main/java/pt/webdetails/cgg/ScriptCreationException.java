/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package pt.webdetails.cgg;

public class ScriptCreationException extends Exception {

  private static final long serialVersionUID = 2053090721740776501L;

  public ScriptCreationException() {
  }

  public ScriptCreationException( final String message ) {
    super( message );
  }

  public ScriptCreationException( final String message, final Throwable cause ) {
    super( message, cause );
  }

  public ScriptCreationException( final Throwable cause ) {
    super( cause );
  }
}
