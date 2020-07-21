'use strict';

use([], function() {
    const props = {
        NUM_COLS: 'numCols',
    };

    const data = granite.resource.properties;
    const cols = parseInt(data[props.NUM_COLS] || 2);
    const block = 'ak-grid-row';
    var cssClass = [block];

    var colsArr = [];
    for (var i = 0; i < cols; i++) {
        colsArr.push({
            parsys: block + '-par-' + (i + 1)
        });
    }

    var component = {
        cols: colsArr,
        cssClass: cssClass
    };

    return component;
});
