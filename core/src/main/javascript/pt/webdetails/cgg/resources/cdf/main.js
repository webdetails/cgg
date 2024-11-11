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
