'use strict';

const ReCAPTCHAService = Packages.com.energyharbor.akron.core.services.reCAPTCHAService;

use([], function() {
    const reCAPTCHAService = sling.getService(ReCAPTCHAService);
    return {
        token: reCAPTCHAService.getSiteKey()
    }
});
