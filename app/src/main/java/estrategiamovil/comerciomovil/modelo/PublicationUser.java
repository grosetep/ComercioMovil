package estrategiamovil.comerciomovil.modelo;

import java.io.Serializable;

/**
 * Created by administrator on 11/08/2016.
 */
public class PublicationUser implements Serializable {
    private String idPublication;
    private String status;
    private String coverDescription;
    private String idPath;
    private String path;
    private String imageName;
    private String regularPrice;
    private String offerPrice;
    private String percentageOff;
    private String availability;
    private String active_days;
    private String detailedDescription;
    private String outstanding;
    private String characteristics;
    private String isAds;
    private String dateLimit;
    private String pendingQuestions;
    private String approvalDetail;

    public String getPendingQuestions() {
        return pendingQuestions;
    }

    public void setPendingQuestions(String pendingQuestions) {
        this.pendingQuestions = pendingQuestions;
    }

    public String getDateLimit() {
        return dateLimit;
    }

    public void setDateLimit(String dateLimit) {
        this.dateLimit = dateLimit;
    }

    public String getIsAds() {
        return isAds;
    }

    public void setIsAds(String isAds) {
        this.isAds = isAds;
    }

    public String getDetailedDescription() {
        return detailedDescription;
    }

    public void setDetailedDescription(String detailedDescription) {
        this.detailedDescription = detailedDescription;
    }

    public String getOutstanding() {
        return outstanding;
    }

    public void setOutstanding(String outstanding) {
        this.outstanding = outstanding;
    }

    public String getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(String characteristics) {
        this.characteristics = characteristics;
    }

    public String getActive_days() {
        return active_days;
    }

    public void setActive_days(String active_days) {
        this.active_days = active_days;
    }

    public String getIdPublication() {
        return idPublication;
    }

    public void setIdPublication(String idPublication) {
        this.idPublication = idPublication;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCoverDescription() {
        return coverDescription;
    }

    public void setCoverDescription(String coverDescription) {
        this.coverDescription = coverDescription;
    }

    public String getIdPath() {
        return idPath;
    }

    public void setIdPath(String idPath) {
        this.idPath = idPath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getRegularPrice() {
        return regularPrice;
    }

    public void setRegularPrice(String regularPrice) {
        this.regularPrice = regularPrice;
    }

    public String getPercentageOff() {
        return percentageOff;
    }

    public void setPercentageOff(String percentageOff) {
        this.percentageOff = percentageOff;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(String offerPrice) {
        this.offerPrice = offerPrice;
    }
    @Override
    public String toString(){
        return getPath()+getImageName()+","+getCoverDescription()+","+getRegularPrice()+","+getOfferPrice()+","+getAvailability()+","+getPercentageOff() + ", active_days:"+getActive_days();
    }

    public String getApprovalDetail() {
        return approvalDetail;
    }

    public void setApprovalDetail(String approvalDetail) {
        this.approvalDetail = approvalDetail;
    }
}
