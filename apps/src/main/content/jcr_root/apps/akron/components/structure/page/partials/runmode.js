'use strict';

use(['akron/resources/runmodes.js'], function(Runmodes) {
    const useWebpack = Runmodes.hasRunmode('webpack');

    return {
        useWebpack: useWebpack
    };
});
