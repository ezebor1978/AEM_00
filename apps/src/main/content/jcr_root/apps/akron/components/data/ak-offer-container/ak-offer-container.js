'use strict';

const OfferEDCService = Packages.com.energyharbor.akron.core.services.OfferEDCService;

use([], function () {
    let massMarketUrl = "";
    const path = currentPage.getPath();
    const offerEDCService = sling.getService(OfferEDCService);

    try {
        massMarketUrl = offerEDCService.getOffersUrl();
    } catch (e) {
        log.error(e);
    }

    return {
        offersContainer: path.equals(massMarketUrl) ? "ak-offer-container--display-col" : ""
    };
});
