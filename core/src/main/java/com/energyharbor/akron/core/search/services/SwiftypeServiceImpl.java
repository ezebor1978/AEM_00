package com.energyharbor.akron.core.search.services;

import com.energyharbor.akron.core.search.models.Query;
import com.swiftype.api.easy.Engine;
import com.swiftype.api.easy.EnginesApi;
import com.swiftype.api.easy.helper.SearchOptions;
import com.swiftype.api.easy.helper.SearchResult;
import com.swiftype.api.easy.helper.SwiftypeConfig;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * A service to communicate with Swiftype using the Swiftype Java SDK. All inner
 * implementation should rely on APIs provided by that SDK whenever possible.
 *
 * Docs: https://github.com/swiftype/swiftype-java
 */
@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = SwiftypeServiceImpl.SwiftypeServiceConfiguration.class)
public class SwiftypeServiceImpl implements SwiftypeService {
    @ObjectClassDefinition(name="Swiftype Search Service",  description = "Service that handles the search functionality of the site")
    @interface SwiftypeServiceConfiguration {
        @AttributeDefinition(name = "API Key", description = "Swiftype API Key", type = AttributeType.PASSWORD)
        String api_key() default "";

        @AttributeDefinition(name = "Engine Slug", description = "Swiftype Engine Slug")
        String engine_slug() default "";
    }

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final SwiftypeConfig swiftypeConfig = SwiftypeConfig.INSTANCE;
    private Engine engine;

    @Activate
    protected void activate(final SwiftypeServiceConfiguration configuration) {
        log.info(String.format("%s starting.", this.getClass()));
        swiftypeConfig.setApiKey(configuration.api_key());
        engine = new EnginesApi().get(configuration.engine_slug());

        Query query = new Query("example");

        log.info(String.format("Found %d results on boot.", search(query).size()));
    }

    public Map<String, SearchResult> search(final Query query) {
        final SearchOptions.Builder optionsBuilder = new SearchOptions.Builder();
        query.getFilters().entrySet()
            .forEach(e -> optionsBuilder.filter("page", e.getKey(), e.getValue()));

        optionsBuilder.page(query.getPage());
        optionsBuilder.perPage(Query.REGULAR_SEARCH_ELEMENTS);
        return search(query, optionsBuilder.build());
    }

    private Map<String, SearchResult> search(final Query query, final SearchOptions options) {
        return engine.search(query.getTerm(), options);
    }
}
