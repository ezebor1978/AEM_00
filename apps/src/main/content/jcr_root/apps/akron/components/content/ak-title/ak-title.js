'use strict';

use([], function() {
    const data = granite.resource.properties;

    return {
        overrideLevel: data.overrideLevel == 'none' ? data.headingLevel : data.overrideLevel
    };
});
