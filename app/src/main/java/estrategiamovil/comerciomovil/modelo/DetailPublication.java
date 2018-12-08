package estrategiamovil.comerciomovil.modelo;

import java.io.Serializable;

/**
 * Created by administrator on 14/06/2016.
 */
public class DetailPublication implements Serializable{
    private String idPublication;
    private String coverDescription;
    private String detailedDescription;
    private String regularPrice;
    private String offerPrice;
    private String score;
    private String effectiveDate;
    private String product;
    private String percentageOff;
    private String availability;
    private String important;
    private String characteristics;
    private String street;
    private String outdoorNumber;
    private String interiorNumber;
    private String reference;
    private String town;
    private String state;
    private String country;
    private String company;
    private String website;
    private String latitude;
    private String longitude;
    private String googleAddress;
    private String subcategory;
    private String publicationDate;
    private String phone;
    private String email;
    private String idMerchant;
    private String businessDescription;
    private String already_rated;
    private String has_questions;
    private String virtual;
    private String shippingOnStore;

    public DetailPublication(String idPublication, String coverDescription, String detailedDescription, String regularPrice, String offerPrice, String score, String effectiveDate, String product, String percentageOff, String availability, String important, String characteristics, String street, String outdoorNumber, String interiorNumber, String reference, String town, String state, String country, String company, String website, String latitude, String longitude, String googleAddress, String subcategory, String publicationDate, String phone, String email, String idMerchant, String businessDescription, String already_rated, String has_questions, String virtual, String shippingOnStore) {
        this.idPublication = idPublication;
        this.coverDescription = coverDescription;
        this.detailedDescription = detailedDescription;
        this.regularPrice = regularPrice;
        this.offerPrice = offerPrice;
        this.score = score;
        this.effectiveDate = effectiveDate;
        this.product = product;
        this.percentageOff = percentageOff;
        this.availability = availability;
        this.important = important;
        this.characteristics = characteristics;
        this.street = street;
        this.outdoorNumber = outdoorNumber;
        this.interiorNumber = interiorNumber;
        this.reference = reference;
        this.town = town;
        this.state = state;
        this.country = country;
        this.company = company;
        this.website = website;
        this.latitude = latitude;
        this.longitude = longitude;
        this.googleAddress = googleAddress;
        this.subcategory = subcategory;
        this.publicationDate = publicationDate;
        this.phone = phone;
        this.email = email;
        this.idMerchant = idMerchant;
        this.businessDescription = businessDescription;
        this.already_rated = already_rated;
        this.has_questions = has_questions;
        this.virtual = virtual;
        this.shippingOnStore = shippingOnStore;
    }

    public String getAlready_rated() {
        return already_rated;
    }

    public String getHas_questions() {
        return has_questions;
    }

    public String getVirtual() {
        return virtual;
    }

    public String getShippingOnStore() {
        return shippingOnStore;
    }

    public String getBusinessDescription() {
        return businessDescription;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public String getGoogleAddress() {
        return googleAddress;
    }

    public String getIdPublication() {
        return idPublication;
    }

    public String getCoverDescription() {
        return coverDescription;
    }

    public String getDetailedDescription() {
        return detailedDescription;
    }

    public String getRegularPrice() {
        return regularPrice;
    }

    public String getOfferPrice() {
        return offerPrice;
    }

    public String getScore() {
        return score;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public String getProduct() {
        return product;
    }

    public String getPercentageOff() {
        return percentageOff;
    }

    public String getAvailability() {
        return availability;
    }

    public String getImportant() {
        return important;
    }

    public String getCharacteristics() {
        return characteristics;
    }

    public String getStreet() {
        return street;
    }

    public String getOutdoorNumber() {
        return outdoorNumber;
    }

    public String getInteriorNumber() {
        return interiorNumber;
    }

    public String getReference() {
        return reference;
    }

    public String getTown() {
        return town;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getCompany() {
        return company;
    }

    public String getWebsite() {
        return website;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getIdMerchant() {
        return idMerchant;
    }
}
