package com.energyharbor.akron.core.servlets;

import com.energyharbor.akron.core.database.services.DatabaseService;
import com.energyharbor.akron.core.models.EDCDetails;
import com.energyharbor.akron.core.services.OfferEDCService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.List;

/**
 * Servlet that returns a list of EDC details based on the zip code provided.
 */
@Component(service=Servlet.class,
    property={
        Constants.SERVICE_DESCRIPTION + "=EDC's by ZIP",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.paths="+ "/bin/akron/edc"
    })
public class EDCByZipServlet extends SlingSafeMethodsServlet {

    @Reference
    DatabaseService databaseService;

    @Reference
    OfferEDCService offerEDCService;

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) throws IOException {
        Gson gson = new Gson();
        JsonObject data = new JsonObject();
        resp.setContentType("application/json");
        String zip = req.getParameter("zip");

        try {
            List<String> edcs = databaseService.getEDCs(zip);
            List<EDCDetails> edcDetails = offerEDCService.getEDCDetails(req.getResourceResolver(), String.join(",", edcs));
            data.add("utilities", gson.toJsonTree(edcDetails));
            gson.toJson(data, resp.getWriter());
        } catch (Exception e) {
            log.error(String.format("An error occurred while trying to get the EDCs related to the ZIP code %s ", zip), e);
            UtilsServlet.handleAPIException(new Exception("An error occurred while processing the request."), gson, resp);
        }
    }
}

