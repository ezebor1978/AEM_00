package com.energyharbor.akron.core.database.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class OfferApplicationAccount {

    private final String serviceCity;

    private final String edc;

    private String meterNo;

    private String etProcessedFlag;

    private final String serviceState;

    private final String serviceZip;

    private final String customerNo;

    private final String serviceAddress;

    private String offerApplicationId;

    private String contractStartDate;

    private String contractEndDate;

    private String customerNameOnBill;

    private String nextMeterreadDate;

    private String integrationStatus;

    private String integrationDesc;

    private String integrationPCT;

    private final String residentialOnly;

    public OfferApplicationAccount(String serviceCity, String edc, String serviceState, String serviceZip, String customerNo, String serviceAddress, String residentialOnly) {
        this.serviceCity = serviceCity;
        this.edc = edc;
        this.serviceState = serviceState;
        this.serviceZip = serviceZip;
        this.customerNo = customerNo;
        this.serviceAddress = serviceAddress;
        this.residentialOnly = residentialOnly;
    }

    public String getServiceCity() {
        return serviceCity;
    }

    public String getEdc() {
        return edc;
    }

    public String getMeterNo() {
        return meterNo;
    }

    public String getEtProcessedFlag() {
        return etProcessedFlag;
    }

    public String getServiceState() {
        return serviceState;
    }

    public String getServiceZip() {
        return serviceZip;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public String getOfferApplicationId() {
        return offerApplicationId;
    }

    public String getContractStartDate() {
        return contractStartDate;
    }

    public String getContractEndDate() {
        return contractEndDate;
    }

    public String getCustomerNameOnBill() {
        return customerNameOnBill;
    }

    public String getNextMeterreadDate() {
        return nextMeterreadDate;
    }

    public String getIntegrationStatus() {
        return integrationStatus;
    }

    public String getIntegrationDesc() {
        return integrationDesc;
    }

    public String getIntegrationPCT() {
        return integrationPCT;
    }

    public String getResidentialOnly() {
        return residentialOnly;
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
