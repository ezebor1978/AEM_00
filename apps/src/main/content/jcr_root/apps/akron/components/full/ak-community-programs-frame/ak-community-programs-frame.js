'use strict';

const OfferEDCService = Packages.com.energyharbor.akron.core.services.OfferEDCService;

use([], function() {
    let communities = [];
    const gson = new com.google.gson.Gson();
    const offerEDCService = sling.getService(OfferEDCService);

    try {
        communities = offerEDCService.getCommunities(resolver);
    } catch (e) {
        log.error(e);
    }

    function getSafeProperty(prop) {
        return currentPage.getProperties().get(prop) ? String(currentPage.getProperties().get(prop)) : ""
    }

    let data = JSON.stringify({
        title: String(currentPage.getTitle()),
        subtitle: getSafeProperty('communitySubtitle'),
        phoneNumber: getSafeProperty('phoneNumber'),
        phoneTitle: getSafeProperty('phoneTitle'),
        phoneIcon: getSafeProperty('phoneIcon'),
        filterPlaceholder: getSafeProperty('filterPlaceholder'),
        column1Title: getSafeProperty('column1Title'),
        column2Title: getSafeProperty('column2Title'),
        column3Title: getSafeProperty('column3Title'),
        column4Title: getSafeProperty('column4Title'),
        communities: JSON.parse(gson.toJson(communities))
    });

    return {
        data: data
    };
});
