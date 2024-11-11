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


/* globals lib */

// Intl polyfill requires this.
var globalThis = this;

// Rhino binds "global" to some apparently unnecessary native function.
// This messes up scripts that expect the global "global" property to
// be, actually, the JS global object.
globalThis.global = this;

// Global stuff - JS language shims
lib('intl/install.js');
lib('require-adapter.js');
lib('define-cfg.js');

require('cgg');
