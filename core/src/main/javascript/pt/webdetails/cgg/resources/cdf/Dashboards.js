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
    './util',
    './dash',
    './components/BaseComponent',
    'jquery'
], function(util, Dashboards, BaseComponent, $) {

    Dashboards.bindControl = bindControl;
    Dashboards.bindExistingControl = bindExistingControl;

    return Dashboards;

    function bindControl(control, Class, scope) {
        if(!Class) Class = this.getControlClass(control, scope);
        if(!Class)
            this.log("Object type " + control.type +
                     " can't be mapped to a valid class", "error");
        else
            castControlToClass(control, Class);

        return this.bindExistingControl(control, Class);
    }

    function bindExistingControl(control, Class) {
        if(!control.dashboard) {
            control.dashboard = this;
            // Ensure BaseComponent's methods
            castControlToComponent(control, Class);
        }
        return control;
    }

    function castControlToClass(control, Class) {
        if(!(control instanceof Class)) {
            var controlImpl = util.makeInstance(Class);

            // Copy implementation into control
            $.extend(control, controlImpl);
        }
    }

    function castControlToComponent(control, Class) {
        // Extend control with BaseComponent methods, if it's not an instance of it.
        // Also, avoid extending if _Class_ was already applied
        // and it is a subclass of BaseComponent.
        if(!(control instanceof BaseComponent) &&
           (!Class || !(Class.prototype instanceof BaseComponent))) {

            var baseProto = BaseComponent.prototype;
            for(var p in baseProto) {

                if(util.hasOwn.call(baseProto, p) &&
                   (control[p] === undefined) &&
                   (typeof baseProto[p] === 'function') &&
                   (p !== 'base')) {

                    control[p] = baseProto[p];
                }
            }
        }
    }
});
