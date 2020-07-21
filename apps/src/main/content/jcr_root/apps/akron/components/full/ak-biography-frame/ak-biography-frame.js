'use strict';

use([], function() {
    let biography;
    let parsedBiography;

    try {
        const bioResource = currentPage.getContentResource();
        biography = bioResource.adaptTo(Packages.com.energyharbor.akron.core.models.Biography);
        biography.init(resolver);
    } catch (e) {
        log.error(e);
    }

    if (biography) {
        parsedBiography = biography.toJSON();
    }

    return {
        biography: parsedBiography,
    };
});
