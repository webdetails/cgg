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

/*
 * querySelector and querySelectorAll implementation taken from
 * http://codereview.stackexchange.com/questions/12444/queryselectorall-shim-for-non-ie-browsers
 */

define(function() {

    var SVG_IMPL = 'batik';
    var A_slice = Array.prototype.slice;

    // DOM utils
    function unwrapNode(node) { return node._node || node; }

    function toDocument(_doc) {
        return (!_doc || _doc._node) ? _doc : createDocument(_doc);
    }

    function loadSvg(path) {
        return createDocument(globalThis._loadSvg(path));
    }

    // DOM shims
    function createWindow(cgg, doc, console) {
        doc = toDocument(doc);
        var loc = '';

        var win = {
            navigate:     loadPath,
            navigator:    {userAgent: ""},
            setTimeout:   globalThis.setTimeout,
            clearTimeout: globalThis.clearTimeout,
            Element:      Element,
            CSSStyleDeclaration: CSSStyleDeclaration,
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
            globalThis.window    = globalThis;
            globalThis.document  = doc;
            globalThis._document = doc._node;
            globalThis.console   = win.console;
            globalThis.navigate  = loadPath;
            globalThis.navigator = {userAgent: ""};
            globalThis.Element   = Element;
            globalThis.CSSStyleDeclaration = CSSStyleDeclaration;
            globalThis.clearTimeout = win.clearTimeout;
            globalThis.setTimeout   = win.setTimeout;
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
            getElementsByTagName: function(tagName) {
                var selectedNodes = _doc.getElementsByTagName(tagName);
                var convertedNodes = [];

                for(var i = 0; i < selectedNodes.length; i++) {
                    var elem = createElement(selectedNodes.item(i));
                    if(elem) convertedNodes.push(elem);
                }

                return convertedNodes;
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
            },
            querySelector: function(s) {
                return querySelector(s, this);
            },
            querySelectorAll: function(s) {
                return querySelectorAll(s, this);
            },
            write: function(s) {
                print("Ignoring not implemented document.write call: '" + s.substr(0, 20) + "...'.");
            }
        };
    }

    function Element(_el) {
        this._el = _el;

        // This is important so that elsewhere (e.g. def.mixin.copy),
        // this object is not seen as cloneable.
        this.constructor = Element;
    }

    Element.prototype = {

        get ownerDocument() {
            return createDocument(this._el.getOwnerDocument());
        },
        get _node() { return this._el; },
        get nodeName() {
            if(this._el) {
                return this._el.nodeName;
            }
        },

        get id() {
            var id = this._el.getAttribute("id");
            return id ? String(id) : null;
        },

        get tagName() {
            return this._el.tagName;
        },

        get className() {
            return (" " + (this._el.className || this._el.getAttribute("class")) + " ");
        },

        get childNodes() {
            var selectedNodes = this._el.getChildNodes();
            var convertedNodes = [];

            for(var i = 0; i < selectedNodes.length; i++) {
                var elem = createElement(selectedNodes.item(i));
                if(elem) convertedNodes.push(elem);
            }

            return convertedNodes;
        },

        get children() {
            var selectedNodes = this._el.getChildNodes();
            var convertedNodes = [];

            for(var i = 0; i < selectedNodes.length; i++) {
                var elem = createElement(selectedNodes.item(i));
                if(elem) convertedNodes.push(elem);
            }

            return convertedNodes;
        },

        get lastChild() { return createElement(this._el.getLastChild()); },
        get firstChild() { return createElement(this._el.getFirstChild()); },

        get style() {
            return createStyle(this._el.getStyle());
        },

        // NOTE: This doesn't really return computed values...
        get computedStyle() {
            return createStyle(this._el.getComputedStyle(this._el, null));
        },

        get parentNode() {
            if(this._el.getParentNode() == null) return null;
            return createElement(this._el.getParentNode());
        },

        set textContent(value) {
            this._el.textContent = value;
        },

        get textContent() {
            return this._el.textContent;
        },

        get namespaceURI() {
            return this._el.getNamespaceURI();
        },

        // def.describe uses this method to detect a DOM node...
        cloneNode: function() {
            throw new Error("Not supported");
        },

        addEventListener: function() {},

        /* We need to appendChild() the raw Java element,
         * but also need to return the wrapped element.
         * If the element wasn't wrapped to begin with, we must wrap it.
         */
        appendChild: function(e) {
            var _e = e._node;
            if(_e) {
                this._el.appendChild(_e);
                return e;
            }

            this._el.appendChild(e);
            return createElement(e);
        },
        removeChild: function(node) {
            this._el.removeChild(node._node || node);
            return node._node ? node : createElement(node);
        },

        insertBefore: function(newNode, refNode) {
            var newNodeEl = refNodeEl = null;
            if(newNode) newNodeEl = newNode._el;
            if(refNode) refNodeEl = refNode._el;
            return createElement(this._el.insertBefore(newNodeEl, refNodeEl));
        },

        getBBox: function() {
            return this._el.getBBox();
        },
        getElementById: function(id) {
            return createElement(this._el.getElementById(id));
        },

        getElementsByTagName: function(tagName) {
            var selectedNodes = this._el.getElementsByTagName(tagName);
            var convertedNodes = [];

            for(var i = 0; i < selectedNodes.length; i++) {
                var elem = createElement(selectedNodes.item(i));
                if(elem) convertedNodes.push(elem);
            }

            return convertedNodes;
        },
        getAttribute: function(attrName) {
            return this._el.getAttribute(attrName);
        },
        setAttribute: function(attrName, value) {
            this._el.setAttribute(attrName, value);
        },
        setAttributeNS: function(ns, attrName, value) {
            this._el.setAttributeNS(ns,attrName, value);
        },
        removeAttribute: function(attrName) {
            this._el.removeAttribute(attrName);
        },
        querySelector: function(s) {
            return querySelector(s, this);
        },
        querySelectorAll: function(s) {
            return querySelectorAll(s, this);
        }
    };

    function createElement(_el) {
        if(_el) {
            return new Element(_el);
        }
        return null;
    }

    function CSSStyleDeclaration(_style) {
        this._style = _style;
    }

    CSSStyleDeclaration.prototype = {
        setProperty: function(name, value, prio) {
            try {
                this._style.setProperty(
                    name,
                    value,
                    (typeof prio === "undefined" ? null : prio));
            } catch(e) {
                if(!(e.javaException.getClass().getName() == "org.w3c.dom.DOMException"))
                    throw e;
            }
        },
        removeProperty: function(name) { this._style.removeProperty(name); },
        getProperty:    function(name) { return this._style.getPropertyValue(name); }
    };

    function createStyle(_style) {
        if(_style) return new CSSStyleDeclaration(_style);
        return null;
    }

    function createConsole(printer) {
        var console = {
            // Override to provide better serialization of arguments.
            stringify: String,

            logNode: function(node) {
                printer.print(globalThis._xmlToString(unwrapNode(node)));
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

    function querySelector(sel, node) {
        var ret = querySelectorAll(sel, node);
        if( ret.length > 0) return ret[0];
        else return null;
    }

    function querySelectorAll(sel, node) {
        var sels = sel.split(","),
            run = function(node,selector) {
                var sel = selector.split(/[ >]+/), com = selector.match(/[ >]+/g) || [], s, c, ret = [node], nodes, l, i, subs, m, j, check, x, w, ok,
                    as;
                com.unshift(" ");
                while(s = sel.shift()) {
                    c = com.shift();
                    if( c) c = c.replace(/^ +| +$/g,"");
                    nodes = ret.slice(0);
                    ret = [];
                    l = nodes.length;
                    subs = s.match(/[#.[]?[a-z_-]+(?:='[^']+'|="[^"]+")?]?/gi);
                    m = subs.length;
                    for( i=0; i<l; i++) {
                        if( subs[0].charAt(0) == "#") ret = [document.getElementById(subs[0].substr(1))];
                        else {
                            check = c == ">" ? nodes[i].children : nodes[i].getElementsByTagName("*");
                            if( !check) continue;
                            w = check.length;
                            for( x=0; x<w; x++) {
                                ok = true;
                                for( j=0; j<m; j++) {
                                    switch(subs[j].charAt(0)) {
                                        case ".":
                                            if( !check[x].className.match(new RegExp("\\b"+subs[j].substr(1)+"\\b"))) ok = false;
                                            break;
                                        case "[":
                                            as = subs[j].substr(1,subs[j].length-2).split("=");
                                            if( !check[x].getAttribute(as[0])) ok = false;
                                            else if( as[1]) {
                                                as[1] = as[1].replace(/^['"]|['"]$/g,"");
                                                if( check[x].getAttribute(as[0]) != as[1]) ok = false;
                                            }
                                            break;
                                        default:
                                            if( check[x].tagName.toLowerCase() != subs[j].toLowerCase()) ok = false;
                                            break;
                                    }
                                    if( !ok) break;
                                }
                                if( ok) ret.push(check[x]);
                            }
                        }
                    }
                }
                return ret;
            }, l = sels.length, i, ret = [], tmp, m, j;
        for( i=0; i<l; i++) {
            tmp = run(node,sels[i]);
            m = tmp.length;
            for( j=0; j<m; j++) {
                ret.push(tmp[j]);
            }
        }
        return ret;
    }

    return {
        document: createDocument,
        element:  createElement,
        style:    createStyle,
        window:   createWindow,
        console:  createConsole,
        loadSvg:  loadSvg,
        run:      globalThis.__timer__run__
    };
});
