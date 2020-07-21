package com.energyharbor.akron.core.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Sling Model for the Article
 */
@Model(adaptables = Resource.class)
public class Article {

    @Self
    private transient Resource resource;

    @Inject
    @Optional
    @Named("jcr:title")
    private String title;

    @Inject
    @Optional
    private String authorImage;

    @Inject
    @Optional
    private String authorImageAltText;

    @Inject
    @Optional
    private String authorName;

    @Inject
    @Optional
    private String publishedDate;

    @Inject
    @Optional
    private String articleImage;

    @Inject
    @Optional
    private String articleImageAltText;

    private String articlePath;

    public void init(ResourceResolver resolver) {
        try {
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH);
            LocalDate date = LocalDate.parse(publishedDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            publishedDate = outputFormatter.format(date);
        } catch(Exception e) {
            publishedDate = "";
        }
        this.articlePath = resolver.map(resource.getPath().split("/jcr:content")[0]);
    }

    public String getPath() { return resource.getPath(); }

    public String getArticlePath() { return articlePath; }

    public String getTitle() { return title; }

    public String getAuthorImage() { return authorImage; }

    public String getAuthorImageAltText() { return authorImageAltText; }

    public String getAuthorName() { return authorName; }

    public String getPublishedDate() { return publishedDate; }

    public String getArticleImage() { return articleImage; }

    public String getArticleImageAltText() { return articleImageAltText; }

    public String toJSON() {
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();
        return gson.toJson(this);
    }
}


