package com.energyharbor.akron.core.services.impl;

import com.adobe.acs.commons.email.EmailService;
import com.energyharbor.akron.core.services.EmailNotificationsService;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = EmailNotificationsServiceImpl.EmailNotificationsServiceConfig.class)
public class EmailNotificationsServiceImpl implements EmailNotificationsService {

    @ObjectClassDefinition(name = "Email Recipients Configuration", description = "Configuration of the email recipients")
    @interface EmailNotificationsServiceConfig {
        @AttributeDefinition(
            name = "Contact Emails",
            description = "Email address(or multiple) that will recieve the contact form.",
            type = AttributeType.STRING,
            cardinality = 9999)
        String[] contactEmails() default "";

        @AttributeDefinition(
            name = "Request a Quote Emails",
            description = "Email address(or multiple) that will recieve the request a quote form.",
            type = AttributeType.STRING,
            cardinality = 9999)
        String[] quoteEmails() default "";
    }

    @Reference
    private EmailService EmailAPI;

    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final String CONTACT_US_TEMPLATE_PATH = "/etc/notification/email/akron/contact-us.txt";
    private static final String REQUEST_A_QUOTE_TEMPLATE_PATH = "/etc/notification/email/akron/request-a-quote.txt";
    private String[] contactEmails;
    private String[] quoteEmails;

    @Activate
    @Modified
    protected void activate(final EmailNotificationsServiceConfig configuration) throws Exception {
        log.info(String.format("%s starting.", this.getClass()));
        contactEmails = configuration.contactEmails();
        quoteEmails = configuration.quoteEmails();
    }


    public boolean sendRequestQuoteEmail(JsonObject data, String fileName, DataSource file) throws IOException {
        List<String> failureList;
        boolean success = true;

        if(!Strings.isNullOrEmpty(quoteEmails[0])) {
            try {
                Map<String, String> emailParams = new HashMap<String,String>();
                Map<String, DataSource> attachments = new HashMap<>();

                emailParams.put("firstName", data.get("first_name").getAsString());
                emailParams.put("lastName", data.get("last_name").getAsString());
                emailParams.put("phoneNumber", data.get("phone_number").getAsString());
                emailParams.put("email", data.get("email_address").getAsString());
                emailParams.put("jobTitle", data.get("job_title").getAsString());
                emailParams.put("company", data.get("company").getAsString());
                emailParams.put("mailingAddress", data.get("mailing_address").getAsString());
                emailParams.put("city", data.get("city").getAsString());
                emailParams.put("state", data.get("state").getAsString());
                emailParams.put("zipCode", data.get("zip_code").getAsString());
                emailParams.put("preferredContact", data.get("preferred_contact").getAsString());
                emailParams.put("utility", data.get("utility").getAsString());
                emailParams.put("approximateMonthlyBill", data.get("approximate_monthly_bill").getAsString());
                emailParams.put("facilityType", data.get("facility_type").getAsString());
                emailParams.put("businessType", data.get("business_type").getAsString());
                emailParams.put("decisionTimeframe", data.get("decision_timeframe").getAsString());
                emailParams.put("howDidYouHear", data.get("how_did_you_hear").getAsString());
                emailParams.put("questionsComments", data.get("questions_comments").getAsString());

                if (file != null) {
                    attachments.put(fileName, file);
                    failureList = EmailAPI.sendEmail(REQUEST_A_QUOTE_TEMPLATE_PATH, emailParams, attachments, quoteEmails);
                } else {
                    failureList = EmailAPI.sendEmail(REQUEST_A_QUOTE_TEMPLATE_PATH, emailParams, quoteEmails);
                }


                if (failureList != null && failureList.isEmpty()) {
                    log.debug("Email sent successfully to the recipients");
                } else {
                    log.error(String.format("Email was not sent to: %s", String.join(",", failureList)));
                    success = false;
                }

            } catch (Exception e) {
                log.error("An error occurred while processing the request", e);
                success = false;
            }
        } else {
            log.error("Recipients list is empty");
            success = false;
        }

        return success;
    }

    public boolean sendContactUsEmail(JsonObject data) throws IOException {
        List<String> failureList;
        boolean success = true;

        if(!Strings.isNullOrEmpty(contactEmails[0])) {
            try {
                Map<String, String> emailParams = new HashMap<String,String>();
                emailParams.put("email", data.get("email_address").getAsString());
                emailParams.put("questionConcerning", data.get("question_concerning").getAsString());
                emailParams.put("firstName", data.get("first_name").getAsString());
                emailParams.put("lastName", data.get("last_name").getAsString());
                emailParams.put("phoneNumber", data.get("phone_number").getAsString());
                emailParams.put("company", data.get("company").getAsString());
                emailParams.put("customerType", data.get("customerType").getAsString());
                emailParams.put("message", data.get("question_or_comment").getAsString());

                failureList = EmailAPI.sendEmail(CONTACT_US_TEMPLATE_PATH, emailParams, contactEmails);

                if (failureList != null && failureList.isEmpty()) {
                    log.debug("Email sent successfully to the recipients");
                } else {
                    log.error(String.format("Email was not sent to: %s", String.join(",", failureList)));
                    success = false;
                }

            } catch (Exception e) {
                log.error("An error occurred while processing the request", e);
                success = false;
            }
        } else {
            log.error("Recipients list is empty");
            success = false;
        }

        return success;
    }
}
