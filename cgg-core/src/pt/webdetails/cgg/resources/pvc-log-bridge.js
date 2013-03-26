// Bridge the CCC log to CGG's global print function
(function() {
    var level = 1;

    // Must be == to work
    if(params.get('debug') == 'true') {
        var debugLevel = parseFloat(params.get('debugLevel'));
        if(!isNaN(debugLevel) && isFinite(debugLevel)) { level = debugLevel; }
    }

    function _callLog(mask) {
        try {
            var args = Array.prototype.slice.call(arguments);

            if(mask) { args[0] = mask.replace('%s', ''); }

            var text = args.map(function(s){ 
                return !s || typeof s !== 'object' ?  (''+s) : pvc.stringify(s, {ownOnly: false});
            }).join(' ');

            print(text);
        } catch(ex) {
            print('Error writting to log: ' + ex);
        }
    }

    // Replace console object's methods
    ['log', 'debug', 'info', 'warn', 'group', 'groupCollapsed', 'groupEnd', 'error']
    .forEach(function(p) { console[p] = _callLog; });

    // Must be called _after_ console methods replacement
    pvc.setDebug(level);

    // Execute immediately
}());;