package estrategiamovil.comerciomovil.modelo;

/**
 * Created by administrator on 23/08/2016.
 */
public class PublicationAddressAditional {
    private String idUbicacion;
    private String googleAddress;
    private String reference;
    private String latitude;
    private String longitude;

    public String getIdUbicacion() {
        return idUbicacion;
    }

    public void setIdUbicacion(String idUbicacion) {
        this.idUbicacion = idUbicacion;
    }

    public String getGoogleAddress() {
        return googleAddress;
    }

    public void setGoogleAddress(String googleAddress) {
        this.googleAddress = googleAddress;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public PublicationAddressAditional(String idUbicacion, String googleAddress, String reference, String latitude, String longitude) {
        this.idUbicacion = idUbicacion;
        this.googleAddress = googleAddress;
        this.reference = reference;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
