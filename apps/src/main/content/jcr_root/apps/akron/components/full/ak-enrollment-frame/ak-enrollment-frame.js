'use strict';

const OfferEDCService = Packages.com.energyharbor.akron.core.services.OfferEDCService;
const ReCAPTCHAService = Packages.com.energyharbor.akron.core.services.reCAPTCHAService;
const OfferType = Packages.com.energyharbor.akron.core.enums.OfferType;

use([], function () {
    let parsedOffer = '{}';
    let token = '';
    let validOffer = false;
    let productId = request.getParameter("offer");
    let isEdit = wcmmode.disabled ? 'false' : 'true';

    const offerEDCService = sling.getService(OfferEDCService);
    const reCAPTCHAService = sling.getService(ReCAPTCHAService);

    try {
        const isBundleEnrollment = properties.formType != null && properties.formType.equals("bundle");

        let offer = offerEDCService.getOfferById(resolver, productId, isBundleEnrollment);

        token = reCAPTCHAService.getSiteKey();

        parsedOffer = offer.toJSON();
        productId = offer.productId;

        if (offer.offerType == OfferType.BUNDLE) {
            validOffer = (offer.bundleGasOffer && offer.bundleGasOffer.isValidOffer)
                && (offer.bundleElectricityOffer && offer.bundleElectricityOffer.isValidOffer);
        } else {
            validOffer = offer.isValidOffer;
        }

    } catch (e) {
        log.error(e);
    }

    return {
        plan: JSON.stringify({
            captchaToken: String(token),
            validOffer: String(validOffer),
            productId: String(productId),
            isEdit: String(isEdit),
            offer: JSON.parse(parsedOffer),
        })
    }
});
