'use strict';

const ArticleService = Packages.com.energyharbor.akron.core.services.ArticleService;

use([], function () {
    let size = 0;
    let articles = [];
    let hasPaths = false;

    const gson = new com.google.gson.Gson();
    const articleService = sling.getService(ArticleService);
    const locale = currentPage.getLanguage();

    if (currentNode.hasNode('searchPaths')) {
        let currentPath;
        hasPaths = true;
        const pathList = [];
        const pathNodes = resolver.getResource(currentNode.getNode('searchPaths').getPath());

        if (pathNodes) {
            const pathIterator = resolver.listChildren(pathNodes);
            while (pathIterator.hasNext()) {
                currentPath = pathIterator.next().adaptTo(Packages.javax.jcr.Node);
                if (currentPath.hasProperty('path')) {
                    pathList.push(currentPath.getProperty('path').getString());
                }
            }
        }

        articles = articleService.getArticles(resolver, locale, pathList);
        size = articles.size();
    }

    return {
        valid: hasPaths && size > 0,
        articleList: JSON.stringify({
            articles: JSON.parse(gson.toJson(articles)),
        })
    };
});
