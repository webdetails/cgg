/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/

package pt.webdetails.cgg.scripts;

import java.util.TimeZone;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

/**
 * The factory of the Rhino contexts in which CGG runs scripts.
 * <p>
 * Contexts use the UTC time zone, instead of the JVM default time zone.
 * <p>
 * Chart data dates have no time zone: they are built with {@code new Date(y, m, d, ...)}, in the context's local
 * time zone, and are formatted back to the same local date, by both {@code Date} methods and by the
 * {@code Intl.DateTimeFormat} polyfill, which is set to use the context's time zone (see {@code intl/install.js}).
 * <p>
 * Unlike real time zones, UTC has no daylight saving time transitions, which could otherwise make some local dates
 * and times not exist, or exist twice. Also, the {@code Intl} polyfill cannot use the JVM default time zone: it
 * only knows the time zone data it bundles, and Rhino does not expose the identifier of the time zone it uses.
 * <p>
 * Only the time zone of CGG's Rhino contexts is changed; the JVM default time zone is not.
 */
public class CggContextFactory extends ContextFactory {

  public static final TimeZone TIME_ZONE = TimeZone.getTimeZone( "UTC" );

  @Override
  protected Context makeContext() {
    final Context cx = super.makeContext();
    cx.setTimeZone( TIME_ZONE );
    return cx;
  }
}
