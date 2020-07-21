package com.energyharbor.akron.core.sightly;

import com.adobe.cq.sightly.WCMUsePojo;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkCheckerConfiguration extends WCMUsePojo {

    private static final String CLASS = "com.day.cq.rewriter.linkchecker.impl.LinkCheckerTransformerFactory";
    private static final String CONFIG_NAME = "linkcheckertransformer.stripHtmltExtension";

    private boolean stripHtmlExtension = false;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void activate() {
        ConfigurationAdmin configAdmin = getSlingScriptHelper().getService(ConfigurationAdmin.class);
        try {
            org.osgi.service.cm.Configuration conf = configAdmin.getConfiguration(CLASS);
            stripHtmlExtension = PropertiesUtil.toBoolean(conf.getProperties().get(CONFIG_NAME),false);
        } catch (Exception e) {
            log.error("An error occurred while retrieving the linkChecker configuration");
        }
    }

    public boolean getStripHtmlExtension() {
        return this.stripHtmlExtension;
    }
}


