package estrategiamovil.comerciomovil.modelo;

import java.io.Serializable;

/**
 * Created by administrator on 07/11/2016.
 */
public class Favorite implements Serializable {
    private String idPublication;
    private String coverDescription;
    private String company;
    private String path;
    private String image;
    private String affectiveDate;
    private String days;
    private String regularPrice;
    private String offerPrice;
    public Favorite() {
    }

    public String getAffectiveDate() {
        return affectiveDate;
    }

    public String getRegularPrice() {
        return regularPrice;
    }

    public void setRegularPrice(String regularPrice) {
        this.regularPrice = regularPrice;
    }

    public String getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(String offerPrice) {
        this.offerPrice = offerPrice;
    }

    public void setAffectiveDate(String affectiveDate) {
        this.affectiveDate = affectiveDate;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getIdPublication() {
        return idPublication;
    }

    public void setIdPublication(String idPublication) {
        this.idPublication = idPublication;
    }

    public String getCoverDescription() {
        return coverDescription;
    }

    public void setCoverDescription(String coverDescription) {
        this.coverDescription = coverDescription;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
