package estrategiamovil.comerciomovil.modelo;

/**
 * Created by administrator on 26/05/2016.
 */
public class PublicationCardViewModel {
    private String idPublication;
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

    public PublicationCardViewModel(String idPublication, String coverDescription, String detailedDescription, String path, String imageName, String regularPrice, String offerPrice, String availability, String score, String effectiveDate, String isfav, String company, String state, String googleLocation, String lat, String lng, String isAds) {
        this.idPublication = idPublication;
        this.coverDescription = coverDescription;
        this.detailedDescription = detailedDescription;
        this.path = path;
        this.imageName = imageName;
        this.regularPrice = regularPrice;
        this.offerPrice = offerPrice;
        this.availability = availability;
        this.score = score;
        this.effectiveDate = effectiveDate;
        this.isfav = isfav;
        this.company = company;
        this.state = state;
        this.googleLocation = googleLocation;
        this.lat = lat;
        this.lng = lng;
        this.isAds = isAds;
    }

    /**
     * compare attributs from two publications
     *
     * @param publication PublicationCardViewModel externa
     * @return true si son iguales, false si hay cambios
     */
    public boolean compareTo(PublicationCardViewModel publication) {
        return this.getIdPublication().compareTo(publication.getIdPublication()) == 0 &&
                this.getCoverDescription().compareTo(publication.getCoverDescription()) == 0 &&
                this.getDetailedDescription().compareTo(publication.getDetailedDescription()) == 0 &&
                this.getPath().compareTo(publication.getPath()) == 0 &&
                this.getImageName().compareTo(publication.getImageName()) == 0 &&
                this.getRegularPrice().compareTo(publication.getRegularPrice()) == 0 &&
                this.getOfferPrice().compareTo(publication.getOfferPrice()) == 0 &&
                this.getAvailability().compareTo(publication.getAvailability()) == 0 &&
                this.getScore().compareTo(publication.getScore()) == 0 &&
                this.getEffectiveDate().compareTo(publication.getEffectiveDate()) == 0 &&
                this.getIsfav().compareTo(publication.getIsfav()) == 0 &&
                this.getCompany().compareTo(publication.getCompany()) == 0 &&
                this.getState().compareTo(publication.getState()) == 0;
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
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof PublicationCardViewModel) {
            PublicationCardViewModel p = (PublicationCardViewModel) obj;
            return p.getIdPublication().equals(idPublication);
        }



        else return false;

    }
}
