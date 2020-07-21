package com.energyharbor.akron.core.database.services.impl;

import com.day.commons.datasource.poolservice.DataSourcePool;
import com.energyharbor.akron.core.database.UtilsDatabase;
import com.energyharbor.akron.core.database.models.EnrollmentData;
import com.energyharbor.akron.core.database.models.OfferApplication;
import com.energyharbor.akron.core.database.models.OfferApplicationAccount;
import com.energyharbor.akron.core.database.services.DatabaseService;
import com.google.common.base.Strings;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = DatabaseServiceImpl.DatabaseServiceConfig.class)
public class DatabaseServiceImpl implements DatabaseService {

    @ObjectClassDefinition(name = "Database Service Configuration", description = "Configuration of properties for the Database service")
    @interface DatabaseServiceConfig {
        @AttributeDefinition(
            name = "Datasource Name",
            description = "Should match exactly the name of the datasource provided in the configuration of the JDBC connection pool",
            type = AttributeType.STRING)
        String datasourceName();
    }

    private String datasourceName;
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String UTILITY_TABLE = "CSAN89.ZIP_TO_UTILITY";
    private static final String PRODUCT_TABLE = "CSAN89.PRODUCT";
    private static final String SEQUENCE_OFFER_ID= "CSAN89.SEQ_OFFER_ID.NEXTVAL";

    private static final String OFFER_APPLICATION = "CSAN89.OFFER_APPLICATION(" +
        "ID, LASTNAME, QUOTEDPRICEAMT, PRICERATECD, COMPLETED, STATE, PHONENUMBER, REFERRERSOURCE, FIRSTNAME, PHONEAREA, EMAIL, CLASSTYCD, ACCEPTEDTERMS, ZIP, PRICETYPE, " +
        "PHONELAST, PRIORITYCD, COMPANYNAME, PRODUCT_ID, CITY, SPECIALOFFERS, PHONEEXCHANGE, FILENETCONTRACTNO, OFFERTYPE, CREATEDDATE, ADDRESS1, SMS_CONSENT, PRODUCT_CRM_ID)";

    private static final String OFFER_APPLICATION_ACCOUNT = "CSAN89.OFFER_APPLICATION_ACCOUNT(" +
        "ID, SERVICECITY, EDC, SERVICESTATE, SERVICEZIP, CUSTOMERNO, SERVICEADDRESS, OFFERAPPLICATIONID, RESIDENTIAL_ONLY)";

    private static final String UTILITY_COLUMN = "UTILITY_CODE";
    private static final String PRIORITY_CODE_COLUMN = "PRIORITY_CD";
    private static final String QUOTED_PRICE_AMT = "QUOTED_PRICE_AMT";
    private static final String PRICE_TYPE = "PRICE_TYPE";
    private static final String FILENET_CONTRACT_NO = "FILENET_CONTRACT_NO";
    private static final String CLASS_TY_CD = "CLASS_TY_CD";
    private static final String PRICE_RATE_CD = "PRICE_RATE_CD";
    private static final String PRODUCT_CRM_ID = "PRODUCT_CRM_ID";
    private static final String NEXT_VAL = "NEXTVAL";

    private static final String QUERY_GET_LIST_OF_EDC = "SELECT * FROM " + UTILITY_TABLE + " WHERE ZIP = ?";
    private static final String QUERY_VALIDATE_EDC = "SELECT * FROM " + UTILITY_TABLE + " WHERE ZIP = ? AND UTILITY_CODE = ?";
    private static final String QUERY_GET_PRIORITY_CODE = "SELECT * FROM " + PRODUCT_TABLE + " WHERE PRODUCT_ID = ?";
    private static final String QUERY_GET_PRODUCT = "SELECT * FROM " + PRODUCT_TABLE + " WHERE PRODUCT_ID = ?";
    private static final String QUERY_GET_SEQUENCE_ID = "SELECT " + SEQUENCE_OFFER_ID + " FROM DUAL";

    private static final String QUERY_INSERT_OFFER_APPLICATION = "INSERT INTO " + OFFER_APPLICATION + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String QUERY_INSERT_OFFER_APPLICATION_ACCOUNT = "INSERT INTO " + OFFER_APPLICATION_ACCOUNT + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

    @Reference
    private DataSourcePool dataSourceService;

    @Activate
    protected void activate(final DatabaseServiceConfig configuration) {
        this.datasourceName = configuration.datasourceName();
    }

    /**
     * Returns a connection object from the JDBC connection pool using the datasource name provided in the configuration of the component.
     * @return a connection object
     */
    private Connection getDatabaseConnection() {
        Connection conn = null;

        try {
            DataSource dataSource = (DataSource) dataSourceService.getDataSource(this.datasourceName);
            conn = dataSource.getConnection();
        } catch (Exception e) {
            log.error(String.format("An error occurred while retrieving a connection object from the JDBC connection pool with the datasource name: %s", this.datasourceName), e);
        }

        return conn;
    }

