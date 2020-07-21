package com.energyharbor.akron.core.services;

import com.energyharbor.akron.core.enums.OfferType;
import com.energyharbor.akron.core.models.Community;
import com.energyharbor.akron.core.models.EDCDetails;
import com.energyharbor.akron.core.models.Offer;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;

public interface OfferEDCService {

    /**
     * Returns a list of EDCDetails based on a list of codes
     * @param resolver a ResourceResolver with permissions to '/content'
     * @param codes comma separated list of EDC codes
     * @return a list of EDCs (or empty list)
     */
    List<EDCDetails> getEDCDetails(ResourceResolver resolver, String codes);


    /**
     * Returns an Offer based on the Product ID provided.
     * @param resolver a ResourceResolver with permissions to '/content'
     * @param productId Product Id of the Offer
     * @param isBundle is getting a bundle offer or not
     * @return a Offer (or null)
     */
    Offer getOfferById(ResourceResolver resolver, String productId, Boolean isBundle);

    /**
     * Returns a list of Offers based on the EDC Code provided.
     * @param resolver a ResourceResolver with permissions to '/content'
     * @param edcCode EDC Code used to look for Offers
     * @param offerType the type of the offer to get
     * @return a list of Offer (or empty list)
     */
    List<Offer> getOffersByEDC(ResourceResolver resolver, String edcCode, OfferType offerType);

    /**
     * Returns the phone number added to the community list page properties added to the parent path
     * @param resolver a ResourceResolver with permissions to '/content'
     * @return a String with the phone number
     */
    String getCommunityProgramsPhone(ResourceResolver resolver);

    /**
     * Returns the offers url
     * @return a String with the path to the offers url
     */
    String getOffersUrl();

    /**
     * Returns the gas offers url
     * @return a String with the path to the offers url
     */
    String getGasOffersUrl();

    /**
     * Returns the bundle offers url
     * @return a String with the path to the offers url
     */
    String getBundleOffersUrl();

    /**
     * Returns the list of the existing Communities under the defined Community Programs page.
     * @param resolver a ResourceResolver with permissions to '/content'
     * @return a list of Communities
     */
    List<Community> getCommunities(ResourceResolver resolver);
}
