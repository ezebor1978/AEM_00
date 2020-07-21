'use strict';

const OfferEDCService = Packages.com.energyharbor.akron.core.services.OfferEDCService;

use([], function () {
    let edcName = "";
    let edcLogo = "";
    const offerEDCService = sling.getService(OfferEDCService);

    try {
        const code = properties.edcCode;
        const edcResult = offerEDCService.getEDCDetails(resolver, code);
        if(!edcResult.isEmpty()) {
            edcName = edcResult.get(0).name;
            edcLogo = edcResult.get(0).logo;
        }
    } catch (e) {
        log.error(e);
    }

    return {
        edcName: edcName,
        edcLogo: edcLogo,
        isBundle: "false",
    };
});
