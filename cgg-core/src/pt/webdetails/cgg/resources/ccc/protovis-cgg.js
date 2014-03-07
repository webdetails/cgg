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
define(['cgg'], function(cgg) {

    return installPvCgg;

    function installPvCgg(pv) {
        // NOTE: init.js does:
        // pv.listen(window, "load", function() {
        //    ... document.getElementsByTagName("script")
        // }
        //
        // 1) document.readyState, document.addEventListener and document.attachEvent
        //    do are not defined in cgg.
        // 2) document.getElementsByTagName("script") would return an empty array
        //
        // So, in any way, the code is harmful as it doesn't have any effect.
        // This is why the init.js doesn't have an explicit batik exclusion anymore.

        // ------------------
        // Mark overrides
        pv.Panel.prototype._rootInstanceInitCanvas = function(s, c) {
            // NOOP
            // Don't know why. Check if it is really important
            // to prevent auto size detection from container element.
            // It may be cause pv.cssStyle does not work,
            // and this is not relevant anyway, as CGG always specifies size upfront.
        };

        pv.Panel.prototype._rootInstanceGetInlineCanvas = function(s) {
            return cgg.win.document.lastChild;
        };

        // ------------------
        // Scene overrides
        pv.SvgScene.applyCanvasStyle = function(canvas) {
          // NOOP
        };

        pv.SvgScene.createRootPanelElement = function() {
          return this.create("g");
        };

        pv.SvgScene.setStyle = function(e, style) {
          var implicitCss = this.implicit.css;
          var prevStyle = e.__style__;
          if(prevStyle === style) { prevStyle = null; }

          for (var name in style) {
              var value = style[name];
              if(!prevStyle || (value !== prevStyle[name])) {
                  if (value == null || value == implicitCss[name]) {
                    e.removeAttribute(name);
                  } else {
                    e.style.setProperty(name,value);
                  }
              }
          }

          e.__style__ = style;
        };

        // ------------------
        // Text API overrides
        var _fontInfoCache, _fontInfoElem;

        function getFontInfo(font){
            if(!_fontInfoCache){
                _fontInfoCache = {};
            }

            var fontInfo = _fontInfoCache[font];
            if(!fontInfo){
                fontInfo = (_fontInfoCache[font] = getFontInfoCore(font));
            }

            return fontInfo;
        }

        function getFontInfoCore(font){
            var sty = getFontInfoElem().style;
            sty.setProperty('font', font);

            // Below, the uses of:
            //   '' + sty.getProperty(...)
            //  convert the results to real strings
            //  and not String objects (this later caused bugs in Java code)

            var family = '' + sty.getProperty('font-family');
            if(!family){
                family = 'sans-serif';
            } else if(family.length > 2){
                var quote = family.charAt(0);
                if(quote === '"' || quote === "'"){
                    family = family.substr(1, family.length - 2);
                }
            }

            return {
                family: family,
                size:   '' + sty.getProperty('font-size'),
                style:  '' + sty.getProperty('font-style'),
                weight: '' + sty.getProperty('font-weight')
            };
        }

        function getFontInfoElem(){
            /*global document:true*/
            return _fontInfoElem ||
                   (_fontInfoElem = cgg.win.document.createElementNS('http://www.w3.org/2000/svg', 'text'));
        }

        pv.Text.measure = function(text, font) {
            var fontInfo = getFontInfo(font);

            // NOTE: the global functions 'getTextLenCGG' and 'getTextHeightCGG' must be
            // defined by the CGG loading environment

            /*global getTextLenCGG:true */
            /*global getTextHeightCGG:true */
            return {
                width:  getTextLenCGG   (text, fontInfo.family, fontInfo.size, fontInfo.style, fontInfo.weight),
                height: getTextHeightCGG(text, fontInfo.family, fontInfo.size, fontInfo.style, fontInfo.weight)
            };
        };
    }
});
