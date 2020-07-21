package com.energyharbor.akron.core.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.inject.Inject;

/**
 * Sling Model for the Fee
 */
@Model(adaptables = Resource.class)
public class Fee {

    @Self
    private transient Resource resource;

    @Inject
    @Optional
    private String priceLabel;

    @Inject
    @Optional
    private String priceValue;

    public String getPriceLabel() {
        return this.priceLabel;
    }

    public String getPriceValue() {
        return this.priceValue;
    }

    public String toJSON() {
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();
        return gson.toJson(this);
    }

    @Override
    public String toString() {
        return this.toJSON();
    }
}


