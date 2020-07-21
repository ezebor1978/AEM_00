package com.energyharbor.akron.core.services;

import com.google.gson.JsonObject;

import javax.activation.DataSource;
import java.io.IOException;

public interface EmailNotificationsService {

    /**
     * Sends a Contact Us email to a predefined list of users.
     * @param data JSONObject with the request parameters
     * @return boolean if the email was sent to all the recipients.
     * @throws IOException
     */
    boolean sendContactUsEmail(JsonObject data) throws IOException;

    /**
     * Sends a Request a quote email to a predefined list of users.
     * @param data JSONObject with the request parameters
     * @param fileName String with the file name
     * @param file DataSource file that will be attached to the email
     * @return boolean if the email was sent to all the recipients.
     * @throws IOException
     */
    boolean sendRequestQuoteEmail(JsonObject data, String fileName, DataSource file) throws IOException;
}

