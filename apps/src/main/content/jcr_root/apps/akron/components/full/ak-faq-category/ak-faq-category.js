'use strict';

use([], function() {
    let pivot;
    let mappedUrl;

    const faqLinks = [];
    const faqCategories = [];
    const categoryTitle = currentPage.getTitle();
    const faqLinksIterator = currentPage.listChildren();
    const currentFaqCategoryLink = currentPage.getPath();
    const categoriesIterator = currentPage.getParent().listChildren();

    while (categoriesIterator.hasNext()) {
        pivot = categoriesIterator.next();
        mappedUrl = resolver.map(pivot.getPath());

        faqCategories.push({
            title: String(pivot.getTitle()),
            path: mappedUrl ? String(mappedUrl) : '',
            active: pivot.getPath().equals(currentFaqCategoryLink)
        });
    }

    while (faqLinksIterator.hasNext()) {
        pivot = faqLinksIterator.next();
        mappedUrl = resolver.map(pivot.getPath());

        faqLinks.push({
            title: String(pivot.getTitle()),
            path: mappedUrl ? String(mappedUrl) : ''
        });
    }

    return {
        data: JSON.stringify({
            faqLinks: faqLinks,
            faqCategories: faqCategories,
            title: String(categoryTitle)
        })
    };
});
