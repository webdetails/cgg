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
    './util'
], function(util) {

    var SVG_IMPL = 'batik';
    var A_slice = Array.prototype.slice;

    return {
        document: createDocument,
        element:  createElement,
        style:    createStyle,
        window:   createWindow,
        console:  createConsole,
        loadSvg:  loadSvg
    };

    // DOM utils
    function unwrapNode(node) { return node._node || node; }

    function toDocument(_doc) {
        return (!_doc || _doc._node) ? _doc : createDocument(_doc);
    }

    function loadSvg(path) {
        return createDocument(util.global._loadSvg(path));
    }

    // DOM shims
    function createWindow(cgg, doc, console) {
        doc = toDocument(doc);
        var loc = '';

        var win = {
            navigate: loadPath,
            get window()   { return this;    }, // !
            get document() { return doc;     },
            get console()  { return console; }
        };

        syncGlobal();

        function loadPath(path) {
            if(path && path !== loc) {
                doc = loadSvg(path);
                loc = path;
                syncGlobal();
            }
        }

        function syncGlobal() {
            if(cgg.useGlobal) {
                var global = util.global;
                global.window    = global;
                global.document  = doc;
                global._document = doc._node;
                global.console   = win.console;
                global.navigate  = loadPath;
            }
        }

        return win;
    }

    function createDocument(_doc) {
        return {
            get _node() { return _doc; },

            get implementation()    { return _doc.getImplementation(); },
            // this feeds pv.renderer
            get svgImplementation() { return SVG_IMPL; },
            get documentElement()   { return createElement(_doc.getDocumentElement()); },
            get lastChild()         { return createElement(_doc.getLastChild());  },
            get firstChild()        { return createElement(_doc.getFirstChild()); },

            /* We need to appendChild() the raw Java element,
             * but also need to return the wrapped element.
             * If the element wasn't wrapped to begin with, we must wrap it.
             */
            appendChild: function(e) {
                var _e = e._node;
                if(_e) {
                  _doc.appendChild(_e);
                  return e;
                }

                _doc.appendChild(e);
                return createElement(e);
            },
            getElementById: function(id) {
                return createElement(_doc.getElementById(id));
            },
            createElement: function(tagName) {
                return createElement(_doc.createElement(tagName));
            },
            createTextNode: function(text) {
                return createElement(_doc.createTextNode(text));
            },
            createElementNS: function(nameSpace, qualifiedName) {
                return createElement(_doc.createElementNS(nameSpace, qualifiedName));
            },
            removeChild: function(node) {
                _doc.removeChild(node._node || node);
                return node.node ? node : createElement(node);
            }
        };
    }

    function createElement(_el) {
        return {
            get _node() { return _el; },

            get lastChild() { return _el.getLastChild(); },
            get style()     { return createStyle(_el.getStyle()); },

            get parentNode() {
                if(_el.getParentNode() == null) return null;
                return createElement(_el.getParentNode());
            },

            addEventListener: function() {},

            /* We need to appendChild() the raw Java element,
             * but also need to return the wrapped element.
             * If the element wasn't wrapped to begin with, we must wrap it.
             */
            appendChild: function(e) {
                var _e = e._node;
                if(_e) {
                    _el.appendChild(_e);
                    return e;
                }

                _el.appendChild(e);
                return createElement(e);
            },
            removeChild: function(node) {
                _el.removeChild(node._node || node);
                return node.node ? node : createElement(node);
            },

            getBBox: function() {
                return _el.getBBox();
            },
            getElementsByTagName: function(tagName) {
                return _el.getElementsByTagName(tagName);
            },
            setAttribute: function(attrName, value) {
                _el.setAttribute(attrName, value);
            },
            setAttributeNS: function(ns, attrName, value) {
                _el.setAttributeNS(ns,attrName, value);
            },
            removeAttribute: function(attrName) {
                _el.removeAttribute(attrName);
            }
        };
    }

    function createStyle(_style) {
        return {
            setProperty: function(name, value, prio) {
                try {
                    _style.setProperty(
                        name,
                        value,
                        (typeof prio === "undefined" ? null : prio));
                } catch(e) {
                    if(!(e.javaException.getClass().getName() == "org.w3c.dom.DOMException"))
                        throw e;
                }
            },
            removeProperty: function(name) { _style.removeProperty(name); },
            getProperty:    function(name) { return _style.getPropertyValue(name); }
        };
    }

    function createConsole(printer) {
        var console = {
            // Override to provide better serialization of arguments.
            stringify: String,

            logNode: function(node) {
                printer.print(util.global._xmlToString(unwrapNode(node)));
            }
        };


        // Redirect console.XYZ to `doLog` (and, finally, to `printer.print`).
        ['log', 'debug', 'info', 'warn', 'group', 'groupCollapsed', 'groupEnd', 'error']
        .forEach(function(p) { console[p] = doLog; });

        return console;

        function doLog(mask) {
            try {
                var args = A_slice.call(arguments);

                if(mask) { args[0] = mask.replace('%s', ''); }

                var text = args.map(logArgToString).join(' ');

                printer.print(text);
            } catch(ex) {
                printer.print('Error writing to log: ' + ex);
            }
        }

        function logArgToString(s) {
            return !s || typeof s !== 'object'
                ? String(s)
                : console.stringify(s); // purposely virtual access
        }
    }
});
