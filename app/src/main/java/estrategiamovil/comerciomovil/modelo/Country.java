package estrategiamovil.comerciomovil.modelo;

/**
 * Created by administrator on 31/05/2016.
 */
public class Country {
    private String idCountry;
    private String country;
    public Country(
            String idCountry,
            String country
    ){
        this.idCountry = idCountry;
        this.country = country;
    }

    public String getIdCountry() {
        return idCountry;
    }

    public String getCountry() {
        return country;
    }
}
