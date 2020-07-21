package com.energyharbor.akron.core.search.services;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.energyharbor.akron.core.search.models.PageIndex;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(immediate = true)
public class PageIndexServiceImpl implements PageIndexService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final static String MAPPING_FILE = "/index/component_index_mapping.json";
    private final static Type MAPPING_TYPE = new TypeToken<Map<String, List<String>>>(){}.getType();
    private Map<String, List<String>> mapping;

    @Activate
    public void activate() {
        log.info(String.format("%s starting", getClass()));
        mapping = loadIndexMapping();
    }

    public PageIndex getPage(Page page) {
        return new PageIndex(page, mapping);
    }

    public PageIndex getPage(String path, ResourceResolver resolver) {
        PageManager pageManager = resolver.adaptTo(PageManager.class);
        Page page = pageManager.getPage(path);
        return getPage(page);
    }

    private Map<String, List<String>> loadIndexMapping() {
        InputStream inputStream = getClass().getResourceAsStream(MAPPING_FILE);
        JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
        Map<String, List<String>> mapping = new Gson().fromJson(reader, MAPPING_TYPE);
        if (mapping == null) {
            log.warn(String.format("Could not generate mapping for %s", getClass()));
            return new HashMap<>();
        } else {
            return mapping;
        }
    }
}
