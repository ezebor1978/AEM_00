package com.energyharbor.akron.core.services.impl;

import com.energyharbor.akron.core.database.models.EnrollmentData;
import com.energyharbor.akron.core.database.models.OfferApplication;
import com.energyharbor.akron.core.database.models.OfferApplicationAccount;
import com.energyharbor.akron.core.database.services.DatabaseService;
import com.energyharbor.akron.core.services.EnrollmentService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

@Component(immediate = true)
public class EnrollmentServiceImpl implements EnrollmentService {

    @Reference
    DatabaseService databaseService;

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    public boolean enroll(JsonObject data) {
        boolean success = true;
        ArrayList<EnrollmentData> enrollmentDataList = new ArrayList<>();

        try {
            JsonArray enrollmentData = data.getAsJsonArray("enrollmentData");

            enrollmentData.forEach(dataItem -> {
                JsonObject enrollmentDataItem = dataItem.getAsJsonObject();

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy hh.mm.ss.SSS a");
                String date = dateFormat.format(new Date());

                String productId = enrollmentDataItem.get("product_id").getAsString();
                Map productValues = databaseService.getProductValues(productId);

                String quotedPriceAMT = (productValues.get("QUOTED_PRICE_AMT") == null ? null : productValues.get("QUOTED_PRICE_AMT").toString());
                String priceRateCD = (productValues.get("PRICE_RATE_CD") == null ? null : productValues.get("PRICE_RATE_CD").toString());
                String classTYCD = (productValues.get("CLASS_TY_CD") == null ? null : productValues.get("CLASS_TY_CD").toString());
                String priceType = (productValues.get("PRICE_TYPE") == null ? null : productValues.get("PRICE_TYPE").toString());
                String priorityCD = (productValues.get("PRIORITY_CD") == null ? null : productValues.get("PRIORITY_CD").toString());
                String fileNetContractActNo = (productValues.get("FILENET_CONTRACT_NO") == null ? null : productValues.get("FILENET_CONTRACT_NO").toString());
                String productCRMID = (productValues.get("PRODUCT_CRM_ID") == null ? null : productValues.get("PRODUCT_CRM_ID").toString());

                String serviceCity = enrollmentDataItem.get("service_city").getAsString().isEmpty() ? enrollmentDataItem.get("mailing_city").getAsString() : enrollmentDataItem.get("service_city").getAsString();
                String serviceAddress = enrollmentDataItem.get("service_address").getAsString().isEmpty() ? enrollmentDataItem.get("mailing_street_address").getAsString() : enrollmentDataItem.get("service_address").getAsString();
                String serviceZip = enrollmentDataItem.get("service_zip") == null ? enrollmentDataItem.get("mailing_zip_code").getAsString() : enrollmentDataItem.get("service_zip").getAsString();
                String serviceState = enrollmentDataItem.get("service_state").getAsString().isEmpty() ? enrollmentDataItem.get("mailing_state").getAsString() : enrollmentDataItem.get("service_state").getAsString();

                String formType = enrollmentDataItem.get("form_type").getAsString();
                if(!formType.isEmpty() && formType.length() >= 1 ){
                    formType = formType.substring(0, 1).toUpperCase() + formType.substring(1);
                } else {
                    formType = null;
                }

                OfferApplication offerApplication = new OfferApplication(
                    enrollmentDataItem.get("last_name").getAsString(),
                    quotedPriceAMT,
                    priceRateCD,
                    "Y",
                    enrollmentDataItem.get("mailing_state").getAsString(),
                    enrollmentDataItem.get("phone_number").getAsString(),
                    enrollmentDataItem.get("hear_about_us").getAsString(),
                    enrollmentDataItem.get("first_name").getAsString(),
                    enrollmentDataItem.get("phone_area").getAsString(),
                    enrollmentDataItem.get("email_address").getAsString(),
                    classTYCD,
                    (enrollmentDataItem.get("accept_disclaimer").getAsString().equals("true") ? "Y" : "N"),
                    enrollmentDataItem.get("mailing_zip_code").getAsString(),
                    priceType,
                    enrollmentDataItem.get("phone_last").getAsString(),
                    priorityCD,
                    enrollmentDataItem.get("company_business").getAsString().isEmpty() ? "" : enrollmentDataItem.get("company_business").getAsString(),
                    productId,
                    enrollmentDataItem.get("mailing_city").getAsString(),
                    "Y",
                    enrollmentDataItem.get("phone_exchange").getAsString(),
                    fileNetContractActNo,
                    formType,
                    date,
                    enrollmentDataItem.get("mailing_street_address").getAsString(),
                    enrollmentDataItem.get("accept_alerts").getAsBoolean() ? "Y" : "N",
                    productCRMID);

                OfferApplicationAccount offerApplicationAccount = new OfferApplicationAccount(
                    serviceCity,
                    enrollmentDataItem.get("edc_code").getAsString(),
                    serviceState,
                    serviceZip,
                    enrollmentDataItem.get("customer_number").getAsString(),
                    serviceAddress,
                    enrollmentDataItem.get("residential_purpose").getAsString().equals("Yes") ? "Y" : "N"
                );

              enrollmentDataList.add(new EnrollmentData(offerApplication, offerApplicationAccount));
            });

            success = databaseService.insertEnrollmentForm(enrollmentDataList);

        } catch (Exception e) {
            log.error("An error occurred while processing the request", e);
            success = false;
        }

        return success;
    }

}
