package com.energyharbor.akron.core.services;

public interface reCAPTCHAService {

    /**
     * Returns the site key value
     * @return the site key
     */
    String getSiteKey();

    /**
     * Validates if the captcha token is valid
     * @return a boolean indicating of the captcha token is valid or not
     */
    boolean validateCaptchaResponse(String captchaResponse);

}
