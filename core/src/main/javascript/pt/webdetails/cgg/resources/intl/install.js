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

/* globals lib, params */

lib('intl/polyfill.js');

(function () {
  // Load Metadata for available Candidate Locales
  var locale = globalThis.__locale;
  if (locale) {
    locale = '' + locale;
  }

  // Load the first matching candidate locale for each library.
  var candidateLocales = getCandidateLocales();
  if(candidateLocales.length > 0) {
    loadMetadataFacet('datetimeformat');
    loadMetadataFacet('numberformat');
    loadMetadataFacet('pluralrules');
  }

  function loadMetadataFacet(facet) {
    var loaded = candidateLocales.some(function(candidateLocale) {

      // The locale data files use the canonical casing and separators.
      candidateLocale = normalizeLocale(candidateLocale);
      if(!candidateLocale) {
        return false;
      }

      // Do not log failures to the console.
      var isSilent = true;
      return lib('intl/locale-data/' + facet + '/' + candidateLocale + '.js', isSilent);
    });

    if(!loaded) {
      print("Could not load " + facet + " locale metadata for any of " + candidateLocales.join(", "));
    }
  }

  // Java's toLanguageTag outputs everything with lower case.
  // Also, sometimes _ and / separators are used.
  function normalizeLocale(locale) {
    if(locale) {
      locale = locale.replace(/[_/]/, "-");
      // Validate syntax and canonicalize.
      try {
        locale = new Intl.Locale(locale).toString();
      } catch(error) {
        locale = null;
      }
    }

    return locale || null;
  }

  function getCandidateLocales() {
    var a = [];

    var locales = globalThis.__candidate_locales;
    if(!locales) {
      if(locale) {
        a.push(locale);
      }
    } else {
      var L = locales.length;
      for(var i = 0; i < L; i++) {
        // convert to actual JS string
        a.push('' + locales[i]);
      }
    }

    return a;
  }
}());

