package com.energyharbor.akron.core.database.services;

import com.energyharbor.akron.core.database.models.EnrollmentData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface DatabaseService {

    /**
     * Returns a list of EDC codes related to the ZIP Code provided.
     * @param zip valid US Zip Code
     * @return a list of EDC Codes
     */
    List<String> getEDCs(String zip);


    /**
     * Returns the correct Priority Code associated with the given Product ID
     * @param id Product ID associated to an existing offer
     * @return a string with the priority code of the Offer
     */
    String getPriorityCode(String id);

    /**
     * Validates if the provided EDC exist in the provided ZIP code
     * @param zip Zip Code
     * @param edc EDC Code (Utility Code)
     * @return a boolean indicating if the EDC is valid in the provided ZIP code.
     */
    boolean validateEDC(String zip, String edc);

    /**
     * Returns a map with the product values associated to the id
     * @param id Product ID
     * @return a Map with the values PRIORITY_CODE_COLUMN, QUOTED_PRICE_AMT, PRICE_TYPE, FILENET_CONTRACT_NO, CLASS_TY_CD, PRICE_RATE_CD,PRODUCT_CRM_ID
     */
    Map<String,String> getProductValues(String id);

    /**
     * Inserts a row in the Offer Application Table and Offer Application Account
     * @param enrollmentDataList model
     * @return success a boolean with the status of the insertion to de DB
     */
    boolean insertEnrollmentForm(ArrayList<EnrollmentData> enrollmentDataList);

}
