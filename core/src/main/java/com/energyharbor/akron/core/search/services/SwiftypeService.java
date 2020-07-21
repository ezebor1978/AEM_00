package com.energyharbor.akron.core.search.services;

import com.energyharbor.akron.core.search.models.Query;
import com.swiftype.api.easy.helper.SearchResult;

import java.util.Map;

public interface SwiftypeService {
    Map<String, SearchResult> search(final Query query);
}
