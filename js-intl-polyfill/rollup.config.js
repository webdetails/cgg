
import { nodeResolve } from "@rollup/plugin-node-resolve";
import commonjs from "@rollup/plugin-commonjs";
import json from "@rollup/plugin-json";
import { babel } from "@rollup/plugin-babel";
import terser from "@rollup/plugin-terser";
import copy from "rollup-plugin-copy";
import license from "rollup-plugin-license";
import { dirname, join } from "path";
import { fileURLToPath, URL } from 'url';
import { createRequire } from "module";
import { transformSync } from "@babel/core";

const require = createRequire(import.meta.url);

// The formatjs export maps require explicit '.js' locale-data paths.
const pluralRulesPath = dirname(require.resolve("@formatjs/intl-pluralrules/locale-data/en.js"));
const numberFormatPath = dirname(require.resolve("@formatjs/intl-numberformat/locale-data/en.js"));
const dateTimeFormatPath = dirname(require.resolve("@formatjs/intl-datetimeformat/locale-data/en.js"));

// The formatjs packages are published as ES2015+, which Rhino cannot parse (e.g. classes, optional catch binding).
// This also applies to the locale data, e.g. plural rules functions with default parameters.
const babelOptions = {
  babelrc: false,
  configFile: false,
  presets: [["@babel/preset-env", { targets: { ie: "11" } }]]
};

const transpileLocaleData = (contents, filename) => transformSync(contents.toString(), { ...babelOptions, filename, sourceType: "script", compact: true }).code;

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
    babel({ ...babelOptions, babelHelpers: "bundled" }),
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
        { src: join(pluralRulesPath, "*.js"), dest: join(relativeTargetPath, "locale-data/pluralrules"), transform: transpileLocaleData },
        { src: join(numberFormatPath, "*.js"), dest: join(relativeTargetPath, "locale-data/numberformat"), transform: transpileLocaleData },
        { src: join(dateTimeFormatPath, "*.js"), dest: join(relativeTargetPath, "locale-data/datetimeformat"), transform: transpileLocaleData }
      ]
    })
  ]
};
