package estrategiamovil.comerciomovil.modelo;

import java.io.Serializable;

/**
 * Created by administrator on 31/05/2017.
 */
public class PendingPublication implements Serializable{
    private String idPublication;
    private String status;
    private String coverDescription;
    private String detailedDescription;
    private String path;
    private String imageName;
    private String regularPrice;
    private String offerPrice;
    private String availability;
    private String score;
    private String effectiveDate;
    private String isfav;
    private String company;
    private String state;
    private String googleLocation;
    private String lat;
    private String lng;
    private String isAds;
    private String approvalDetail;

    public String getApprovalDetail() {
        return approvalDetail;
    }

    public void setApprovalDetail(String approvalDetail) {
        this.approvalDetail = approvalDetail;
    }

    public void setIdPublication(String idPublication) {
        this.idPublication = idPublication;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCoverDescription(String coverDescription) {
        this.coverDescription = coverDescription;
    }

    public void setDetailedDescription(String detailedDescription) {
        this.detailedDescription = detailedDescription;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setRegularPrice(String regularPrice) {
        this.regularPrice = regularPrice;
    }

    public void setOfferPrice(String offerPrice) {
        this.offerPrice = offerPrice;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public void setIsfav(String isfav) {
        this.isfav = isfav;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setGoogleLocation(String googleLocation) {
        this.googleLocation = googleLocation;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public void setIsAds(String isAds) {
        this.isAds = isAds;
    }

    public String getIdPublication() {
        return idPublication;
    }

    public String getStatus() {
        return status;
    }

    public String getCoverDescription() {
        return coverDescription;
    }

    public String getDetailedDescription() {
        return detailedDescription;
    }

    public String getPath() {
        return path;
    }

    public String getImageName() {
        return imageName;
    }

    public String getRegularPrice() {
        return regularPrice;
    }

    public String getOfferPrice() {
        return offerPrice;
    }

    public String getAvailability() {
        return availability;
    }

    public String getScore() {
        return score;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public String getIsfav() {
        return isfav;
    }

    public String getCompany() {
        return company;
    }

    public String getState() {
        return state;
    }

    public String getGoogleLocation() {
        return googleLocation;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getIsAds() {
        return isAds;
    }
}
