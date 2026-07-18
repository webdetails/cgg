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



// Base stuff
lib('cgg-env.js');

require.config({
  config: {
    'pentaho/environment': {
      application: 'pentaho/cdf'
    }
  }
});

// CDF stuff
require('cdf');
