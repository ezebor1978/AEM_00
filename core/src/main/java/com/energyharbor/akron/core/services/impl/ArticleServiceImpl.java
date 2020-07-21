package com.energyharbor.akron.core.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.energyharbor.akron.core.models.Article;
import com.energyharbor.akron.core.services.ArticleService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import java.util.*;

@Component(immediate = true)
public class ArticleServiceImpl implements ArticleService {

    @Reference
    private QueryBuilder queryBuilder;

    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    private final static String TYPE = "cq:Page";
    private final static String PUBLISHED_DATE_LOCATION = "jcr:content/publishedDate";
    private final static String RESOURCE_TYPE_LOCATION = "jcr:content/sling:resourceType";
    private final static String RESOURCE_TYPE_VALUE = "akron/components/structure/article";

    /**
     * Generates the predicate for the query that will be executed to get the articles list
     *
     * @param paths Path of the JCR to be used
     * @return Map<String, String>
     */
    private Map<String, String> generateQuery(List<String> paths) {
        Map<String, String> predicateMap = new HashMap<>();
        predicateMap.put("type", TYPE);
        predicateMap.put("group.p.or", "true");

        for (int i = 0; i<paths.size(); i++) {
            predicateMap.put("group." + i + "_path", paths.get(i));
        }

        predicateMap.put("property", RESOURCE_TYPE_LOCATION);
        predicateMap.put("property.value", RESOURCE_TYPE_VALUE);

        predicateMap.put("orderby", "@" + PUBLISHED_DATE_LOCATION);
        predicateMap.put("orderby.sort", "desc");
        predicateMap.put("p.limit", "-1");

        return predicateMap;
    }

    /**
     * Helper method to run queries
     *
     * @param resolver  ResourceResolver with permissions to read content
     * @param predicate Map containing the predicate of the query
     * @return Iterator with the results
     */
    private Iterator<Node> runQuery(ResourceResolver resolver, Map<String, String> predicate) {
        Session session = resolver.adaptTo(Session.class);
        Query query = queryBuilder.createQuery(PredicateGroup.create(predicate), session);
        SearchResult result = query.getResult();
        return result.getNodes();
    }

    public Article getArticle(ResourceResolver resolver, String path, Locale locale) {
        if (path != null) {
            Resource resource = resolver.getResource(path + "/" + JcrConstants.JCR_CONTENT);
            if (resource != null) {
                Article article = resource.adaptTo(Article.class);
                article.init(resolver);
                return article;
            }
        }

        return null;
    }

    public List<Article> getArticles(ResourceResolver resolver, Locale locale, List<String> paths) {
        List<Article> articles = new ArrayList<>();
        try {
            Map<String, String> articlesQuery = generateQuery(paths);
            Iterator<Node> nodes = runQuery(resolver, articlesQuery);
            while (nodes.hasNext()) {
                Node node = nodes.next();
                Article article = resolver.resolve(node.getPath() + "/" + JcrConstants.JCR_CONTENT).adaptTo(Article.class);
                article.init(resolver);
                articles.add(article);
            }
        } catch (Exception e) {
            log.error("Could not get all articles", e);
        }
        return articles;
    }
}


