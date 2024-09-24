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

import { nodeResolve } from "@rollup/plugin-node-resolve";
import commonjs from "@rollup/plugin-commonjs";
import json from "@rollup/plugin-json";
import copy from "rollup-plugin-copy";
import {terser} from "rollup-plugin-terser";
import license from "rollup-plugin-license";
import { dirname, join } from "path";
import { fileURLToPath, URL } from 'url';
import { createRequire } from "module";

const require = createRequire(import.meta.url);

const pluralRulesPath = dirname(require.resolve("@formatjs/intl-pluralrules/locale-data/en"));
const numberFormatPath = dirname(require.resolve("@formatjs/intl-numberformat/locale-data/en"));
const dateTimeFormatPath = dirname(require.resolve("@formatjs/intl-datetimeformat/locale-data/en"));

const relativeSourcePath = "src/main/javascript";
const relativeTargetPath = "target/dist";

export default {
  input: join(relativeSourcePath, "polyfill.js"),
  output: {
    file: join(relativeTargetPath, "polyfill.js"),
    format: "iife"
  },
  plugins: [
    nodeResolve(),
    commonjs(),
    json(),
    terser(),
    license({
      thirdParty: {
        includePrivate: true,
        output: {
          file: fileURLToPath(new URL(join(relativeTargetPath, "LICENSES"), import.meta.url))
        }
      }
    }),
    copy({
      targets: [
        { src: join(pluralRulesPath, "*.js"), dest: join(relativeTargetPath, "locale-data/pluralrules") },
        { src: join(numberFormatPath, "*.js"), dest: join(relativeTargetPath, "locale-data/numberformat") },
        { src: join(dateTimeFormatPath, "*.js"), dest: join(relativeTargetPath, "locale-data/datetimeformat") }
      ]
    })
  ]
};
