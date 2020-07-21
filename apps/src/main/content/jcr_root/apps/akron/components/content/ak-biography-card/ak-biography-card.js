'use strict';

use([], function() {
    let biography;
    let valid = false;
    let parsedBiography = '{}';
    const path = properties.bioUrl;

    if (path != null) {
        try {
            const bioResource =  resolver.resolve(path)
                .adaptTo(Packages.com.day.cq.wcm.api.Page)
                .getContentResource();

            biography = bioResource.adaptTo(Packages.com.energyharbor.akron.core.models.Biography);
            biography.init(resolver);
        } catch (e) {
            log.error(e);
        }
    }

    if (biography) {
        valid = true;
        parsedBiography = biography.toJSON();
    }

    return {
        valid: valid,
        biography: JSON.stringify({
            ctaText: String(properties.ctaText),
            content: JSON.parse(parsedBiography)
        }),
    };
});
