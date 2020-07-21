'use strict';

const WCMUtils = Packages.com.day.cq.wcm.commons.WCMUtils;
const ExternalizerService = Packages.com.day.cq.commons.Externalizer;
const PageIndexService = Packages.com.energyharbor.akron.core.search.services.PageIndexService;

use([], function() {

    const data = granite.resource.properties;
    const EXCLUDE_FROM_SEARCH_PROP = 'excludeFromSearch';
    const pageIndexService = sling.getService(PageIndexService);
    const externalizerService = sling.getService(ExternalizerService);
    const currentLanguage = currentPage.getLanguage().getLanguage();

    const ignored = currentPage.getProperties().containsKey(EXCLUDE_FROM_SEARCH_PROP);
    const pageIndex = pageIndexService.getPage(currentPage.getPath(), resolver);
    const canonicalUrl = externalizerService.externalLink(
        resolver,
        ExternalizerService.PUBLISH,
        currentPage.getPath()
    );

    return {
        favIcon: '/etc.clientlibs/settings/wcm/designs/akron/clientlib-all/resources/images/favicon.ico',
        ignored: ignored,
        canonicalUrl: canonicalUrl,
        keywords: WCMUtils.getKeywords(currentPage, false),
        swiftype: {
            lang: currentLanguage,
            pageDescription: data['jcr:description'],
            pageIndex: pageIndex
        }
    };
});
