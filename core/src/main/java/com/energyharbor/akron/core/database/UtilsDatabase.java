package com.energyharbor.akron.core.database;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilsDatabase {

    private static final Pattern ZIPCODE_PATTERN = Pattern.compile("^[0-9]{5}$");
    private static final Pattern PRODUCT_ID_PATTERN = Pattern.compile("^[0-9]{1,5}$");
    private static final Pattern EDC_CODE_PATTERN = Pattern.compile("^[a-zA-Z0-9]{1,8}$");

    /**
     * Checks if the ZipCode provided is valid
     *
     * @param zip zipcode
     * @return boolean
     */
    public static boolean isValidZipCode(String zip) {
        Matcher matcher = ZIPCODE_PATTERN.matcher(zip);
        return matcher.matches();
    }

    /**
     * Checks if the Product ID provided is valid
     *
     * @param id Product ID
     * @return boolean
     */
    public static boolean isValidProductId(String id) {
        Matcher matcher = PRODUCT_ID_PATTERN.matcher(id);
        return matcher.matches();
    }

    /**
     * Checks if the EDC Code provided is valid
     *
     * @param code EDC Code
     * @return boolean
     */
    public static boolean isValidEDCCode(String code) {
        Matcher matcher = EDC_CODE_PATTERN.matcher(code);
        return matcher.matches();
    }
}


