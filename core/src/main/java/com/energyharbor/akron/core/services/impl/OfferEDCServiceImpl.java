package com.energyharbor.akron.core.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.energyharbor.akron.core.enums.OfferType;
import com.energyharbor.akron.core.models.Community;
import com.energyharbor.akron.core.models.EDCDetails;
import com.energyharbor.akron.core.models.Offer;
import com.energyharbor.akron.core.services.OfferEDCService;
import com.google.common.base.Strings;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import java.util.*;

@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = OfferEDCServiceImpl.OfferEDCServiceConfig.class)
public class OfferEDCServiceImpl implements OfferEDCService {

    @ObjectClassDefinition(name = "Offers and EDC Service Configuration", description = "Configuration of properties for the Offers and EDC service")
    @interface OfferEDCServiceConfig {
        @AttributeDefinition(
            name = "EDC Details page",
            description = "Enter the page where the EDC Details will be edited",
            type = AttributeType.STRING)
        String edcDetailsUrl();

        @AttributeDefinition(
            name = "Mass Market Offers page",
            description = "Enter the page where the Mass Market Offers will be edited",
            type = AttributeType.STRING)
        String offersUrl();

        @AttributeDefinition(
            name = "Mass Market Gas Offers page",
            description = "Enter the page where the Mass Market Gas Offers will be edited",
            type = AttributeType.STRING)
        String gasOffersUrl();

        @AttributeDefinition(
            name = "Mass Market Bundle Offers page",
            description = "Enter the page where the Mass Market Bundle Offers will be edited",
            type = AttributeType.STRING)
        String bundleOffersUrl();

        @AttributeDefinition(
            name = "Special Offers page",
            description = "Enter the page where the Special Offers will be edited",
            type = AttributeType.STRING)
        String specialOfferUrl();

        @AttributeDefinition(
            name = "Community Programs Page",
            description = "Enter the page where the Community Programs Page will be edited.",
            type = AttributeType.STRING)
        String communityUrl();
    }

    private String offersUrl;
    private String gasOffersUrl;
    private String bundleOffersUrl;
    private String edcDetailsUrl;
    private String specialOffersUrl;
    private String communityProgramsUrl;

    private final String bundleEnrollmentUrl = "/content/akron/en/enrollment-bundle";
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    private final static String UNS_TYPE = "nt:unstructured";
    private final static String PAGE_CONTENT_TYPE = "cq:PageContent";
    private final static String RESOURCE_TYPE = "sling:resourceType";

    private final static String OFFER_EDC_CODE_PROP = "edcCode";
    private final static String OFFER_PRODUCT_ID_PROP = "productId";
    private final static String BUNDLE_OFFER_BUNDLE_ID_PROP = "bundleId";
    private final static String OFFER_BUNDLE_ONLY_PROP = "bundleOnly";
    private final static String OFFER_PRECEDENCE_PROP = "precedence";
    private final static String OFFER_RESOURCE_TYPE_VALUE = "akron/components/data/ak-offer";
    private final static String GAS_OFFER_RESOURCE_TYPE_VALUE = "akron/components/data/ak-gas-offer";
    private final static String BUNDLE_OFFER_RESOURCE_TYPE_VALUE = "akron/components/data/ak-bundle-offer";

    private final static String COMMUNITY_RESOURCE_TYPE = "akron/components/structure/community";
    private final static String COMMUNITY_NAME_PROP = "jcr:title";

    private final static String EDC_DETAILS_CODE_PROP = "code";
    private final static String EDC_DETAILS_RESOURCE_TYPE_VALUE = "akron/components/data/ak-edc-details";

    @Reference
    private QueryBuilder queryBuilder;

    @Activate
    protected void activate(final OfferEDCServiceConfig configuration) {
        this.offersUrl = configuration.offersUrl();
        this.gasOffersUrl = configuration.gasOffersUrl();
        this.bundleOffersUrl = configuration.bundleOffersUrl();
        this.edcDetailsUrl = configuration.edcDetailsUrl();
        this.specialOffersUrl = configuration.specialOfferUrl();
        this.communityProgramsUrl = configuration.communityUrl();
    }

    /**
     * Generate a base query with the common property names and values
     *
     * @param type jcr:primaryType of the node
     * @param paths List of the Paths of the JCR to query
     * @param limit limit of results. Use -1 to get all the results.
     * @return Map<String, String>
     */
    private Map<String, String> generateCommonBaseQuery(String type, List<String> paths, String limit) {
        Map<String, String> commonPredicateMap = new HashMap<>();
        commonPredicateMap.put("type", type);
        commonPredicateMap.put("group.p.or", "true");
        for (int i = 1; i<=paths.size(); i++) {
            commonPredicateMap.put("group." + i + "_path", paths.get(i-1));
        }
        commonPredicateMap.put("p.limit", limit);
        return commonPredicateMap;
    }

