
import { nodeResolve } from "@rollup/plugin-node-resolve";
import commonjs from "@rollup/plugin-commonjs";
import json from "@rollup/plugin-json";
import terser from "@rollup/plugin-terser";
import copy from "rollup-plugin-copy";
import license from "rollup-plugin-license";
import { dirname, join } from "path";
import { fileURLToPath, URL } from 'url';
import { createRequire } from "module";

const require = createRequire(import.meta.url);

// Only datetimeformat v7 export map requires explicit '.js' locale-data paths, but others were changed for consistency
const pluralRulesPath = dirname(require.resolve("@formatjs/intl-pluralrules/locale-data/en.js"));
const numberFormatPath = dirname(require.resolve("@formatjs/intl-numberformat/locale-data/en.js"));
const dateTimeFormatPath = dirname(require.resolve("@formatjs/intl-datetimeformat/locale-data/en.js"));

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
