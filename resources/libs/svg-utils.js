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

var cgg = cgg || {};

cgg.utils = cgg.utils || {};

cgg.utils.loadSvg = function(path) {
  return new cgg.document(_loadSvg(path));
};

cgg.utils.initDocument = function(path) {
  _document = _loadSvg(path);
  document = new cgg.document(_document);
}

cgg.utils.printNode = function(node) {
    print(_xmlToString(node._node ? node._node : node));
}
