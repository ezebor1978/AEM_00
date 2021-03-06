/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.energyharbor.akron.core.servlets;

import com.energyharbor.akron.core.database.services.DatabaseService;
import com.energyharbor.akron.core.models.EDCDetails;
import com.energyharbor.akron.core.services.OfferEDCService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Servlet that writes some sample content into the response.
 */
@Component(service=Servlet.class,
    property={
           Constants.SERVICE_DESCRIPTION + "=Simple Demo Servlet",
           "sling.servlet.methods=" + HttpConstants.METHOD_GET,
           "sling.servlet.paths="+ "/bin/akron/testservlet",
    })
public class SimpleServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    DatabaseService databaseService;

    @Reference
    OfferEDCService offerEDCService;

    @Override
    protected void doGet(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {

        String zip = req.getParameter("zip");
        String pc = databaseService.getPriorityCode("57655");
        List<String> edcs = databaseService.getEDCs(zip);
        List<EDCDetails> edcDetails = offerEDCService.getEDCDetails(req.getResourceResolver(), String.join(",", edcs));

        resp.setContentType("text/plain");
        resp.getWriter().write("List of Utilities: " + Arrays.toString(edcDetails.toArray()));
    }
}
