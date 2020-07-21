package com.energyharbor.akron.core.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Sling Model for the Community
 */
@Model(adaptables = Resource.class)
public class Community {

    @Self
    private transient Resource resource;

    @Inject
    @Optional
    @Named("jcr:title")
    private String communityName;

    @Inject
    @Optional
    private String county;

    @Inject
    @Optional
    private String edcCode;

    private String utility;

    private String state;

    private String url;

    public void init(ResourceResolver resolver, EDCDetails details) {
        this.url = resolver.map(resource.getPath().split("/jcr:content")[0]);
        utility = details.getName();
        state = details.getState();
    }

    public String getName() { return communityName; }

    public String getCounty() { return county; }

    public String getEdcCode() { return edcCode; }

    public String getUtility() { return utility; }

    public String getState() { return state; }

    public String getUrl() { return url; }

    public String toJSON() {
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();
        return gson.toJson(this);
    }
}


