package estrategiamovil.comerciomovil.modelo;

import java.io.Serializable;

/**
 * Created by administrator on 01/06/2016.
 */
public class City implements Serializable {
    private String idCity;
    private String city;
    public City(
            String idCity,
            String city
    ){
        this.idCity = idCity;
        this.city = city;
    }


    public String getIdCity() {
        return idCity;
    }

    public String getCity() {
        return city;
    }
}
