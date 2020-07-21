'use strict';

const OfferEDCService = Packages.com.energyharbor.akron.core.services.OfferEDCService;

use([], function() {

    let phoneNumber = "";
    let edc = null;
    let productId = request.getParameter("offer");
    const offerEDCService = sling.getService(OfferEDCService);

    try {
        const isBundleEnrollment = properties.enrollmentType != null && properties.enrollmentType.equals("bundle");

        let offer = offerEDCService.getOfferById(resolver, productId, isBundleEnrollment);

        if (offer && offer.isBundle == true && offer.bundleElectricityOffer && offer.bundleElectricityOffer.isValidOffer == true){
            edc = offer.bundleElectricityOffer.edcDetails;
            phoneNumber = edc.phoneNumber;
        } else if (offer && offer.isBundle != true && offer.isValidOffer == true) {
            edc = offer.edcDetails;
            phoneNumber = edc.phoneNumber;
        }

    } catch (e) {
        log.error(e);
    }

    let data = JSON.stringify({
        logo: String(currentPage.getProperties().get('logoImage')),
        logoUrl: String(currentPage.getProperties().get('logoUrl')),
        phoneLabel: String(currentPage.getProperties().get('phoneNumberLabel')),
        phoneIcon: String(currentPage.getProperties().get('phoneIcon')),
        phoneNumber: String(phoneNumber)
    });

    return {
        data: data
    };
});
