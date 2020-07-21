'use strict';

use([], function() {
    return {
        hideSearchBar: currentPage.getProperties().get('hideSearchBar') != null ? 'true' : 'false',
        currentPage: resolver.map(currentPage.getPath()),
        isEdit: wcmmode.disabled ? 'false' : 'true'
    };
});
