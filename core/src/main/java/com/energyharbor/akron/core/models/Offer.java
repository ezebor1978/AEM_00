package com.energyharbor.akron.core.models;

import com.energyharbor.akron.core.enums.OfferType;
import com.energyharbor.akron.core.services.OfferEDCService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Sling Model for the Offer component
 */
@Model(adaptables = Resource.class)
public class Offer {

    @OSGiService
    private transient OfferEDCService offerEDCService;

    @Self
    private transient Resource resource;

    @Inject
    @Optional
    private String title;

    @Inject
    @Optional
    private String marketingTitle;

    @Inject
    @Optional
    private String marketingBackground;

    @Inject
    @Optional
    private String icon;

    @Inject
    @Optional
    private String duration;

    @Inject
    @Optional
    private String price;

    @Inject
    @Optional
    private String priceLabel;

    @Inject
    @Optional
    private String priceDescription;

    @Inject
    @Optional
    private String greenOfferImage;

    @Inject
    @Optional
    private String greenOfferText;

    @Inject
    @Optional
    private boolean showDetails;

    @Inject
    @Optional
    private String moreDetails;

    @Inject
    @Optional
    private String bestFor;

    @Inject
    @Optional
    private String productId;

    @Inject
    @Optional
    private String edcCode;

    @Inject
    @Optional
    private String enrollmentUrl;

    @Inject
    @Optional
    private String priorityCode;

    @Inject
    @Optional
    private String gasProductId;

    @Inject
    @Optional
    private String electricityProductId;

    @Inject
    @Optional
    private String bundleId;

    @Inject
    @Optional
    private Boolean bundleOnly;

    @Inject
    @Optional
    private String gasIcon;

    @Inject
    @Optional
    private String electricityIcon;

    @Inject
    @Optional
    @Named("multifieldPriceBreakdown")
    private transient Resource rawPriceBreakdown;

    @Inject
    @Optional
    @Named("multifieldEdcProviders")
    private transient Resource rawEdcProviders;

    private List<Fee> multifieldPriceBreakdown;

    private List<String> multifieldEdcProviders;

    private Offer bundleGasOffer;

    private Offer bundleElectricityOffer;

    private OfferType offerType;

    private EDCDetails edcDetails;

    private Boolean isBundle;

    private Boolean isValidOffer;

    public String getTitle() {
        return this.title;
    }

    public String getMarketingTitle() {
        return this.marketingTitle;
    }

    public String getMarketingBackground() {
        return this.marketingBackground;
    }

    public String getIcon() {
        return this.icon;
    }

    public String getDuration() {
        return this.duration;
    }

    public String getPrice() {
        return this.price;
    }

    public String getPriceLabel() {
        return this.priceLabel;
    }

    public String getPriceDescription() {
        return this.priceDescription;
    }

    public String getGreenOfferImage() {
        return this.greenOfferImage;
    }

    public String getGreenOfferText() {
        return this.greenOfferText;
    }

    public boolean getShowDetails() {
        return this.showDetails;
    }

    public String getMoreDetails() {
        return this.moreDetails;
    }

    public String getBestFor() {
        return this.bestFor;
    }

    public String getProductId() {
        return this.productId;
    }

    public String getEdcCode() {
        return this.edcCode;
    }

    public String getEnrollmentUrl() {
        return this.enrollmentUrl;
    }

    public String getPriorityCode() {
        return this.priorityCode;
    }

    public List<Fee> getMultifieldPriceBreakdown() {
        return this.multifieldPriceBreakdown;
    }

    public Boolean getIsBundle() { return this.isBundle; }

    public String getGasProductId() { return this.gasProductId; }

    public String getElectricityProductId() { return this.electricityProductId; }

    public List<String> getMultifieldEdcProviders() {
        return this.multifieldEdcProviders;
    }

    public Offer getBundleGasOffer() {
        return bundleGasOffer;
    }

    public Offer getBundleElectricityOffer() {
        return bundleElectricityOffer;
    }

    public EDCDetails getEdcDetails() {
        return edcDetails;
    }

    public OfferType getOfferType() {
        return offerType;
    }

    public Boolean getIsValidOffer() {
        return isValidOffer;
    }

    public String getBundleId() {
        return bundleId;
    }

    public Boolean getBundleOnly() { return bundleOnly; }

    public String getGasIcon() { return  gasIcon; }

    public String getElectricityIcon() { return electricityIcon; }

    @PostConstruct
    protected void init() throws RepositoryException {
        isValidOffer = false;
        multifieldPriceBreakdown = new ArrayList<Fee>();
        if(rawPriceBreakdown != null) {
            Iterator<Resource> iterator = rawPriceBreakdown.listChildren();
            while(iterator.hasNext()) {
                Fee fee = iterator.next().adaptTo(Fee.class);
                if(fee != null) {
                    multifieldPriceBreakdown.add(fee);
                }
            }
        }

        List<EDCDetails> edcResult = offerEDCService.getEDCDetails(resource.getResourceResolver(), edcCode);
        if (!edcResult.isEmpty()) {
            edcDetails = edcResult.get(0);
            isValidOffer = true;
        }

        String resourcePath = resource.getPath();
        String gasOffersUrl = offerEDCService.getGasOffersUrl();
        String electricityOffersUrl = offerEDCService.getOffersUrl();
        String bundleOffersUrl = offerEDCService.getBundleOffersUrl();

        if (resourcePath.startsWith(gasOffersUrl)) {
            offerType = OfferType.GAS;
        } else if (resourcePath.startsWith(electricityOffersUrl)) {
            offerType = OfferType.ELECTRICITY;
        } else if (resourcePath.startsWith(bundleOffersUrl)) {
            offerType = OfferType.BUNDLE;
        }

        isBundle = offerType == OfferType.BUNDLE;

        if (isBundle != null && isBundle == true) {
            bundleInit();
        }
    }

    public void bundleInit() throws RepositoryException {
        multifieldEdcProviders = new ArrayList<String>();
        if(rawEdcProviders != null) {
            Iterator<Resource> iterator = rawEdcProviders.listChildren();
            while(iterator.hasNext()) {
                Node edcCodeNode =  iterator.next().adaptTo(Node.class);
                if(edcCodeNode != null) {
                    multifieldEdcProviders.add(edcCodeNode.getProperty("edcCode").getString());
                }
            }
        }

        if (gasProductId != null) {
            bundleGasOffer = offerEDCService.getOfferById(resource.getResourceResolver(), gasProductId, false);
        }

        if (electricityProductId != null) {
            bundleElectricityOffer = offerEDCService.getOfferById(resource.getResourceResolver(), electricityProductId, false);
        }
    }

    public String toJSON() {
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();
        return gson.toJson(this);
    }

    @Override
    public String toString() {
        return this.toJSON();
    }
}


