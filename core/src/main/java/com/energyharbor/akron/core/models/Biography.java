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
 * Sling Model for the Biography
 */
@Model(adaptables = Resource.class)
public class Biography {

    @Self
    private transient Resource resource;

    @Inject
    @Optional
    @Named("jcr:title")
    private String title;

    @Inject
    @Optional
    @Named("jcr:description")
    private String description;

    @Inject
    @Optional
    private String bioImage;

    @Inject
    @Optional
    private String bioImageAltText;

    @Inject
    @Optional
    private String job;

    private String bioPath;

    public void init(ResourceResolver resolver) {
        this.bioPath = resolver.map(resource.getPath().split("/jcr:content")[0]);
    }

    public String getPath() { return resource.getPath(); }

    public String getBioPath() { return bioPath; }

    public String getTitle() { return title; }

    public String getDescription() { return description; }

    public String getBioImage() { return bioImage; }

    public String getBioImageAltText() { return bioImageAltText; }

    public String getJob() { return job; }

    public String toJSON() {
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();
        return gson.toJson(this);
    }
}


