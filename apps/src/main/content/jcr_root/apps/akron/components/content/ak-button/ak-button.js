'use strict';

use(['akron/resources/runmodes.js'], function(Runmodes) {
    const isAuthor = Runmodes.hasRunmode('author');

    function getNode(path) {
        if (!path || !request.resourceResolver.getResource(path)) return null;

        return request.resourceResolver.getResource(path)
            .adaptTo(Packages.javax.jcr.Node);
    }

    function validateLink(linkPath) {
        var node = getNode(linkPath);

        if (node && isAuthor) {
            return linkPath + '.html';
        } else {
            return linkPath;
        }
    }

    const data = granite.resource.properties;
    return {
        ctaLink: validateLink(data.ctaLink)
    }
});
