'use strict';

/**
 * Type conversion helpers for Rhino.
 * Converts Java types into Rhino types.
 */
use([], function() {
    return {
        str: function(s) {
            return s ? '' + s : null;
        },
        num: function(n) {
            return n ? Number(n) : null;
        },
        bool: function(b) {
            return b == true;
        }
    };
});
