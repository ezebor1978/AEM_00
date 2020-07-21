package com.energyharbor.akron.core.database.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class OfferApplication {

    private final String lastName;

    private final String quotedPriceAMT;

    private final String priceRateCD;

    private final String completed;

    private String offerGroupCode;

    private final String state;

    private final String phoneNumber;

    private final String referrersSource;

    private final String firstName;

    private final String phoneArea;

    private final String email;

    private String confirmationNumber;

    private final String classTYCD;

    private final String acceptedTerms;

    private final String zip;

    private final String priceType;

    private final String phoneLast;

    private final String priorityCD;

    private final String companyName;

    private String latStepCompleted;

    private String offerCode;

    private final String product_ID;

    private final String city;

    private final String specialOffers;

    private final String phoneExChange;

    private final String fileNetContractActNo;

    private final String offerType;

    private final String createdDate;

    private final String address1;

    private String address2;

    private String lastModified;

    private String productID;

    private String employee;

    private String integrationStatus;

    private String integrationDesc;

    private String partnerId;

    private String mobilePhone;

    private final String smsConsent;

    private String externalKey;

    private String enrollmentFormUrl;

    private String annualUsage;

    private String referralCode;

    private final String productCRMID;

    public OfferApplication( String lastName, String quotedPriceAMT, String priceRateCD, String completed, String state,
                         String phoneNumber, String referrersSource, String firstName, String phoneArea, String email, String classTYCD,
                         String acceptedTerms, String zip, String priceType, String phoneLast, String priorityCD, String companyName, String product_ID,
                         String city, String specialOffers, String phoneExChange, String fileNetContractActNo,
                         String offerType, String createdDate, String address1, String smsConsent, String productCRMID) {

        this.lastName = lastName;
        this.quotedPriceAMT = quotedPriceAMT;
        this.priceRateCD = priceRateCD;
        this.completed = completed;
        this.state = state;
        this.phoneNumber = phoneNumber;
        this.referrersSource = referrersSource;
        this.firstName = firstName;
        this.phoneArea = phoneArea;
        this.email = email;
        this.classTYCD = classTYCD;
        this.acceptedTerms = acceptedTerms;
        this.zip = zip;
        this.priceType = priceType;
        this.phoneLast = phoneLast;
        this.priorityCD = priorityCD;
        this.companyName = companyName;
        this.product_ID = product_ID;
        this.city = city;
        this.specialOffers = specialOffers;
        this.phoneExChange = phoneExChange;
        this.fileNetContractActNo = fileNetContractActNo;
        this.offerType = offerType;
        this.createdDate = createdDate;
        this.address1 = address1;
        this.smsConsent = smsConsent;
        this.productCRMID = productCRMID;
    }

    public String getLastName() {
        return lastName;
    }

    public String getQuotedPriceAMT() {
        return quotedPriceAMT;
    }

    public String getPriceRateCD() {
        return priceRateCD;
    }

    public String getCompleted() {
        return completed;
    }

    public String getOfferGroupCode() {
        return offerGroupCode;
    }

    public String getState() {
        return state;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getReferrersSource() {
        return referrersSource;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getPhoneArea() {
        return phoneArea;
    }

    public String getEmail() {
        return email;
    }

    public String getConfirmationNumber() {
        return confirmationNumber;
    }

    public String getClassTYCD() {
        return classTYCD;
    }

    public String getAcceptedTerms() {
        return acceptedTerms;
    }

    public String getZip() {
        return zip;
    }

    public String getPriceType() {
        return priceType;
    }

    public String getPhoneLast() {
        return phoneLast;
    }

    public String getPriorityCD() {
        return priorityCD;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getLatStepCompleted() {
        return latStepCompleted;
    }

    public String getOfferCode() {
        return offerCode;
    }

    public String getProduct_ID() {
        return product_ID;
    }

    public String getCity() {
        return city;
    }

    public String getSpecialOffers() {
        return specialOffers;
    }

    public String getPhoneExChange() {
        return phoneExChange;
    }

    public String getFileNetContractActNo() {
        return fileNetContractActNo;
    }

    public String getOfferType() {
        return offerType;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getLastModified() {
        return lastModified;
    }

    public String getProductID() {
        return productID;
    }

    public String getEmployee() {
        return employee;
    }

    public String getIntegrationStatus() {
        return integrationStatus;
    }

    public String getIntegrationDesc() {
        return integrationDesc;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public String getSmsConsent() {
        return smsConsent;
    }

    public String getExternalKey() {
        return externalKey;
    }

    public String getEnrollmentFormUrl() {
        return enrollmentFormUrl;
    }

    public String getAnnualUsage() {
        return annualUsage;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public String getProductCRMID() {
        return productCRMID;
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
