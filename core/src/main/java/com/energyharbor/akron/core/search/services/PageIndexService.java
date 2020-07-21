package com.energyharbor.akron.core.search.services;

import com.day.cq.wcm.api.Page;
import com.energyharbor.akron.core.search.models.PageIndex;
import org.apache.sling.api.resource.ResourceResolver;

public interface PageIndexService {
    /**
     * Converts a single {@link Page} to an indexable {@link PageIndex}.
     *
     * @param page  The page to index
     * @return      A {@link PageIndex} that contains indexable data
     *              about the input page
     */
    PageIndex getPage(Page page);

    /**
     * Converts a path to a {@link Page}, then converts that page to
     * an indexable {@link PageIndex}.
     *
     * @param path      The path to the page to index
     * @param resolver  Resolver with permission to /content
     * @return      A {@link PageIndex} that contains indexable data
     *              about the input page
     */
    PageIndex getPage(String path, ResourceResolver resolver);
}