    /**
     * Generates a base query predicate to retrieve either Offers or EDC Details components.
     *
     * @param type jcr:primaryType of the node
     * @param paths List of the Paths of the JCR to query
     * @param resourceTypeLocation Location of the Resource type in the tree of content
     * @param resourceType Resource type of the node
     * @param limit limit of results. Use -1 to get all the results.
     *
     * @return Map<String, String>
     */
    private Map<String, String> generateBaseQuery(String type, List<String> paths, String resourceTypeLocation, String resourceType, String limit) {
        Map<String, String> predicateMap = generateCommonBaseQuery(type, paths, limit);
        predicateMap.put("1_property", resourceTypeLocation);
        predicateMap.put("1_property.value", resourceType);
        return predicateMap;
    }

    /**
     * Generates a base query predicate to retrieve either Offers or EDC Details components with multiple resourceType options.
     *
     * @param type jcr:primaryType of the node
     * @param paths List of the Paths of the JCR to query
     * @param resourceTypeLocation Location of the Resource type in the tree of content
     * @param resourceType A list of possible resourceTypes for the node
     * @param limit limit of results. Use -1 to get all the results.
     *
     * @return Map<String, String>
     */
    private Map<String, String> generateBaseQuery(String type, List<String> paths, String resourceTypeLocation, List<String> resourceType, String limit) {
        Map<String, String> predicateMap = generateCommonBaseQuery(type, paths, limit);
        predicateMap.put("1_property", resourceTypeLocation);
        for (int i = 1; i<=resourceType.size(); i++) {
            predicateMap.put("1_property." + i + "_value", resourceType.get(i-1));
        }
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

    public List<EDCDetails> getEDCDetails(ResourceResolver resolver, String codes) {
        List<EDCDetails> EDCs = new ArrayList<>();
        try {
            if(!Strings.isNullOrEmpty(codes)) {
                int codeIndex = 1;
                List<String> path = new ArrayList<>();
                path.add(this.edcDetailsUrl);
                Iterator<String> codesIterator = Arrays.stream(codes.split(",")).iterator();
                Map<String, String> baseQueryPredicate = generateBaseQuery(UNS_TYPE, path, RESOURCE_TYPE, EDC_DETAILS_RESOURCE_TYPE_VALUE, "-1");

                baseQueryPredicate.put("2_property", EDC_DETAILS_CODE_PROP);
                while (codesIterator.hasNext()) {
                    baseQueryPredicate.put("2_property." + codeIndex + "_value", codesIterator.next());
                    codeIndex++;
                }

                Iterator<Node> nodes = runQuery(resolver, baseQueryPredicate);
                while (nodes.hasNext()) {
                    EDCs.add(resolver.resolve(nodes.next().getPath()).adaptTo(EDCDetails.class));
                }
            }
        } catch (Exception e) {
            log.error(String.format("An error occurred while searching for EDCs with the code(s) %s under %s", codes, this.edcDetailsUrl), e);
        }

        return EDCs;
    }

    public Offer getOfferById(ResourceResolver resolver, String productId, Boolean isBundle) {
        Offer offer = null;
        try {
            if(!Strings.isNullOrEmpty(productId)) {
                List<String> paths = new ArrayList<>();
                List<String> resourceTypes = new ArrayList<>();
                if (isBundle) {
                    paths.add(this.bundleOffersUrl);
                    resourceTypes.add(BUNDLE_OFFER_RESOURCE_TYPE_VALUE);
                } else {
                    paths.add(this.offersUrl);
                    paths.add(this.gasOffersUrl);
                    paths.add(this.specialOffersUrl);
                    paths.add(this.communityProgramsUrl);
                    resourceTypes.add(OFFER_RESOURCE_TYPE_VALUE);
                    resourceTypes.add(GAS_OFFER_RESOURCE_TYPE_VALUE);
                }
                Map<String, String> baseQueryPredicate = generateBaseQuery(UNS_TYPE, paths, RESOURCE_TYPE, resourceTypes, "-1");
                if (isBundle) {
                    baseQueryPredicate.put("2_property", BUNDLE_OFFER_BUNDLE_ID_PROP);
                } else {
                    baseQueryPredicate.put("2_property", OFFER_PRODUCT_ID_PROP);
                }
                baseQueryPredicate.put("2_property.value", productId);
                Iterator<Node> nodes = runQuery(resolver, baseQueryPredicate);
                if (nodes.hasNext()) {
                    offer = resolver.resolve(nodes.next().getPath()).adaptTo(Offer.class);
                }
            }
        } catch (Exception e) {
            log.error(String.format("An error occurred while searching for an Offer with the Product ID %s under %s", productId, this.offersUrl), e);
        }

        return offer;
    }

    public List<Offer> getOffersByEDC(ResourceResolver resolver, String code, OfferType offerType) {
        List<Offer> offers = new ArrayList<Offer>();
        try {
            if(!Strings.isNullOrEmpty(code) && offerType != null) {
                List<String> path = new ArrayList<>();
                String offerResourceType = "";

                switch(offerType) {
                    case GAS:
                        path.add(this.gasOffersUrl);
                        offerResourceType = GAS_OFFER_RESOURCE_TYPE_VALUE;
                        break;
                    case ELECTRICITY:
                        path.add(this.offersUrl);
                        offerResourceType = OFFER_RESOURCE_TYPE_VALUE;
                        break;
                    case BUNDLE:
                        path.add(this.bundleOffersUrl);
                        offerResourceType = BUNDLE_OFFER_RESOURCE_TYPE_VALUE;
                }

                Map<String, String> baseQueryPredicate = generateBaseQuery(UNS_TYPE, path, RESOURCE_TYPE, offerResourceType, "-1");
                baseQueryPredicate.put("2_property", OFFER_EDC_CODE_PROP);
                baseQueryPredicate.put("2_property.depth", "2");
                baseQueryPredicate.put("2_property.value", code);
                baseQueryPredicate.put("orderby", "@" + OFFER_PRECEDENCE_PROP);

                Iterator<Node> nodes = runQuery(resolver, baseQueryPredicate);
                while (nodes.hasNext()) {
                    Offer offer = resolver.resolve(nodes.next().getPath()).adaptTo(Offer.class);
                    if (offerType == OfferType.BUNDLE || (offer.getBundleOnly() == null || !offer.getBundleOnly())) {
                        offers.add(offer);
                    }
                }
            }
        } catch (Exception e) {
            log.error(String.format("An error occurred while searching for Offers with the EDC Code %s under %s", code, this.offersUrl), e);
        }

        return offers;
    }

    public String getCommunityProgramsPhone(ResourceResolver resolver) {
        String phoneNumber = "";
        try {
            Node page = resolver.resolve(this.communityProgramsUrl + "/" + JcrConstants.JCR_CONTENT).adaptTo(Node.class);
            phoneNumber = page != null && page.hasProperty("phoneNumber") ? page.getProperty("phoneNumber").getValue().getString(): "";
        } catch (Exception e) {
            log.error(String.format("An error occurred while searching for the CommunityList phone number with the path %s", this.communityProgramsUrl), e);
        }

        return phoneNumber;
    }

    public List<Community> getCommunities(ResourceResolver resolver){
        Node node;
        EDCDetails details;
        Community community;
        Iterator<Node> nodes;
        List<Community> communities = new ArrayList<Community>();
        Map<String, EDCDetails> allEDCs = new HashMap<String, EDCDetails>();

        try {
            List<String> pathEDC = new ArrayList<>();
            pathEDC.add(this.edcDetailsUrl);
            Map<String, String> allEDCsPredicate = generateBaseQuery(UNS_TYPE, pathEDC, RESOURCE_TYPE, EDC_DETAILS_RESOURCE_TYPE_VALUE, "-1");
            nodes = runQuery(resolver, allEDCsPredicate);
            while (nodes.hasNext()) {
                details = resolver.resolve(nodes.next().getPath()).adaptTo(EDCDetails.class);
                allEDCs.put(details.getCode(), details);
            }

            List<String> pathCommunity = new ArrayList<>();
            pathCommunity.add(this.communityProgramsUrl);
            Map<String, String> communitiesPredicate = generateBaseQuery(PAGE_CONTENT_TYPE, pathCommunity, RESOURCE_TYPE, COMMUNITY_RESOURCE_TYPE, "-1");
            communitiesPredicate.put("orderby", "@" + COMMUNITY_NAME_PROP);
            nodes = runQuery(resolver, communitiesPredicate);

            while (nodes.hasNext()) {
                node = nodes.next();
                try {
                    community = resolver.resolve(node.getPath()).adaptTo(Community.class);
                    if(allEDCs.containsKey(community.getEdcCode())) {
                        community.init(resolver, allEDCs.get(community.getEdcCode()));
                        communities.add(community);
                    }
                } catch (Exception e) {
                    log.error(String.format("An error occurred while trying to parse the community located at %s", node.getPath()), e);
                }
            }

        } catch (Exception e) {
            log.error(String.format("An error occurred while retrieving the list of communities under %s", this.communityProgramsUrl), e);
        }

        return communities;
    }

    public String getOffersUrl() {
        return offersUrl;
    }

    public String getGasOffersUrl() { return gasOffersUrl; }

    public String getBundleOffersUrl() { return bundleOffersUrl; }

    public String getBundleEnrollmentUrl() { return bundleEnrollmentUrl; }
}


