package com.energyharbor.akron.core.servlets;

import com.energyharbor.akron.core.services.EnrollmentService;
import com.energyharbor.akron.core.services.reCAPTCHAService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Servlet that receives the request from the enrollment form, and calls Enrollment service to add the data to the DB
 */
@Component(service=Servlet.class,
    property={
           Constants.SERVICE_DESCRIPTION + "=Enrollment",
           "sling.servlet.methods=" + HttpConstants.METHOD_POST,
           "sling.servlet.paths="+ "/bin/akron/enrollment"
})
public class EnrollmentServlet extends SlingAllMethodsServlet {

    @Reference
    private EnrollmentService enrollmentService;

    @Reference
    private reCAPTCHAService reCAPTCHAService;

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doPost(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) throws IOException {
        Gson gson = new Gson();
        resp.setContentType("application/json");

        boolean success = true;

        try {

            JsonObject data = UtilsServlet.getBody(req);
            log.debug("Submission received.");
            log.debug(data.toString());

            JsonArray enrollmentData = data.getAsJsonArray("enrollmentData");

            ArrayList<Boolean> isDataValid = new ArrayList<Boolean>();

            String captchaResponse = data.get(UtilsServlet.CAPTCHA_RESPONSE).getAsString();

            boolean validCaptcha = reCAPTCHAService.validateCaptchaResponse(captchaResponse);

            if (!validCaptcha) {
                isDataValid.add(false);
                log.error(String.format("The received captcha is either invalid or empty, Captcha: %s", captchaResponse));
            }

            enrollmentData.forEach(dataItem -> {
                JsonObject enrollmentDataItem = dataItem.getAsJsonObject();
                String phoneNumber = enrollmentDataItem.get("phone_number").getAsString();
                String emailAddress = enrollmentDataItem.get("email_address").getAsString();

                try {
                    if(isEmptyAnyParameter(enrollmentDataItem)) {
                        log.error("One of the required fields is empty.");
                    } else if(!UtilsServlet.isValidEmailAddress(emailAddress)) {
                        log.error("Invalid email address");
                    } else {
                        String phoneArea = phoneNumber.substring(0, 3);
                        String phoneLast = phoneNumber.substring(8);
                        String phoneExchange = phoneNumber.substring(4, 7);
                        enrollmentDataItem.addProperty("phone_area", phoneArea);
                        enrollmentDataItem.addProperty("phone_last", phoneLast);
                        enrollmentDataItem.addProperty("phone_exchange", phoneExchange);
                    }
                } catch (Exception e) {
                    isDataValid.add(false);
                }
            });

            if (isDataValid.contains(false)) {
                success = false;
            } else {
                success = enrollmentService.enroll(data);
            }

        } catch (Exception e) {
            success = false;
            log.error("Error occurred while processing the request", e);
        }

        if(!success) {
            UtilsServlet.handleAPIException(new Exception("An error occurred while processing the request."), gson, resp);
        }
    }

    /**
     * Checks if any of the required parameters is empty
     *
     * @param data JsonObject
     * @return boolean
     */
    private boolean isEmptyAnyParameter(JsonObject data) {
        String formType = data.get("form_type").getAsString();
        String firstName = data.get("first_name").getAsString();
        String lastName = data.get("last_name").getAsString();
        String phoneNumber = data.get("phone_number").getAsString();
        String emailAddress = data.get("email_address").getAsString();
        String mailingStreetAddress = data.get("mailing_street_address").getAsString();
        String mailingCity = data.get("mailing_city").getAsString();
        String mailingState = data.get("mailing_state").getAsString();
        String mailingZipCode = data.get("mailing_zip_code").getAsString();
        String customerNumber = data.get("customer_number").getAsString();
        String hearAboutUs = data.get("hear_about_us").getAsString();
        boolean differentServiceAddress = data.get("different_service_address").getAsBoolean();
        String serviceStreetAddress = data.get("service_address").getAsString();
        String serviceCity = data.get("service_city").getAsString();
        String serviceState = data.get("service_state").getAsString();
        String serviceCode = data.get("service_zip_code").getAsString();
        boolean acceptDisclaimer = data.get("accept_disclaimer").getAsBoolean();
        String productId = data.get("product_id").getAsString();

        boolean isEmptyAnyParameter = false;

        if(formType.equals("residential") && data.get("residential_purpose") == null) {
            isEmptyAnyParameter = true;
        } else if(formType.equals("business") && data.get("company_business") == null) {
            isEmptyAnyParameter = true;
        } else if(firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty() || emailAddress.isEmpty() || mailingStreetAddress.isEmpty() || mailingCity.isEmpty() ||
                    mailingState.isEmpty() || mailingZipCode.isEmpty() || customerNumber.isEmpty() || hearAboutUs.isEmpty() || productId.isEmpty()) {
            isEmptyAnyParameter = true;
        } else if(differentServiceAddress && (serviceStreetAddress.isEmpty() ||serviceCity.isEmpty() || serviceState.isEmpty() || serviceCode.isEmpty())) {
            isEmptyAnyParameter = true;
        } else if(!acceptDisclaimer) {
            isEmptyAnyParameter = true;
        }

        return isEmptyAnyParameter;
    }
}

