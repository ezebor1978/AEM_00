package com.energyharbor.akron.core.database.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class EnrollmentData {

    private final OfferApplication offerApplication;

    private final OfferApplicationAccount offerApplicationAccount;

    private String offerApplicationId;

    private String offerApplicationAccountId;

    public EnrollmentData(OfferApplication offerApplication, OfferApplicationAccount offerApplicationAccount) {
        this.offerApplication = offerApplication;
        this.offerApplicationAccount = offerApplicationAccount;
    }

    public EnrollmentData(OfferApplication offerApplication, OfferApplicationAccount offerApplicationAccount, String offerApplicationId, String offerApplicationAccountId) {
        this.offerApplication = offerApplication;
        this.offerApplicationAccount = offerApplicationAccount;
        this.offerApplicationId = offerApplicationId;
        this.offerApplicationAccountId = offerApplicationAccountId;
    }

    public OfferApplication getOfferApplication() {
        return offerApplication;
    }

    public OfferApplicationAccount getOfferApplicationAccount() {
        return offerApplicationAccount;
    }

    public String getOfferApplicationId() {
        return offerApplicationId;
    }

    public String getOfferApplicationAccountId() {
        return offerApplicationAccountId;
    }

    public String toJSON() {
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();
        return gson.toJson(this);
    }

    public String toString() {
        return this.toJSON();
    }
}
