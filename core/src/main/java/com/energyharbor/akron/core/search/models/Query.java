package com.energyharbor.akron.core.search.models;

import java.util.HashMap;
import java.util.Map;

public class Query {

    public static String LANGUAGE = "language";
    public static String ENGLISH = "en";

    /**
     * Swiftype: 100 is the max number for the PER PAGE attribute
     * Docs: https://swiftype.com/documentation/site-search/searching/pagination
     */
    public static int REGULAR_SEARCH_ELEMENTS = 100;

    private String term;
    private Map<String, String[]> filters;
    private int page;

    public Query(String term) {
        this.term = term;
        this.filters = new HashMap<>();
    }

    public Query(String term, Map<String, String[]> filters) {
        this(term);
        this.filters.putAll(filters);
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public Map<String, String[]> getFilters() {
        return new HashMap<>(filters);
    }

    public void setFilters(Map<String, String[]> filters) {
        this.filters = filters;
    }

    public void addFilter(String key, String[] value) {
        this.filters.put(key, value);
    }

    public void addFilter(String key, String value) {
        String[] arrValue = new String[1];
        arrValue[0] = value;
        this.filters.put(key, arrValue);
    }

    public void removeFilter(String key) {
        this.filters.remove(key);
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
