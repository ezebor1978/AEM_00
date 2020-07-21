package com.energyharbor.akron.core.servlets;

import com.energyharbor.akron.core.models.APIError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;

import javax.mail.util.ByteArrayDataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilsServlet {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^.+@.+(\\.[a-z]{2,})$", Pattern.CASE_INSENSITIVE);
    static String CAPTCHA_RESPONSE = "g-recaptcha-response";

    static void handleAPIException(Exception e, Gson gson, SlingHttpServletResponse response) throws IOException {
        if (e != null && e.getMessage() != null) {
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            gson.toJson(new APIError(HttpStatus.SC_BAD_REQUEST, e.getMessage()), response.getWriter());
        } else {
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            gson.toJson(new APIError(HttpStatus.SC_INTERNAL_SERVER_ERROR), response.getWriter());
        }
    }

    /**
     * Creates a JSONObject with the request
     * @param req SlingHttpServletRequest
     * @return JsonObject
     * @throws IOException
     */
    static JsonObject getBody(SlingHttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader;
        reader = req.getReader();

        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append('\n');
        }

        return new Gson().fromJson(sb.toString(), JsonObject.class);
    }

    /**
     * Find the JSON request parameter and create a JSONObject with it
     * @param params Map<String, RequestParameter[]>
     * @return JsonObject
     */
    static JsonObject getRequestParameterJSON(Map< String, RequestParameter[] > params) throws IOException{
        JsonObject data = null;
        Gson gson = new Gson();

        for (Map.Entry<String, RequestParameter[]> pairs : params.entrySet()) {

            RequestParameter[] valArray = pairs.getValue();
            RequestParameter param0 = valArray[0];

            boolean isFormField = param0.isFormField();
            if (isFormField) {
                String jsonData = param0.getString();
                JsonReader reader = new JsonReader(new StringReader(jsonData));
                reader.setLenient(true);
                data = gson.fromJson(reader, JsonObject.class);
            }
        }

        return data;
    }

    /**
     * Find the File in the request parameter and create a ByteArrayDataSource with it
     * @param params Map<String, RequestParameter[]>
     * @return ByteArrayDataSource
     * @throws IOException
     */
    static ByteArrayDataSource getRequestParameterAttachment(Map< String, RequestParameter[] > params) throws IOException {
        ByteArrayDataSource byteArrFile = null;
        InputStream fileStream = null;
        String fileMimeType = "";

        for (Map.Entry<String, RequestParameter[]> pairs : params.entrySet()) {

            RequestParameter[] valArray = pairs.getValue();
            RequestParameter param0 = valArray[0];

            boolean isFormField = param0.isFormField();
            if (!isFormField) {
                fileStream = param0.getInputStream();
                fileMimeType = param0.getContentType();
                byteArrFile = new ByteArrayDataSource(fileStream, fileMimeType);
                byteArrFile.setName(param0.getFileName());
            }
        }

        return byteArrFile;
    }

    /**
     * Checks against the regex the email
     *
     * @param email String
     * @return boolean
     */
    static boolean isValidEmailAddress(String email) {
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }
}