    /**
     * Helper method to run a provided SQL query.
     * The result is a list of maps in each map are the results per row of the columns fetched
     * The columns to return in the results must be determined by parameters.
     *
     * @param query SQL query to run
     * @param parameters SQL parameters to inject in the PreparedStatement after initializing the query
     * @param columnNames name of the columns of each fetched row that will be returned. Each column must be of type string.
     * @return a connection object
     */
    private List<Map<String,String>> executeSelectStatement(String query, List<String> parameters, List<String> columnNames, String errorMessage) {
        boolean connectionReady = false;
        List<Map<String,String>> results = new ArrayList<Map<String,String>>();

        try (
            Connection conn = this.getDatabaseConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query)
        ){
            connectionReady = true;
            conn.setAutoCommit(false);

            for(int i=0; i<parameters.size(); i++) {
                // Index of the PreparedStatement params starts in 1
                preparedStatement.setString(i+1, parameters.get(i));
            }

            try (ResultSet resultSet =  preparedStatement.executeQuery()){
                while(resultSet.next()) {
                    Map<String, String> map = new HashMap<>();
                    for (String name : columnNames){
                        map.put(name,resultSet.getString(name));
                    }
                    results.add(map);
                }
            }

            conn.setAutoCommit(true);
        } catch (Exception e) {
            if(connectionReady) {
                log.error(errorMessage, e);
            } else {
                log.error("Connection object from the JDBC pool couldn't be retrieved, this is likely related to using a wrong Datasource name or invalid credentials in the JDBC Connection Pool service");
            }
        }

