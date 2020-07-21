'use strict';
const OfferEDCService = Packages.com.energyharbor.akron.core.services.OfferEDCService;

use([], function() {

    let phone = currentPage.getProperties().get("phoneNumber");
    const offerEDCService = sling.getService(OfferEDCService);

    try {
        phone = phone ? String(phone) : String(offerEDCService.getCommunityProgramsPhone(resolver));
    } catch (e) {
        log.error(e);
    }

    let data = JSON.stringify({
        phoneNumber: String(phone),
        name: String(currentPage.getTitle()),
        subtitle: getSafeProperty('communitySubtitle'),
        county: getSafeProperty('county'),
        edcCode: getSafeProperty('edcCode'),
        state: getSafeProperty('state'),
        phoneNumberLabel: getSafeProperty('phoneNumberLabel'),
        phoneIcon: getSafeProperty('phoneIcon'),
    });

    function getSafeProperty(prop) {
        return currentPage.getProperties().get(prop) ? String(currentPage.getProperties().get(prop)) : ""
    }

    return {
        data: data
    };


});
