if(!Object.keys) {
	Object.keys = function(o) {
	 	if(o !== Object(o)) { throw new TypeError('Object.keys called on non-object'); }

	 	var ret = [];
	 	for(var p in o) {
	 		if(Object.prototype.hasOwnProperty.call(o,p)) { ret.push(p); }
	 	}
	 	return ret;
	}
}

if(!Object.create){
    /** @ignore */
    Object.create = (function(){

        var Klass = function(){},
            proto = Klass.prototype;

        /** @private */
        function create(baseProto){
            Klass.prototype = baseProto || {};
            var instance = new Klass();
            Klass.prototype = proto;

            return instance;
        }

        return create;
    }());
}
