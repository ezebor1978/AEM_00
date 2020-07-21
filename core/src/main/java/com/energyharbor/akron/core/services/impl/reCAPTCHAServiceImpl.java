package com.energyharbor.akron.core.services.impl;

import com.energyharbor.akron.core.models.CaptchaResponse;
import com.energyharbor.akron.core.services.reCAPTCHAService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = reCAPTCHAServiceImpl.reCAPTCHAServiceConfig.class)
public class reCAPTCHAServiceImpl implements reCAPTCHAService {

    @ObjectClassDefinition(name = "Google reCAPTCHA Configuration")
    @interface reCAPTCHAServiceConfig {
        @AttributeDefinition(
            name = "Verify Url",
            description = "This is the url to verify reCAPTCHA responses")
        String verifyUrl() default "https://www.google.com/recaptcha/api/siteverify";

        @AttributeDefinition(
            name = "Site Key",
            description = "Key to generate the reCAPTCHA component in the frontend")
        String siteKey() default "";

        @AttributeDefinition(
            name = "Secret Key",
            description = "Use this for communication between your site and Google")
        String secretKey() default "";
    }

    private String siteKey;
    private String secretKey;
    private String verifyUrl;
    private static final Gson gson = new GsonBuilder().serializeNulls().create();
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Activate
    protected void activate(final reCAPTCHAServiceConfig configuration) {
        this.siteKey = configuration.siteKey();
        this.secretKey = configuration.secretKey();
        this.verifyUrl = configuration.verifyUrl();
    }

    public String getSiteKey() {
        return this.siteKey;
    }

    public boolean validateCaptchaResponse (String captchaResponse) {
        try {
            String inputLine;
            StringBuilder content = new StringBuilder();
            String body = String.format("secret=%s&response=%s", this.secretKey, captchaResponse);
            URL base = new URL(this.verifyUrl);
            HttpURLConnection conn = (HttpURLConnection) base.openConnection();
            byte[] postData = body.getBytes(StandardCharsets.UTF_8);

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
            conn.setRequestProperty("charset", "utf-8");

            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postData);
            }
            BufferedReader in = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            conn.disconnect();
            CaptchaResponse parsedCaptcha = gson.fromJson(content.toString(), CaptchaResponse.class);

            return parsedCaptcha.isSuccess();
        } catch (IOException e) {
            log.error("An error occurred while verifying a captcha token", e);
            return false;
        }
    }
}


