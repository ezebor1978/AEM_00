package com.energyharbor.akron.core.sightly;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.contentsync.handler.util.RequestResponseFactory;
import com.day.cq.wcm.api.WCMMode;
import org.apache.jackrabbit.vault.util.Constants;
import org.apache.sling.engine.SlingRequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class I18nDictionary extends WCMUsePojo {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private String json;

    @Override
    public void activate() {
        SlingRequestProcessor requestProcessor = getSlingScriptHelper().getService(SlingRequestProcessor.class);
        RequestResponseFactory requestResponseFactory = getSlingScriptHelper().getService(RequestResponseFactory.class);

        this.getResponse().setCharacterEncoding(Constants.ENCODING);

        if (requestProcessor != null && requestResponseFactory != null) {
            String locale = get("locale", Locale.class).toLanguageTag();
            String requestPath = String.format("/libs/cq/i18n/dict.%s.json", locale);

            HttpServletRequest req = requestResponseFactory.createRequest("GET", requestPath);
            try {
                req.setCharacterEncoding(Constants.ENCODING.toUpperCase());
            } catch (UnsupportedEncodingException e) {
                // pass
            }
            WCMMode.DISABLED.toRequest(req);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            HttpServletResponse resp = requestResponseFactory.createResponse(out);
            resp.setCharacterEncoding(Constants.ENCODING.toUpperCase());

            try {
                requestProcessor.processRequest(req, resp, getRequest().getResourceResolver());
                this.json = out.toString(Constants.ENCODING);
            } catch (Exception e) {
                this.json = "{}";
            }
        } else {
            log.warn("requestProcessor or requestResponseFactory is null");
        }
    }

    public String getJson() {
        return json;
    }
}



