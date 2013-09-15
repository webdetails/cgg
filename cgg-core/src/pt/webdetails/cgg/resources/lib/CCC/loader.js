(function() {
    // Loads the CCC library that best suits the desired version.
    // * params.get('cccVersion') - the desired major[.minor[-variant]] version string/number:
    //    '2.0', '1.9', '1.0-beta', ...
    //   The default value is the newest available.

    // Obtains lib infos, indexed by versino component, 
    //  and sorted from Newest to Oldest version.
    // Versions with variant are ordered before 
    //  corresponding major.minor version without variant.
    // The order between versions of same major.minor and 
    //  of different non-null variants is not significant.
    var getRootLibInfo = function() {
        return {
            component: null,
            children: [
                {
                    component: 2, 
                    children: [
                        {component: 0, version: new Version(2, 0, 'analyzer'), paths: paths_2_0_analyzer},
                        {component: 0, version: new Version(2, 0),             paths: paths_2_0}
                    ]
                }
            ]
        };
    }

    var libBasePath  = 'lib/CCC/';

    function paths_2_0() {
        var versionBasePath = libBasePath + '2.0/';
        return [
            libBasePath     + 'jquery-shim.js',
            versionBasePath + 'protovis.js',
            versionBasePath + 'protovis-cgg.js',
            libBasePath     + 'tipsy-shim.js',
            versionBasePath + 'def.js',
            versionBasePath + 'pvc-d2.0.js',
            versionBasePath + 'compatVersion.js'
        ];
    }

    function paths_2_0_analyzer() {
        // Let cgg init, as well.
        cgg.debug = 4;
        cgg.init();
        // Needed otherwise debugging mode throws...
        cgg.debug = 0;
        JSON.stringify = String;

        var versionBasePath = libBasePath + '2.0-analyzer/';
        return [
            libBasePath     + 'jquery-shim.js',
            versionBasePath + 'protovis.js',
            libBasePath     + 'tipsy-shim.js',
            versionBasePath + 'pvc-d2.0-analyzer.js'
        ];
    }

    // ----------------------

    /** 
     * Major version changes are breaking.
     * Minor version changes are non-breaking, with new or changed functionality.
     * A variant is a, possibly incompatible, variation of 
     *  the corresponding major.minor version.
     * It's not a pre-release term, like in: "2.0.beta",
     *  but, instead, a branch term, like in: "2.0-analyzer".
     * For comparison purposes a version with no variation
     *  is considered newer than a version with a variation.
     * Two versions with different non-null variations are not comparable...
     */
    function Version(major, minor, variant) {
        this.major = this[0] = Version.parseNumber(major);
        this.minor = this[1] = isFinite(this.major) ? 
            Version.parseNumber(minor, /*asMinor*/true) : 
            Infinity;

        this.length = 2;

        this.variant = variant ? String(variant) : null;
    }

    Version.prototype.toString = function() {
        return this.major + '.' + this.minor + (this.variant ? ('-' + this.variant) : '');
    };

    Version.parse = function(value) {
        // Newest.
        if(value != null && value !== '') {
            try {
                switch(typeof value) {
                    case 'string':
                        var m = /^(?:(Infinity|\d+)(?:\.(Infinity|\d+))?)?(?:-(.+))?$/
                                .exec(value);

                        if(m) { return new Version(m[1], m[2], m[3]); }

                        break;

                    case 'number':
                        if(!isNaN(value)) {
                            if(num <= 0) { return Version.OLDEST; }

                            // Split number into its major, minor components
                            // Ex: num = 20.723
                            return new Version(
                                    // major === 20 (floor is done inside constructor)
                                    num,
                                    
                                    // minor === 723 (20.723 -> ["20", 723"] -> "723" (or undefined))
                                    String(num).split('.')[1]);
                        }
                        break;
                }
            } catch(ex) {
                // no point in logging twice more or less the same thing
            }

            print("WARNING! Ignoring invalid version value: '" + value + "'.");
        }

        return Version.NEWEST;
    };

    Version.parseNumber = function(value, asMinor) {
        if(value == null || value === '') { return asMinor ? 0 : Infinity; }

        var n = +value;
        if(isNaN(n)) { throw new Error("Invalid version component: " + value); }

        return n <= 0 ? 0 : Math.floor(n); // Infinity passes through
    };

    Version.NEWEST = new Version(Infinity, Infinity);
    Version.OLDEST = new Version(       0,        0);

    // ----------

    /**
     * Gets the available lib info that "best" satisfies the requested version.
     * See associated test file for a spec by example.
     */
    function chooseLib(version, rootInfo) {
        return chooseLibRecursive(version, 0, version.length, rootInfo);
    }

    function chooseLibRecursive(version, level, levelCount, parentInfo) {
        if(level >= levelCount) {
            return parentInfo;
        }

        var isLastLevel = level === levelCount - 1;
        var infoNewer;
        var component = version[level];
        var children = parentInfo.children;
        for(var i = 0, I = children.length ; i < I ; i++) {
            var info = children[i];
            var icomponent = info.component;
            var isLastChild = (i === (I - 1));
            var leafInfo;
            
            if(component > icomponent) {
                if(infoNewer) {
                    return chooseLibChildRecursive(version, level, levelCount, infoNewer);
                }

                // Want Newer than available.
                leafInfo = chooseLibChildRecursive(Version.NEWEST, level, levelCount, info);
                if(leafInfo) { return leafInfo; }
                
            } else if(component === icomponent) {
                // Same component.
                leafInfo = chooseLibChildRecursive(version, level, levelCount, info, infoNewer, isLastChild);
                if(leafInfo) { return leafInfo; }

            } else if(component < icomponent) {
                if(isLastChild) {
                    return chooseLibChildRecursive(Version.OLDEST, level, levelCount, info);
                }
            }

            if(!isLastLevel || !info.version.variant) {
                infoNewer = info;
            }
        }

        return null;
    }

    function chooseLibChildRecursive(version, level, levelCount, info, infoNewer, isLastChild) {
        var levelNext  = level + 1;
        var leafInfo   = chooseLibRecursive(version, levelNext, levelCount, info);
        var checkNewer = !!infoNewer;
        if(leafInfo) {
            // Check if we can really use leafInfo.
            if(levelNext >= levelCount && !canUseVariant(version, leafInfo.version)) {
                checkNewer &= isLastChild;
                // If !isLastChild, don't backtrack (yet).
                // Continue looking for appropriate variant.
                leafInfo = null;
            }
        }

        // No match because of different variants.
        // Older children can't match as well.
        // If any, back-track to infoNewer and retry.
        if(!leafInfo && checkNewer) {
            leafInfo = chooseLibRecursive(Version.OLDEST, levelNext, levelCount, infoNewer);
        }
        return leafInfo;
    }

    function canUseVariant(version, iversion) {
        var variant  =  version.variant;
        var ivariant = iversion.variant;
        return !!(variant === ivariant || (variant && !ivariant));
        // NOTE: not-null ivariant are analyzed before the null variant.
        // TRUE
        //  Ex: variant: null, ivariant: null
        //  Ex: variant: a,    ivariant: a
        //  Ex: variant: a,    ivariant: null
        // FALSE
        //  Ex: variant: null, ivariant: b
        //  Ex: variant:    a, ivariant: b (a != b)
    }
    
    // ----------
    
    function load(versionValue) {
        var version = Version.parse(versionValue);

        if(isFinite(version.major)) { print("Request CCC v" + version); }

        var rootInfo = getRootLibInfo();

        var info = chooseLib(version, rootInfo);

        // Chose the latest, anyway.
        //if(!info && infos.length) { info = infos[0]; }
        if(info) {
            var iversion = info.version;
            if(iversion.major > version.major) {
                print("[WARNING] Choosing version with newer major!");
            } else if(iversion.major < version.major) {
                if(isFinite(version.major)) {
                    print("[WARNING] Choosing version with older major!");
                }
            } else if(iversion.minor > version.minor) {
                // :-)
            } else if(iversion.minor < version.minor) {
                if(isFinite(version.minor)) {
                    print("[WARNING] Choosing version with older minor!");
                }
            } else if(iversion.variant !== iversion.variant) {
                print("[WARNING] Choosing version with no variant!");
            }
            
            print("Using CCC v" + iversion);

            // Libraries are loaded in global scope (not in the current scope).
            info.paths().forEach(function(path) { lib(path); });
        }

        return info;
    }

    // ----------

    if(typeof _TestHook_ === 'undefined') {

        load(cgg.javaToJS(params.get('cccVersion')));

    } else {
        // Export stuff required for testing
        _TestHook_.cccLibLoader = {
            load:    load,
            Version: Version,
            rootLib: function(getRootLib) {
                if(getRootLib) {
                    getRootLibInfo = getRootLib;
                    return this;
                }
                return getRootLibInfo;
            }
        };
    }
}());
