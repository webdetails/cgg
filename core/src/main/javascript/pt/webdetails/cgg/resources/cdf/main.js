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
define([
    'cgg',
    'Base',
    'jquery',
    './Dashboards',
    './components/BaseComponent',
    './components/UnmanagedComponent',

    // Force these to load,
    //  so that they can register their
    //  Component Classes or Class Handlers
    './components/ccc/BaseCccComponent',
    './components/ProtovisComponent'
], function(cgg, Base, $, Dashboards, BaseComponent, UnmanagedComponent) {

    globalThis.Base = Base;
    globalThis.$ = globalThis.jQuery = $;
    globalThis.Dashboards = Dashboards;
    globalThis.BaseComponent = BaseComponent;
    globalThis.UnmanagedComponent = UnmanagedComponent;

    // Register cgg renderer.
    cgg.registerRenderer({render: renderComponent});

    return {};

    function renderComponent(component) {
        Dashboards.bindControl(component).update();
    }
});
