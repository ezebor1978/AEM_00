package com.energyharbor.akron.core.services;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.gson.*;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The objective of this class is to extract all the properties input in a Dialog.
 * This will get the the authored properties and return them in a JSON format, to
 * be called in the HTML of each component to have them available in the JSX file
 * as react props.
 */
@Model(adaptables=Resource.class)
public class DialogDataParser {

    private final Resource resource;
    private String parentHomePage;
    private static final String JCR_FILTER = "jcr:";
    private static final String CQ_FILTER = "cq:";
    private static final String SLING_FILTER = "sling:";
    private static final String IS_HOME = "is_home";
    private static final String PATH_REGEX = "^/content(/[^/ ]*)+/?$";

    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final String MULTI_FIELD_FILTER = "multifield";

    public DialogDataParser(Resource resource) {
        this.resource = resource;
    }

    /**
     * This method will get the values of a {@link Resource} and also uses
     * getResourceValuesAndChildren to verify if the resource input has any
     * children and get them
     * @param resourceInput the resource to its properties
     * @param includeMetaData flag to include MetaData in results or not
     * @return Map<String, Object>
     */
    private Map<String, Object> getResourceValuesAndChildren(Resource resourceInput, boolean includeMetaData) {
        ResourceResolver resolver = resource.getResourceResolver();
        Map<String, Object> children = iterateInResourceChildren(resourceInput, includeMetaData);

        // Obtain the Resource base properties
        Stream<Map.Entry<String, Object>> stream = resourceInput
            .getValueMap().entrySet().stream()
            .map(entry -> {
                Object value = entry.getValue();
                // Outgoing URLs should respect mappings
                if (value instanceof String && ((String)value).matches(PATH_REGEX)) {
                    entry.setValue(resolver.map((String)value));
                }
                return entry;
            });
        Map<String, Object> properties;
        if(includeMetaData) {
            properties = stream.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } else {
            properties = stream.filter(p -> !p.getKey().startsWith(JCR_FILTER))
                .filter(p -> !p.getKey().startsWith(SLING_FILTER))
                .filter(p -> !p.getKey().startsWith(CQ_FILTER))
                .collect(Collectors.toMap(Map.Entry::getKey, v -> {
                    Object value = v.getValue();
                    if (value instanceof String[]) {
                        return generateJsonArray((String[])value);
                    } else if (value instanceof String){
                        return new JsonPrimitive(value.toString());
                    } else if(isJSONValid(value.toString())){
                        String[] array = {value.toString()};
                        return generateJsonArray(array);
                    } else {
                        return value;
                    }
                }));
        }

        // Merge Resource properties with Children properties
        if(children.size() > 0) {
            properties.putAll(children);
        }

        properties.put("id", getId());

        return properties;
    }


    /**
     * Validates if a input string is a valid JSON in order to define if parse it
     * to a {@link JsonElement} or a {@link JsonPrimitive}
     * @param jsonInString
     * @return boolean if a string is valid JSON or not
     */
    private boolean isJSONValid(String jsonInString) {
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();
        try {
            gson.fromJson(jsonInString, Object.class);
            return true;
        } catch (JsonSyntaxException ex) {
            return false;
        }
    }

    /**
     * When an element in the Resource is an Array, this method will return the
     * {@link JsonElement} as a {@link JsonArray}
     * @param array
     * @return
     */
    private JsonElement generateJsonArray(String[] array){
        JsonParser parser = new JsonParser();
        JsonArray parent = new JsonArray();
        Arrays.stream(array).forEach(e -> {
            if(isJSONValid(e)) {
                parent.add(parser.parse(e));
            }
        });
        return parent;
    }

