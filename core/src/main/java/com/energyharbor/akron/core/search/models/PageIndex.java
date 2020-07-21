package com.energyharbor.akron.core.search.models;

import com.day.cq.wcm.api.Page;
import com.google.gson.annotations.SerializedName;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import java.util.List;
import java.util.Map;

public class PageIndex {

    private final String title;

    @SerializedName("navigation_title")
    private final String navigationTitle;

    private final String name;

    private final String description;

    @SerializedName("resource_type")
    private final String resourceType;

    private final String path;

    @SerializedName("content")
    private final String content;

    private final transient Map<String, List<String>> indexMapping;
    private final transient Resource contentResource;
    private static final transient String PROPERTY_RESOURCE_TYPE = "sling:resourceType";

    public PageIndex(Page page) {
        this(page, null);
    }

    public PageIndex(Page page, Map<String, List<String>> mapping) {
        indexMapping = mapping;

        description = page.getDescription();
        title = page.getTitle();
        navigationTitle = page.getNavigationTitle();
        contentResource = page.getContentResource();
        resourceType = page.getContentResource().getResourceType();
        path = page.getPath();
        name = page.getName();

        content = this.stringifyDescendants("", contentResource, "");
    }

    /**
     * Recursively stringifies the contents of a cq:Page object
     * by iterating over its children and recursively iterating over
     * each node that also has children.
     *
     * @param previous  The aggregated string so far. Initially empty.
     * @param resource  The current Resource. Properties from this resource
     *                  will be aggregated, and then this method will be called
     *                  on all children of this Resource.
     * @return          A String containing all the indexable content of resource
     *                  and its children.
     */
    private String stringifyDescendants(String previous, Resource resource, String resourceType) {
        for (Resource child : resource.getChildren()) {
            ValueMap vm = child.getValueMap();
            if (vm.containsKey(PROPERTY_RESOURCE_TYPE) || !resourceType.isEmpty()) {
                if (vm.containsKey(PROPERTY_RESOURCE_TYPE)) {
                    resourceType = vm.get(PROPERTY_RESOURCE_TYPE).toString();
                }
                if (indexMapping.containsKey(resourceType)) {
                    for (String key : vm.keySet()) {
                        if (this.shouldStringify(resourceType, key)) {
                            previous = previous + " " + cleanString(vm.get(key).toString());
                        }
                    }
                }
            }
            if (child.hasChildren()) {
                previous = stringifyDescendants(previous, child, resourceType);
            }
        }
        return previous;
    }

    /**
     * Cleans an input String by removing characters that should
     * not be sent to the indexer.
     *
     * @param input  A string to clean
     * @return       The input string without HTML tags, entities,
     *               or newline characters
     */
    private String cleanString(String input) {
        try {
            return input
                // Remove HTML tags
                .replaceAll("<[^>]*>","")

                // Remove HTML entities
                .replaceAll("&[^\\s]*;", "")

                // Remove newline characters
                .replaceAll("[\\n\\r]"," ");
        } catch (NullPointerException e) {
            return "";
        }
    }

    /**
     * Determines if a specific key should be stringified
     * based on the specified resourceType. These definitions
     * are originally retrieved from page_index_mapping.json
     * of the format:
     *
     * {
     *     "path/of/resource/type": [
     *          "property-to-include",
     *          "another-property"
     *     ]
     * }
     *
     * @param resourceType  The resourceType of the current component
     * @param key           The property name on that component
     * @return              true if the value of this property should be
     *                      included in the index. Else false.
     */
    private boolean shouldStringify(String resourceType, String key) {
        return indexMapping.get(resourceType).contains(key);
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getNavigationTitle() {
        return navigationTitle;
    }

    public String getContent() {
        return content;
    }
}


