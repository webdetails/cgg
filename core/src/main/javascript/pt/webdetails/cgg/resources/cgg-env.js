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



/* globals lib */

// Rhino's ImporterTopLevel exposes the JavaImporter constructor also as the global "global" property.
// Make "global" be the JS global object instead, as scripts written for Node.js expect.
// (On Rhino < 1.7.14, which lacked globalThis, the Intl polyfill fell back to "global" to find the global object.)
globalThis.global = this;

// Global stuff - JS language shims
lib('intl/install.js');
lib('require-adapter.js');
lib('define-cfg.js');

require('cgg');