    /**
     * This method will iterate over the children of a {@link Resource}
     * and find all the multi fields in order to get its values
     * @param resourceInput the resource to validate its children
     * @param includeMetaData flag to include MetaData in results or not
     * @return Map<String, Object>
     */
    private Map<String, Object> iterateInResourceChildren(Resource resourceInput, boolean includeMetaData) {
        Map<String, Object> children = new MultiValueMap();

        resourceInput.getResourceType();
        Iterator<Resource> resourceChildren = resourceInput.listChildren();
        while(resourceChildren.hasNext()) {
            Resource temp = resourceChildren.next();
            if(temp.getName().startsWith(MULTI_FIELD_FILTER)) {
                Iterator<Resource> tempChildren = temp.listChildren();
                while(tempChildren.hasNext()) {
                    Resource multifieldChild = tempChildren.next();
                    children.put(temp.getName(), getResourceValuesAndChildren(multifieldChild, includeMetaData));
                }
            }
        }

        return children;
    }

    /**
     * Iterate through the nodes to find the parent home page for the current page
     * @param resolver
     * @return the path to the parent home page. null if not found
     */
    private String getParentHomePagePath(ResourceResolver resolver) {
        try {
            Page currentPage = resource.getResourceResolver().getResource(resolver.adaptTo(PageManager.class)
                .getContainingPage(resource).getPath()).adaptTo(Page.class);
            do{
                if(currentPage.getProperties().containsKey(IS_HOME)) {
                    return currentPage.getPath();
                } else {
                    currentPage = currentPage.getParent();
                }
            } while(currentPage != null);
        } catch (NullPointerException e) {
            // pass
        }
        return null;
    }


    /**
     * This method will return only the properties configured by the author in the dialog for the provided resource
     * @param resource - the resource that will be used to get the properties
     * @return {@link String}
     */
    private String getResourceData(Resource resource) {
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();
        Map<String, Object> data = getResourceValuesAndChildren(resource, false);
        return gson.toJson(data);
    }

    /**
     * This method will return only the properties configured by the author in the dialog
     * @return {@link String}
     */
    public String getData() {
        return getResourceData(this.resource);
    }

    /**
     * This method will return the properties as map
     * @return {@link Map}
     */
    public Map<String, Object> getDataMap(){
        return getResourceValuesAndChildren(resource, false);
    }

    /**
     * This method will return a unique ID of the current resource
     * based on its JCR identifier
     * @return a unique ID
     */
    public String getId() {
        try {
            return String.format(
                "id-%s",
                resource.adaptTo(Node.class)
                    .getIdentifier()
                    .replaceAll("/", "-")
                    .replaceAll(":", "-"));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * This method will return the properties configured by the author in the dialog and
     * also include metadata from JCR and SLING
     * @return {@link String}
     */
    public String getMetaData() {
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();
        Map<String, Object> metaData = getResourceValuesAndChildren(resource, true);
        return gson.toJson(metaData);
    }

    /**
     * Generates a Json representation of the properties configured in the current resource
     * @return String
     */
    public String getPageProperties() {
        PageManager pageManager= resource.getResourceResolver().adaptTo(PageManager.class);
        Resource page = resource.getResourceResolver().getResource(pageManager.getContainingPage(resource).getPath() + "/jcr:content");
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();
        Map<String, Object> metaData = getResourceValuesAndChildren(page, true);
        return gson.toJson(metaData);
    }

    /**
     * Looks for a parent page of time Home Page and returns the values configured for the header in the parent page
     * @return
     */
    public String getInheritedComponentData() {
        Integer componentNodeIndex = resource.getPath().indexOf("/jcr:content");
        String componentPath = resource.getPath().substring(componentNodeIndex);
        ResourceResolver resolver = resource.getResourceResolver();

        if(getParentHomePage()!= null) {
            return getResourceData(resolver.getResource(parentHomePage + componentPath));
        }
        log.warn(String.format("Current page doesn't have a home page ancestor. Page path: %s", resource.getPath()));
        return null;
    }

    /**
     * Set and return the parentHomePage for this.resource
     * @return path to parent home page
     */
    public String getParentHomePage() {
        if(parentHomePage != null) {
            return parentHomePage;
        }
        parentHomePage = getParentHomePagePath(resource.getResourceResolver());
        return parentHomePage;
    }
}
