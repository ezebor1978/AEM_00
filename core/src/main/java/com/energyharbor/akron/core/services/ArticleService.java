package com.energyharbor.akron.core.services;

import com.energyharbor.akron.core.models.Article;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;
import java.util.Locale;

public interface ArticleService {

    /**
     * Get an Article based on a specified path
     * @param resolver a ResourceResolver with permissions to path
     * @param path the path of the article
     * @return an article (or null)
     */
    Article getArticle(ResourceResolver resolver, String path, Locale locale);

    /**
     * Get an List of Articles under a specified set of paths
     * @param resolver a ResourceResolver with permissions to path
     * @param paths the paths of the articles
     * @return  an article list (or null)
     */
    List<Article> getArticles(ResourceResolver resolver, Locale locale, List<String> paths);
}
