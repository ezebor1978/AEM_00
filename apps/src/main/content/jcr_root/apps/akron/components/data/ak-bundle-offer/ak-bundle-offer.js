'use strict';

const OfferEDCService = Packages.com.energyharbor.akron.core.services.OfferEDCService;

use([], function () {
    const gson = new com.google.gson.Gson();
    let edcList = [];
    let parsedGasOffer = '{}';
    let parsedElectricityOffer = '{}';
    const offerEDCService = sling.getService(OfferEDCService);

    try {
        let gasOffer = offerEDCService.getOfferById(resolver, properties.gasProductId, false);
        if (gasOffer) {
            parsedGasOffer = gasOffer.toJSON();
            const gasOfferEdcDetails = parsedGasOffer.edcDetails;
            if (gasOfferEdcDetails != null) {
                edcList.push({
                    type: 'GAS',
                    edcName: gasOfferEdcDetails.name,
                    edcLogo: gasOfferEdcDetails.logo
                })
            }
        }

        let electricityOffer = offerEDCService.getOfferById(resolver, properties.electricityProductId, false);
        if (electricityOffer) {
            parsedElectricityOffer = electricityOffer.toJSON();
            const electricityEdcDetails = parsedElectricityOffer.edcDetails;
            if (electricityEdcDetails != null) {
                edcList.push({
                    type: 'ELECTRICITY',
                    edcName: electricityEdcDetails.name,
                    edcLogo: electricityEdcDetails.logo
                })
            }
        }

    } catch (e) {
        log.error(e);
    }

    return {
        isBundle: 'true',
        offerData: JSON.stringify({
            gasOffer: JSON.parse(parsedGasOffer),
            electricityOffer: JSON.parse(parsedElectricityOffer),
            edcList: JSON.parse(gson.toJson(edcList))
        })

    };
});
