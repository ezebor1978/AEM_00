'use strict';

use([], function() {
    const props = {
        MULTIFIELD: 'multifieldItems',
    };

    const block = 'ak-tabs-navigation';
    var cssClass = [block];

    var tabItemsResource = resolver.getResource(currentNode.getPath() + '/' + props.MULTIFIELD);
    var tabsArr = [];


    if(tabItemsResource !== null) {
        var tabItems = resolver.listChildren(tabItemsResource);
        if(tabItems != null) {
            for (var i = 0; tabItems.hasNext(); i++) {
                tabItems.next();
                tabsArr.push({
                    parsys: 'ak-tab-par' + (i + 1)
                });
            }
        }
    }

    return {
        tabsArr: tabsArr,
        cssClass: cssClass,
        isEdit: wcmmode.disabled ? 'false' : 'true'
    };
});
