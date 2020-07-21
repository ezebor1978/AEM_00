'use strict';

const OfferEDCService = Packages.com.energyharbor.akron.core.services.OfferEDCService;
const DatabaseService = Packages.com.energyharbor.akron.core.database.services.DatabaseService;
const OfferType = Packages.com.energyharbor.akron.core.enums.OfferType;

use([], function () {
    let validEDC = false;
    let parsedEDC = '{}';
    let electricityOffersRelated = [];
    let gasOffersRelated = [];
    let bundleOffersRelated = [];
    let isEdit = wcmmode.disabled ? 'false' : 'true';

    const zip = request.getParameter("zip");
    const edc = request.getParameter("edc");

    const gson = new com.google.gson.Gson();
    const databaseService = sling.getService(DatabaseService);
    const offerEDCService = sling.getService(OfferEDCService);

    try {
        validEDC = databaseService.validateEDC(zip, edc);
        if(validEDC) {
            electricityOffersRelated = offerEDCService.getOffersByEDC(resolver, edc, OfferType.ELECTRICITY);
            gasOffersRelated = offerEDCService.getOffersByEDC(resolver, edc, OfferType.GAS);
            bundleOffersRelated = offerEDCService.getOffersByEDC(resolver, edc, OfferType.BUNDLE);
            const edcList = offerEDCService.getEDCDetails(resolver, edc);
            if(!edcList.isEmpty()) {
                parsedEDC = edcList.get(0).toJSON();
            }
        }
    } catch (e) {
        log.error(e);
    }

    return {
        plan: JSON.stringify({
            validEDC: validEDC,
            isEdit: String(isEdit),
            edc: JSON.parse(parsedEDC),
            zip: !validEDC ? "" : String(zip),
            electricityOffers: {
                type: 'ELECTRICITY',
                offersList: JSON.parse(gson.toJson(electricityOffersRelated))
            },
            gasOffers: {
                type: 'GAS',
                offersList: JSON.parse(gson.toJson(gasOffersRelated))
            },
            bundleOffers: {
                type: 'BUNDLE',
                offersList: JSON.parse(gson.toJson(bundleOffersRelated))
            }
        })
    };
});
