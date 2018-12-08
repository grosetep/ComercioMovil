package estrategiamovil.comerciomovil.modelo;

import java.io.Serializable;

/**
 * Created by administrator on 28/09/2016.
 */
public class SuscriptionPrice implements Serializable{
    private String id;
    private String cost;
    private String description;
    private String idTypeSuscription;
    private String months;

    public String getMonths() {
        return months;
    }

    public void setMonths(String months) {
        this.months = months;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdTypeSuscription() {
        return idTypeSuscription;
    }

    public void setIdTypeSuscription(String idTypeSuscription) {
        this.idTypeSuscription = idTypeSuscription;
    }
}
