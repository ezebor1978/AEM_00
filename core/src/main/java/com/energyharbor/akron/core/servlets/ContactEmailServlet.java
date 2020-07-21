package com.energyharbor.akron.core.servlets;

import com.energyharbor.akron.core.services.EmailNotificationsService;
import com.energyharbor.akron.core.services.reCAPTCHAService;
import com.google.gson.Gson;
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

/**
 * Servlet that receives the request from contact form, and calls Email Service to send the email.
 */
@Component(service=Servlet.class,
    property={
           Constants.SERVICE_DESCRIPTION + "=Contact Form Submit",
           "sling.servlet.methods=" + HttpConstants.METHOD_POST,
           "sling.servlet.paths="+ "/bin/akron/email/contact"
})
public class ContactEmailServlet extends SlingAllMethodsServlet {

    @Reference
    private EmailNotificationsService emailService;

    @Reference
    private reCAPTCHAService reCAPTCHAService;

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doPost(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) throws IOException {
        Gson gson = new Gson();
        resp.setContentType("application/json");

        try {
            JsonObject data = UtilsServlet.getBody(req);
            log.debug("Submission received.");
            log.debug(data.toString());
            String email = data.get("email_address").getAsString();

            if(!UtilsServlet.isValidEmailAddress(email)) {
                log.error("Invalid email address");
                UtilsServlet.handleAPIException(new Exception("Invalid email address."), gson, resp);
            } else if(!isEmptyAnyParameter(data)) {
                log.error("One of the required fields is empty.");
                UtilsServlet.handleAPIException(new Exception("One of the required fields is empty."), gson, resp);
            } else {
                boolean validCaptcha =  reCAPTCHAService.validateCaptchaResponse(data.get(UtilsServlet.CAPTCHA_RESPONSE).getAsString());
                if(validCaptcha) {
                    boolean emailSent = emailService.sendContactUsEmail(data);
                    if (!emailSent) {
                        log.error("Email could not be sent to one or more recipients.");
                        UtilsServlet.handleAPIException(new Exception("An error occurred while processing the request."), gson, resp);
                    }
                } else {
                    log.error("Invalid captcha.");
                    UtilsServlet.handleAPIException(new Exception("An error occurred while processing the request."), gson, resp);
                }
            }

        } catch (Exception e) {
            log.error("An error occurred while trying to send the Contact Us email", e);
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
        String questionConcerning = data.get("question_concerning").getAsString();
        String firstName = data.get("first_name").getAsString();
        String lastName = data.get("last_name").getAsString();
        String phoneNumber = data.get("phone_number").getAsString();
        String customerType = data.get("customerType").getAsString();
        String questionOrComment = data.get("question_or_comment").getAsString();
        String captchaResponse = data.get(UtilsServlet.CAPTCHA_RESPONSE).getAsString();

        return !questionConcerning.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty() && !phoneNumber.isEmpty() && !customerType.isEmpty() && !questionOrComment.isEmpty() && !captchaResponse.isEmpty();
    }
}

