'use strict';

const Query = Packages.com.energyharbor.akron.core.search.models.Query;
const SwiftypeServiceInterface = Packages.com.energyharbor.akron.core.search.services.SwiftypeService;

use([], function() {
    // Services
    const gson = new com.google.gson.GsonBuilder().disableHtmlEscaping().create();
    const swiftypeService = sling.getService(SwiftypeServiceInterface);

    // Request and Page Properties
    const searchTerm = request.getParameter('q') ? '' + request.getParameter('q') : null;

    let searchResults = [];

    if (searchTerm != null) {
        searchResults = runQuery(searchTerm);
    }

    function runQuery(searchTerm) {
        let resultList;
        let parsedResults = [];
        let query = new Query(searchTerm);
        query.setPage(1);

        try {
            let searchResults = swiftypeService.search(query);
            resultList = searchResults.get('page').results;
        } catch (e) {
            resultList = null;
        }


        if (resultList != null) {
            for (let i = 0; i < resultList.size(); i++) {
                let currentResult = resultList.get(i).fields;
                let excerpt = currentResult.get('body')
                    ? currentResult.get('body')
                    : '';
                let description = currentResult.get('description')
                    ? currentResult.get('description')
                    : null;


                parsedResults.push({
                    title: currentResult.get('title') ? currentResult.get('title') : '',
                    description: description ? description : excerpt,
                    url: currentResult.get('url') ? currentResult.get('url') : ''
                });
            }
        }

        return parsedResults;
    }



    return {
        data: JSON.stringify({
            currentSearchTerm: searchTerm,
            searchResults: JSON.parse(gson.toJson(searchResults))
        })
    };
});
