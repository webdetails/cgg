/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
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

  Intl.DateTimeFormat.__setDefaultTimeZone(getOffsetString());

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

  function getOffsetString() {
    // getTimezoneOffset() returns the offset from local time to UTC
    // with the opposite sign of the conventional UTC offset
    const offsetMinutes = -new Date().getTimezoneOffset();

    // Whether the timezone is ahead (+) or behind (-) UTC
    const sign = offsetMinutes >= 0 ? "+" : "-";
    const absoluteMinutes = Math.abs(offsetMinutes);

    const hours = String(Math.floor(absoluteMinutes / 60)).padStart(2, '0');
    const minutes = String(absoluteMinutes % 60).padStart(2, '0');

    return `${sign}${hours}:${minutes}`;
  }
}());

