package com.energyharbor.akron.core.servlets;

import com.energyharbor.akron.core.services.EmailNotificationsService;
import com.energyharbor.akron.core.services.reCAPTCHAService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.util.ByteArrayDataSource;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Map;


/**
 * Servlet that receives the request from request a quote, and calls Email Service to send the email.
 */
@Component(service=Servlet.class,
    property={
           Constants.SERVICE_DESCRIPTION + "=Request A Quote Form Submit",
           "sling.servlet.methods=" + HttpConstants.METHOD_POST,
           "sling.servlet.paths="+ "/bin/akron/email/request-quote"
})
public class RequestAQuoteEmailServlet extends SlingAllMethodsServlet {

    @Reference
    private EmailNotificationsService emailService;

    @Reference
    private reCAPTCHAService reCAPTCHAService;

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doPost(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) throws IOException,ServletException {
        Gson gson = new Gson();

        try {
            Map< String, RequestParameter[] > params = req.getRequestParameterMap();
            JsonObject data = UtilsServlet.getRequestParameterJSON(params);
            ByteArrayDataSource file = UtilsServlet.getRequestParameterAttachment(params);
            String email = data.get("email_address").getAsString();

            if(!UtilsServlet.isValidEmailAddress(email)) {
                log.error("Invalid email address");
                UtilsServlet.handleAPIException(new Exception("Invalid email address."), gson, resp);
            } else if(!isEmptyAnyParameter(data)) {
                log.error("One of the required fields is empty.");
                UtilsServlet.handleAPIException(new Exception("One of the required fields is empty."), gson, resp);
            }else {
                boolean validCaptcha =  reCAPTCHAService.validateCaptchaResponse(data.get(UtilsServlet.CAPTCHA_RESPONSE).getAsString());
                if(validCaptcha) {
                    boolean emailSent;
                    if (file != null) {
                        emailSent = emailService.sendRequestQuoteEmail(data, file.getName(), file);
                    } else {
                        emailSent = emailService.sendRequestQuoteEmail(data, null, file);
                    }

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
            log.error("An error occurred while trying to send the Request a Quote email", e);
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
        String emailAddress = data.get("email_address").getAsString();
        String company = data.get("company").getAsString();
        String firstName = data.get("first_name").getAsString();
        String lastName = data.get("last_name").getAsString();
        String phoneNumber = data.get("phone_number").getAsString();
        String jobTitle = data.get("job_title").getAsString();
        String zipCode = data.get("zip_code").getAsString();
        String utility = data.get("utility").getAsString();
        String approximateMonthlyBill = data.get("approximate_monthly_bill").getAsString();
        String captchaResponse = data.get(UtilsServlet.CAPTCHA_RESPONSE).getAsString();

        return !emailAddress.isEmpty() && !company.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty() && !phoneNumber.isEmpty() && !jobTitle.isEmpty() && !zipCode.isEmpty() && !utility.isEmpty() && !approximateMonthlyBill.isEmpty() && !captchaResponse.isEmpty();
    }
}

