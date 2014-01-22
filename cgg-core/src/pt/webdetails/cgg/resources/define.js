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
(function(global) {
    var U; // undefined
    var DEBUG = 0;
    var O_hasOwn = Object.prototype.hasOwnProperty;
    var O_getOwn = function(p, dv) { return O_hasOwn.call(this, p) ? this[p] : dv; };

    var _reJsExt   = /\.js$/i;
    // "/", "//", "foo://", "bar:", "C:"
    var _reAbsPath = /(^\/)|(^[a-z]+:)|(\.js$)/i;

    var _rootModule;
    var _defaultDeps = ['require', 'exports', 'module'];

    var require;

    // MAIN Methods

    // dep  (synchronous syntax)
    // deps, fun
    function require() {
        return _rootModule.require.apply(_rootModule, arguments);
    }

    // id?, deps?, factory
    function define() {
        // factory
        // deps, factory
        // id, factory
        // id, deps, factory

        var args = arguments;
        var L = args.length;
        if(!L) throw new Error("Argument 'factory' is required.");
        if(L > 3) L = 3; // Ignore args beyond 3.

        var id, deps;
        var factory = args[L-1]; // any value type will do, although functions are special.
        if(L === 3) {
            deps = args[1];
            id   = args[0];
        } else if(L === 2) {
            // L === 1
            id = args[0];
            if(id instanceof Array) {
                deps = id;
                id   = U;
            }
        }

        if(deps != null && !(deps instanceof Array))
            throw new Error("Argument 'deps' should be an array.");

        if(id != null && typeof id !== 'string')
            throw new Error("Argument 'id' should be a string.");

        _rootModule.dispatchDefine(id, deps, factory);
    }

    function reset() {
        DEBUG && print("BEG RESET");
        _rootModule = new Module();

        // Read global configuration.
        var config = global.__defineCfg__;
        if(config) {
            DEBUG && print("BEG Loading global configuration");
            _rootModule.configure(config);
            DEBUG && print("END Loading global configuration");
        }
        DEBUG && print("END RESET");
    }

    function configure(config) {
        _rootModule.configure(config);
    }

    // Inform that this is an AMD define.
    define.amd = {};

    // Should be changed/mocked
    // Can be configure through "loadSync" config option as well.
    require.get = function(path) {
        DEBUG && print("Should be loading '" + path + "'");
    };
    require.reset  = reset;
    require.config = configure;

    // Public module object

    function AmdModule(module) {
        this.id = module.id;

        this.config = function() {
            return module.config.config;
        };
    }

    // ------------

    function Module(name, parent) {
        // NOTE: The following code strives to make every instance
        // have the same number and type of properties, as well as their layout.
        this.parent = parent || null;
        this.children = [];
        this._childrenByName = {};
        this.config = {};
        this._amdModule = null;

        if(parent) {
            if(!name) throw new Error("Non-root module must have a local name.");

            this.name   = name || '';
            this.id     = parent.id ? (parent.id + '/' + name) : name;
            this.shared = parent.shared;
        } else {
            this.name   = '';
            this.id     = '';
            this.shared = {
                root: this,

                // All Modules by id.
                all: {},

                // Stack of modules that are being loaded.
                // For `define(...)` to know to which module
                // the dependencies and definition function belong to.
                // TODO: once this is a synchronous implementation,
                // is a stack really needed?
                loadingModules: []
            };
        }

        DEBUG && print("Creating module named '" + this.name + "' and with id '" + this.id + "'");

        this.shared.all[this.id] = this;

        this._path = null; // Cached path value.

        this.isResolved = false; // Quick state check
        this.isLoaded   = false;
        this.isLoading  = false;
        this.exports = U;

        // -----------

        if(parent) parent._addChild(this);
    }

    Module.prototype.dispose = function() {
        var id = this.id;
        if(id) {
            this.id = null;
            var parent = this.parent;
            if(parent) {
                this.parent = null;
                var i = parent.children.indexOf(this);
                if(i >= 0) parent.children.splice(i, 1);
                delete parent._childrenByName[this.name];
            }
            delete this.shared.all[id];
        }
    };

    Module.prototype.isRoot     = function() { return !this.parent; };
    Module.prototype.isTopLevel = function() { var p = this.parent; return !!p && !p.parent; };

    Module.prototype._addChild = function(child) {
        var name = child.name;
        // assertion 1
        if(DEBUG && O_hasOwn.call(this._childrenByName, name))
            throw new Error("Already have a child module named '" + name + "'.");

        this.children.push(child);
        this._childrenByName[name] = child;
    };

    // SPECIAL modules' support.

    Module.prototype.getAmdModule = function() {
        return this._amdModule || (this._amdModule = new AmdModule(this));
    };

    Module.prototype.getExports = function() {
        return this.exports !== U ? this.exports : (this.exports = {});
    };

    // The root module returns the global require function.
    // Other modules return a contextual require.
    // Only the global require function defines `config` and `reset`.
    Module.prototype.getRequire = function() {
        if(this.isRoot()) return require;

        var ctxModule = this;

        return function contextualRequire() {
            return ctxModule.require.apply(ctxModule, arguments);
        };
    };

    // -----------

    Module.prototype.path = function() {
        var path = this._path;
        return path != null ? path : this._evalPath();
    };

    Module.prototype.basePath = function(_) {
        if(arguments.length) {
            this.shared.root.config.basePath = _ || '';
            return this;
        }
        return this.shared.root.config.basePath;
    };

    Module.prototype.isPackage = function() {
        return !!this.config.packageMain;
    };

    // The path for loading purposes (and not for parent child path concatenation, etc.)
    Module.prototype.pathLoad = function() {
        var path = this.path();

        var main = this.config.packageMain;
        if(main) path += '/' + main; // Am a package

        var basePath = this.basePath();
        if(basePath && !_reAbsPath.test(path)) {
            path = basePath + '/' + path;
        }

        return path.replace(_reJsExt, '') + '.js';
    };

    Module.prototype._evalPath = function() {
        var path = this.config.path;
        if(path == null) {
            var parent = this.parent;
            path = parent ? parent.path() : '';
            // Fixating path to '', makes child paths become 'foo', 'bar', ...
            // This has the added benefit of supporting the root node.
            if(path) path += '/' + this.name;
            else path = this.name;
        }
        return (this._path = path);
    };

    // To be called when this.config.path is changed.
    Module.prototype._invalidatePath = function() {
        this._path = null;
        this.children.forEach(function(child) {
            if(child.config.path == null) child._invalidatePath();
        });
    };

    Module.prototype.child = function(name) {
        return O_getOwn.call(this._childrenByName, name);
    };

    // -----

    // Should only be called on a root module.
    Module.prototype.configure = function(config) {
        DEBUG && print("BEG configure");
        if(DEBUG && !this.isRoot()) throw new Error("Invalid operation.");

        var baseUrl = config.baseUrl;
        if(baseUrl !== U) this.basePath(baseUrl);

        if(typeof config.loadSync === 'function') require.get = config.loadSync;

        // Packages - Array of PackageConfig
        var packageList = config.packages;
        if(packageList) {
            packageList.forEach(function(pkg) {
                if(!pkg) throw new Error("Invalid `packages` configuration.");

                if(typeof pkg === 'string') pkg = {name: pkg};

                var name = pkg.name;
                if(!name) throw new Error("Package must have a name.");
                if(name.indexOf('/') >= 0)
                    throw new Error("Invalid package name '" + name +"'.");

                this.get(name, true).configPackage(pkg);
            }, this);
        }

        var amid;

        // Shim - Map of "absolute module-id" to "shim-config".
        var shims = config.shim;
        for(amid in shims) {
            var shim = shims[amid];
            if(!shim) throw new Error("Invalid `shim` configuration.");

            this.get(amid, true).configShim(shim);
        }

        // Paths - Map of "absolute module id prefixes" to "path".
        var paths = config.paths;
        for(amid in paths) {
            var path = paths[amid];
            // TODO: should allow an '' path?
            if(!path) throw new Error("Invalid `paths` configuration.");

            this.get(amid, true).configPath(path);
        }

        // Also need map
        // Map - Map of "absolute module id prefixes" to a MapConfig.
        var maps = config.map;
        for(var contextAmid in maps) {
            var map;
            if(!contextAmid || !(map = maps[contextAmid]))
                throw new Error("Invalid `map` configuration.");

            var contextModule = contextAmid === '*'
                    ? this //.shared.root
                    : this.get(contextAmid, true);

            for(var fromAmid in map) {
                var toAmid;
                if(!fromAmid || !(toAmid = map[fromAmid]))
                    throw new Error("Invalid `map` configuration.");

                // NOTE: fromAmid may be equal to toAmid (to undo an * config.)
                contextModule
                    .configMap(this.get(fromAmid, true), this.get(toAmid, true));
            }
        }

        // Configs - Map of "absolute module id" to configuration value.
        var configs = config.config;
        for(amid in configs) {
            var cfg = configs[amid];
            if(cfg != null) {
                this.get(amid, true).configConfig(cfg);
            }
        }

        DEBUG && print("END configure");
    };

    Module.prototype.configPackage = function(pkg) {
        var main = pkg.main;
        if(main) { if(_reJsExt.test(main)) main = main.substr(0, main.length - 3); }
        else { main = 'main'; }

        this.config.packageMain = main;
        this.configPath(pkg.location || this.name);
    };

    Module.prototype.configShim = function(shim) {
        if(shim instanceof Array) shim = {deps: shim};

        var deps = shim.deps;
        this.config.depIds = deps && deps.length ? deps : null;

        this.config.shim = {
            exportsVar: shim.exports,
            init: shim.init
        };
    };

    Module.prototype.configPath = function(path) {
        this.config.path = path;
        this._invalidatePath();
    };

    Module.prototype.configMap = function(fromModule, toModule) {
        // this = contextModule
        var maps = this.config.maps || (this.config.maps = []);

        // Need more specific from-modules first.
        // TODO: binary search...
        maps.push({ctx: this, from: fromModule, to: toModule});
        maps.sort(function(a, b) { return a.from.compare(b.from); });
    };

    Module.prototype.configConfig = function(config) {
        var owner = this;
        var parent = this.parent;
        if(parent && parent.isPackage() &&
           (this.name === this.parent.config.packageMain)) {
            owner = parent;
        }
        owner.config.config = config;
    };

    // ----------

    // Order by specificity (more specific first),
    // and then by descending alphabetical order of ids (when unrelated).
    Module.prototype.compare = function(b) {
        var a = this;
        if(a === b) return 0; // equal
        return a.contains(b) === 0 ? +1 : // a is parent of b
               b.contains(a) === 0 ? -1 : // a is child  of b
               // Unrelated.
               // Don't want to return 0 on this case,
               // to reserve it for the equal condition,
               // so compare using descending alphabetical order of ids.
               a.id < b.id ? +1 :
               a.id > b.id ? -1 :
               0; // should not happen...
    };

    Module.prototype.contains = function(other) {
        if(this === other) return true;

        if(DEBUG && this.shared !== other.shared)
            throw new Error("Module is not from the same module hierarchy.");

        if(this.isRoot()) return true;

        do { if(this === other) return true; } while((other = other.parent));

        return false;
    };

    Module.prototype.mapRuleLocal = function(fromModule) {
        // this = contextModule;
        if(fromModule !== this) { // TODO: Should this be ensured?
            var maps = this.config.maps;
            if(maps) {
                // Relies on the "more-specific-from first" order of maps.
                for(var i = 0, L = maps.length ; i < L ; i++) {
                    var map = maps[i];
                    if(map.from.contains(fromModule)) return map;
                }
            }
        }
        return null;
    };

    Module.prototype.mapRule = function(fromModule) {
        // contextModule = this, this.parent, this.parent.parent, ...
        return this.mapRuleLocal(fromModule) ||
               (this.parent ? this.parent.mapRule(fromModule) : null);
    };

    // Excluding the name of self.
    Module.prototype.namesTo = function(descendantOrSelf) {
        var names = [];
        while(descendantOrSelf && this !== descendantOrSelf) {
            names.push(descendantOrSelf.name);
            descendantOrSelf = descendantOrSelf.parent;
        }
        if(!descendantOrSelf) throw new Error("Not a descendant or self.");
        names.reverse();
        return names;
    };

    Module.prototype.get = function(mid, expectsAbsolute) {
        DEBUG && print("get mid '" + mid + "' on '" + this.id + "'.");
        // contextModule =  this        (for mapping)
        // baseModule    =  this.parent (for starting relative mids)
        // fromModule    =~ mid

        var isRelative = mid.charAt(0) === '.';

        if(expectsAbsolute && isRelative) throw new Error("Absolute module id expected, but found '" + mid + "'.");

        // Packages resolve paths as if the main.js file was executing.
        var module = !isRelative      ? this.shared.root :
                     this.isPackage() ? this :
                     (this.parent || this); // root can't go further up

        var midTerms = mid.split('/');
        while(midTerms.length) {
            var term = midTerms.shift();
            //DEBUG && print("  Checking term '" + term + "'");
            // Two consecutive slashes? '//'
            if(!term) throw new Error("Invalid module id '" + mid + "'. Contains empty term.");
            if(term === '.') {
                // Stay in same module.
            } else if(term === '..') {
                // One term up. Go to parent, if any, or it is an error.
                // At most, can go to root.
                module = module.parent;
                if(!module)
                    throw new Error("Invalid module id '" + mid + "'. It is above root module.");
            } else {
                // Get existing child or create one.
                var child = module.child(term);
                var isNew = !child;
                if(isNew || /*isLast*/!midTerms.length) {
                    // If it is a new module:
                    // Check if there is a module mapping rule first.
                    // Unfortunately, for that we need to create the "from" module...
                    // So we dispose it later if we jump.
                    // If so, don't create a new module,
                    // cause its not going to be used.
                    // There's no problem in this case,
                    // cause there's no danger of existing a more specific
                    // mapping rule "below", cause right now...
                    // there are no modules below,
                    // and so no configs as well.
                    if(isNew) {
                        DEBUG && print("  New module '" + term + "' on '" + module.id + "'.");
                        child = new Module(term, module);
                    }

                    var mapRule = this.mapRule(child);
                    if(mapRule) {
                        DEBUG && print("  Map module '" + mapRule.from.id + "' to '" + mapRule.to.id + "'.");

                        // Repeat "delta terms"
                        var deltaTerms = mapRule.from.namesTo(child);
                        if(deltaTerms.length)
                            midTerms = deltaTerms.concat(midTerms);

                        // Revert creation of new module...
                        if(isNew) child.dispose();

                        child = mapRule.to;
                    }
                }
                module = child;
            }
        }

        // Cannot go up to/get the root module?
        if(module.isRoot()) throw new Error("Invalid module id '" + mid + "'. It is the root module.");

        DEBUG && print("get -> '" + module.id + "'");

        return module;
    };

    Module.prototype._resolveDeps = function() {
        // Root cannot have dependencies...
        // Except for what mapping is concerned,
        // dependencies are resolved relative to parent module.
        if(this.parent) {
            // Shim deps are set in config.depIds as well.
            var depIds = this.config.depIds;
            if(depIds) return this.requireMany(depIds, /*inDefine*/true);
        }
    };

    Module.prototype.require = function() {
        DEBUG && print("BEG require on '" + this.id + "'");
        try {
            var depId, depIds, fun;

            var i = -1;
            var args = arguments;
            var L = args.length;
            while(++i < L) {
                var a = args[i];
                switch(typeof a) {
                    case 'function': fun   = a; break;
                    case 'string':   depId = a; break;
                    case 'object':   if(a instanceof Array) depIds = a; break;
                }
            }

            if(fun) {
                this.apply(depIds, fun);
            } else {
                // Synchronous only syntax
                if(depId ) return this.requireOne(depId);
                if(depIds) return this.requireMany(depIds);
            }
        } finally {
            DEBUG && print("END require");
        }
    };

    Module.prototype.requireMany = function(mids, inDefine) {
        return mids.map(function(mid) {
            return this.requireOne(mid, inDefine);
        }, this);
    };

    Module.prototype.requireOne = function(mid, inDefine) {
        // Special modules
        if(mid === 'require') return this.getRequire();
        if(mid === 'exports') return inDefine ? this.getExports()   : U;
        if(mid === 'module' ) return inDefine ? this.getAmdModule() : U;

        // Get or create a module instance
        // that corresponds to the dependency module id.
        // Mid can be relative.
        var data;
        var index = mid.indexOf('!');
        var isPlugin = index >= 0;
        if(isPlugin) {
            data = mid.substr(index + 1)
            mid  = mid.substr(0, index);
        }

        var module = this.get(mid);
        return isPlugin ? module.pluginLoad(data) : module.resolve();
    };

    Module.prototype.apply = function(mids, f, x) {
        var args = mids && this.requireMany(mids);
        if(f) return f.apply(x || global, args || []);
    };

    Module.prototype.loadingModule = function() {
        var lms = this.shared.loadingModules;
        return lms.length ? lms[lms.length - 1] : null;
    };

    // Should only be called on the root module.
    // Called by the global `define(...)` to do the actual defining.
    Module.prototype.dispatchDefine = function(id, deps, factory) {
        // Hook up to loading module, if any.

        DEBUG && print("BEG Defining mid " +
            (id != null ? ("'" + id + "'") : "<anonymous>") +
            " with deps [" + (deps || '') + "]");

        var loadingModule;

        if(!id) {
            loadingModule = this.loadingModule();

            // This was a requested module, or it is an error to have no name.
            if(!loadingModule) throw new Error("There's no module currently being loaded.");
        } else {
            // Even if this was a request, the specified `id` is what counts.
            loadingModule = this.get(id, true);
            if(loadingModule.isLoaded) {
                // Don't overwrite modules that were already loaded.
                DEBUG && print("A module with id '" + loadingModule.id + "' was already loaded. Ignoring this one.");
                return;
            }
        }

        loadingModule.define(deps, factory);

        DEBUG && print("END Defining");
    };

    Module.prototype.define = function(depIds, factory) {
        DEBUG && print("BEG Define '" + this.id + "' with deps: [" + depIds + "]");

        var isFactoryFun = typeof factory === 'function';

        if(depIds) {
            if(!depIds.length) depIds = null;
        } else if(isFactoryFun && factory.length) {
            depIds = _defaultDeps.slice(0, factory.length);
        }

        this.config.depIds = depIds;

        // This is for unrequested defines.
        this.isLoading = false;
        this.isLoaded  = true;

        if(isFactoryFun) {
            this.config.factory = factory;
        } else {
            this.exports = factory;
            if(!depIds) this.isResolved = true;
        }

        DEBUG && print("END Define '" + this.id + "'");
    };

    Module.prototype.load = function() {
        if(!this.isLoaded && !this.isLoading) {
            this.isLoading = true;

            var path = this.pathLoad();

            DEBUG && print("BEG load '" + this.id + "' -> path '" + path + "'");

            this.shared.loadingModules.push(this);
            try {
                this._loadPath(path);
            } finally {
                this.isLoading = false;
                this.isLoaded  = true;
                this.shared.loadingModules.pop();

                DEBUG && print("END load '" + this.id + "'");
            }
        }
    };

    Module.prototype._loadPath = function(path) {
        require.get(path);
    };

    Module.prototype.resolve = function() {
        if(!this.isResolved) {
            // Prevent unbounded recursion when
            // cyclic/mutual dependencies exist between modules.
            // To load a module that has a cyclic dependency
            // and grab a final reference to its exported value,
            // the special `module` dependency must be asked for.
            // `module.exports` is an object that (hopefully)
            // when all/both modules are loaded, will contain all the
            // properties of dependency module.
            this.isResolved = true;

            this._resolveCore();
        }

        return this.exports;
    };

    Module.prototype._resolveCore = function() {
        var deps;

        DEBUG && print("BEG resolve '" + this.id + "'");

        var config = this.config;
        var shim   = config.shim;
        if(shim) deps = this._resolveDeps();

        this.load();

        if(shim) {
            if(shim.exportsVar) this.exports = global[shim.exportsVar];
            if(shim.init) {
                var exports = shim.init.apply(global, deps);
                if(exports !== U) this.exports = exports;
            }
            config.shim = null;
        } else {
            deps = this._resolveDeps();
            if(config.factory) {
                var exports = config.factory.apply(global, deps || []);
                if(exports !== U) this.exports = exports;
                config.factory = null;
            }
        }
        DEBUG && print("END resolve '" + this.id + "'");
    };

    Module.prototype.pluginLoad = function(id) {
        DEBUG && print("BEG pluginLoad '" + this.id + "' of '" + id + "'");
        var exports;
        var plugin = this.resolve();
        if(plugin) {
            var load = plugin.load;
            if(typeof load === 'function') {
                load.call(plugin, id, this.getRequire(), function(value) {
                    exports = value;
                });
                // Hey! This is a synchronous AMD loader :-?
                // So the callback better have been called already...
            }
        }

        DEBUG && print("END pluginLoad '" + this.id + "' of '" + id + "' (exports=" + exports + ")");

        return exports;
    };

    // ----------------

    reset();

    // Export

    global.require = require;
    global.define  = define;

}(this));