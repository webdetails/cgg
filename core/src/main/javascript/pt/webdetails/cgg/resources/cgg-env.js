/*!
* Copyright 2002 - 2021 Webdetails, a Hitachi Vantara company.  All rights reserved.
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
