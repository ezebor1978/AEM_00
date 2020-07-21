'use strict';

const ArticleService = Packages.com.energyharbor.akron.core.services.ArticleService;

use([], function() {
    let article;
    let parsedArticle;
    const path = currentPage.getPath();
    const articleService = sling.getService(ArticleService);

    if (path != null) {
        try {
            article = articleService.getArticle(resolver, path, currentPage.getLanguage());
        } catch (e) {
            log.error(e);
        }
    }

    if (article) {
        parsedArticle = article.toJSON();
    }

    return {
        article: parsedArticle,
    };
});
