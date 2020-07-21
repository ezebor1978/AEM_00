package com.energyharbor.akron.core.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.inject.Inject;

/**
 * Sling Model for the EDC Details
 */
@Model(adaptables = Resource.class)
public class EDCDetails {

    @Self
    private transient Resource resource;

    @Inject
    @Optional
    private String name;

    @Inject
    @Optional
    private String code;

    @Inject
    @Optional
    private String phoneNumber;

    @Inject
    @Optional
    private String state;

    @Inject
    @Optional
    private String logo;

    @Inject
    @Optional
    private String cusNumLabel;

    @Inject
    @Optional
    private int cusNumLength;

    @Inject
    @Optional
    private String cusNumChars;

    @Inject
    @Optional
    private String wrongUtilityModal;

    @Inject
    @Optional
    private String helpFindModal;

    @Inject
    @Optional
    private String whatThisModal;

    public String getName() {
        return this.name;
    }

    public String getCode() {
        return this.code;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getLogo() {
        return this.logo;
    }

    public String getCusNumLabel() {
        return this.cusNumLabel;
    }

    public int getCusNumLength() {
        return this.cusNumLength;
    }

    public String getCusNumChars() {
        return this.cusNumChars;
    }

    public String getWrongUtilityModal() {
        return this.wrongUtilityModal;
    }

    public String getHelpFindModal() {
        return this.helpFindModal;
    }

    public String getWhatThisModal() {
        return this.whatThisModal;
    }

    public String getState() {
        return this.state;
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