        return results;
    }

    /**
     * Gets an ID from the SEQ_OFFER_ID sequence for the offer tables.
     *
     * @return a unique id
     */
    private String getOfferID() {
        String id = null;
        List<String> columns = new ArrayList<String>();
        columns.add(NEXT_VAL);

        List<Map<String, String>> result = this.executeSelectStatement(QUERY_GET_SEQUENCE_ID,  new ArrayList<>(), columns, ("An error occurred while getting a new sequence id from SEQ_OFFER_ID"));
        if(!result.isEmpty() && result.get(0) != null) {
            id = result.get(0).get(NEXT_VAL);
        }

        return id;
    }

    /**
     * Helper method to run a series of defined SQL queries
     * The result is a insert in the defined tables
     * The values to be added in the tables are defined in the lists
     *
     * @param enrollmentDataList
     * @param errorMessage
     * @return success status of the insertion
     */
    private boolean insertOfferApplicationAndOfferApplicationAccount(ArrayList<EnrollmentData> enrollmentDataList, String errorMessage) {

        boolean connectionReady = false;
        boolean success = true;
        ArrayList<Boolean> preparedStatementSuccess = new ArrayList<>();
        ArrayList<PreparedStatement> preparedStatements = new ArrayList<>();

        try (
            Connection conn = this.getDatabaseConnection()
        ){
            connectionReady = true;
            conn.setAutoCommit(false);

            if (enrollmentDataList.size() > 0) {
                enrollmentDataList.forEach(dataItem -> {
                    try {
                        PreparedStatement offerApplicationPreparedStatement = conn.prepareStatement(QUERY_INSERT_OFFER_APPLICATION, Statement.RETURN_GENERATED_KEYS);
                        PreparedStatement offerApplicationAccountPreparedStatement = conn.prepareStatement(QUERY_INSERT_OFFER_APPLICATION_ACCOUNT, Statement.RETURN_GENERATED_KEYS);
                        OfferApplication offerApplication= dataItem.getOfferApplication();
                        OfferApplicationAccount offerApplicationAccount = dataItem.getOfferApplicationAccount();

                        //OfferApplication add parameters to the offerApplicationPreparedStatement
                        offerApplicationPreparedStatement.setString(1, dataItem.getOfferApplicationId());
                        offerApplicationPreparedStatement.setString(2, offerApplication.getLastName());
                        offerApplicationPreparedStatement.setString(3, offerApplication.getQuotedPriceAMT());
                        offerApplicationPreparedStatement.setString(4, offerApplication.getPriceRateCD());
                        offerApplicationPreparedStatement.setString(5, offerApplication.getCompleted());
                        offerApplicationPreparedStatement.setString(6, offerApplication.getState());
                        offerApplicationPreparedStatement.setString(7, offerApplication.getPhoneNumber());
                        offerApplicationPreparedStatement.setString(8, offerApplication.getReferrersSource());
                        offerApplicationPreparedStatement.setString(9, offerApplication.getFirstName());
                        offerApplicationPreparedStatement.setString(10, offerApplication.getPhoneArea());
                        offerApplicationPreparedStatement.setString(11, offerApplication.getEmail());
                        offerApplicationPreparedStatement.setString(12, offerApplication.getClassTYCD());
                        offerApplicationPreparedStatement.setString(13, offerApplication.getAcceptedTerms());
                        offerApplicationPreparedStatement.setString(14, offerApplication.getZip());
                        offerApplicationPreparedStatement.setString(15, offerApplication.getPriceType());
                        offerApplicationPreparedStatement.setString(16, offerApplication.getPhoneLast());
                        offerApplicationPreparedStatement.setString(17, offerApplication.getPriorityCD());
                        offerApplicationPreparedStatement.setString(18, offerApplication.getCompanyName());
                        offerApplicationPreparedStatement.setString(19, offerApplication.getProduct_ID());
                        offerApplicationPreparedStatement.setString(20, offerApplication.getCity());
                        offerApplicationPreparedStatement.setString(21, offerApplication.getSpecialOffers());
                        offerApplicationPreparedStatement.setString(22, offerApplication.getPhoneExChange());
                        offerApplicationPreparedStatement.setString(23, offerApplication.getFileNetContractActNo());
                        offerApplicationPreparedStatement.setString(24, offerApplication.getOfferType());
                        offerApplicationPreparedStatement.setString(25, offerApplication.getCreatedDate());
                        offerApplicationPreparedStatement.setString(26, offerApplication.getAddress1());
                        offerApplicationPreparedStatement.setString(27, offerApplication.getSmsConsent());
                        offerApplicationPreparedStatement.setString(28, offerApplication.getProductCRMID());

                        //OfferApplicationAccount add parameters to the offerApplicationAccountPreparedStatement
                        offerApplicationAccountPreparedStatement.setString(1, dataItem.getOfferApplicationAccountId());
                        offerApplicationAccountPreparedStatement.setString(2, offerApplicationAccount.getServiceCity());
                        offerApplicationAccountPreparedStatement.setString(3, offerApplicationAccount.getEdc());
                        offerApplicationAccountPreparedStatement.setString(4, offerApplicationAccount.getServiceState());
                        offerApplicationAccountPreparedStatement.setString(5, offerApplicationAccount.getServiceZip());
                        offerApplicationAccountPreparedStatement.setString(6, offerApplicationAccount.getCustomerNo());
                        offerApplicationAccountPreparedStatement.setString(7, offerApplicationAccount.getServiceAddress());
                        offerApplicationAccountPreparedStatement.setString(8, dataItem.getOfferApplicationId());
                        offerApplicationAccountPreparedStatement.setString(9, offerApplicationAccount.getResidentialOnly());

                        preparedStatements.add(offerApplicationPreparedStatement);
                        preparedStatements.add(offerApplicationAccountPreparedStatement);
                    } catch (Exception e) {
                        preparedStatementSuccess.add(false);
                        log.error("Error processing prepared statements", e);
                    }
                });
            } else {
                preparedStatementSuccess.add(false);
            }

            preparedStatements.forEach(item -> {
                try {
                    item.executeUpdate();
                } catch (Exception e) {
                    preparedStatementSuccess.add(false);
                }
            });

            if (!preparedStatementSuccess.contains(false)) {
                conn.commit();
                conn.setAutoCommit(true);
            }

        } catch (Exception e) {
            preparedStatementSuccess.add(false);
            if(connectionReady) {
                log.error(errorMessage, e);
            } else {
                log.error("Connection object from the JDBC pool couldn't be retrieved, this is likely related to using a wrong Datasource name or invalid credentials in the JDBC Connection Pool service");
            }
        } finally {
            preparedStatements.forEach(item -> {
                try {
                    item.close();
                } catch (SQLException e) {
                    preparedStatementSuccess.add(false);
                    log.error("An error occurred while closing the prepared statements", e);
                }
            });
        }

        success = !preparedStatementSuccess.contains(false);

        return success;
    }

    public List<String> getEDCs(String zip) {
        List<String> edcs = new ArrayList<String>();
        if(!Strings.isNullOrEmpty(zip) && UtilsDatabase.isValidZipCode(zip)) {

            List<String> parameters = new ArrayList<String>();
            parameters.add(zip);

            List<String> columns = new ArrayList<String>();
            columns.add(UTILITY_COLUMN);

            List<Map<String,String>> result = this.executeSelectStatement(QUERY_GET_LIST_OF_EDC, parameters, columns, String.format("An error occurred while querying the database to get the list of EDCs for the zipcode: %s", zip));

            for (Map setOfResult: result){
                edcs.add(setOfResult.get(UTILITY_COLUMN).toString());
            }

        } else {
            if(Strings.isNullOrEmpty(zip)) {
                log.error("The zipcode provided is null or empty");
            } else {
                log.error(String.format("The zipcode provided '%s' is not valid", zip));
            }
        }

        return edcs;
    }

    public String getPriorityCode(String id) {
        String priorityCode = "";
        if(!Strings.isNullOrEmpty(id) && UtilsDatabase.isValidProductId(id)) {

            List<String> parameters = new ArrayList<String>();
            parameters.add(id);

            List<String> columns = new ArrayList<String>();
            columns.add(PRIORITY_CODE_COLUMN);

            List<Map<String,String>> result = this.executeSelectStatement(QUERY_GET_PRIORITY_CODE, parameters, columns, String.format("An error occurred while querying the database to get the Priority Code of the Product with the id: %s", id));
            if(!result.isEmpty() && result.get(0) != null) {
                priorityCode = result.get(0).get(PRIORITY_CODE_COLUMN);
            }

        } else {
            if(Strings.isNullOrEmpty(id)) {
                log.error("The id provided is null or empty");
            } else {
                log.error(String.format("The id provided '%s' is not valid", id));
            }
        }

        return priorityCode;
    }

    public boolean insertEnrollmentForm(ArrayList<EnrollmentData> enrollmentDataList) {
        boolean success = false;
        ArrayList<String> productIds = new ArrayList<>();
        ArrayList<EnrollmentData> computedEnrollmentData = new ArrayList<>();

        if (enrollmentDataList.size() > 0) {
            enrollmentDataList.forEach(item -> {
                String offerApplicationId = getOfferID();
                String offerApplicationAccountId =  getOfferID();

                if(item.getOfferApplication() != null && item.getOfferApplicationAccount() != null && offerApplicationId != null && offerApplicationAccountId != null) {
                    productIds.add(item.getOfferApplication().getProduct_ID());
                    computedEnrollmentData.add(new EnrollmentData(item.getOfferApplication(), item.getOfferApplicationAccount(), offerApplicationId, offerApplicationAccountId));
                } else {
                    log.error("OfferApplication or OfferApplicationAccount is null. Or offerApplicationID or offerApplicationAccountID is empty");
                }
            });

            success = insertOfferApplicationAndOfferApplicationAccount(computedEnrollmentData, String.format("An error occurred while inserting an OFFER APPLICATION and OFFER APPLICATION ACCOUNT with Product ID: %s", productIds.toString()));
        }




        return success;
    }

    public Map<String,String> getProductValues(String id) {
        List<Map<String,String>> list = new ArrayList<>();

        if(!Strings.isNullOrEmpty(id) && UtilsDatabase.isValidProductId(id)) {

            List<String> parameters = new ArrayList<String>();
            parameters.add(id);

            List<String> columns = new ArrayList<String>();
            columns.add(PRIORITY_CODE_COLUMN);
            columns.add(QUOTED_PRICE_AMT);
            columns.add(PRICE_TYPE);
            columns.add(FILENET_CONTRACT_NO);
            columns.add(CLASS_TY_CD);
            columns.add(PRICE_RATE_CD);
            columns.add(PRODUCT_CRM_ID);

            list = this.executeSelectStatement(QUERY_GET_PRODUCT, parameters, columns, String.format("An error occurred while querying the database to consult the Product table with the ID: %s.", id));

        } else {
            log.error(String.format("The Product ID: %s is empty or not valid", id));
        }

        return list.isEmpty() ? new HashMap<>() : list.get(0);
    }

    public boolean validateEDC(String zip, String edc) {
        boolean isValid = false;

        if(!Strings.isNullOrEmpty(zip) && !Strings.isNullOrEmpty(edc) && UtilsDatabase.isValidZipCode(zip) && UtilsDatabase.isValidEDCCode(edc)) {

            List<String> parameters = new ArrayList<String>();
            parameters.add(zip);
            parameters.add(edc);

            List<String> columns = new ArrayList<String>();
            columns.add(UTILITY_COLUMN);

            List<Map<String, String>> result = this.executeSelectStatement(QUERY_VALIDATE_EDC, parameters, columns, String.format("An error occurred while querying the database to validate if the EDC: %s with the ZIP: %s are correct.", edc, zip));
            if(!result.isEmpty() && result.get(0) != null && result.get(0).get(UTILITY_COLUMN).equals(edc)) {
                isValid = true;
            }

        } else {
            log.error(String.format("The ZIP: %s and EDC: %s provided are not valid", zip, edc));
        }

        return isValid;
    }
}


