package com.energyharbor.akron.core.services;

import com.google.gson.JsonObject;

public interface EnrollmentService {

    /**
     * Query the DB to retrieve the Product values needed for the Enrollment Form Submission
     * Creates the Offer Application and Offer Application Account
     * Calls to insert the row in the DB
     *
     * @param data JSONObject with the request parameters from the enroll form containing the form fields like
     *             the personal information, mailing address, electric service address, customer number among others,
     *             the product id is used to retrieved the row with the information of the product to add to the offer tables
     *             and other variables like edc code and form type.
     *
     * @return boolean indicating if the enrollment was success or not
     */
    boolean enroll(JsonObject data);
}

