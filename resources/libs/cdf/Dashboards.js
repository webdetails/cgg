/*!
* Copyright 2002 - 2013 Webdetails, a Pentaho company.  All rights reserved.
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
