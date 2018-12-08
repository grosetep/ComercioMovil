package estrategiamovil.comerciomovil.modelo;

import java.io.Serializable;

/**
 * Created by administrator on 02/09/2016.
 */
public class Category implements Serializable{
    private String idCategory;
    private String category;
    private String idImageRoute;

    public Category() {
    }

    public Category(String idCategory, String category, String idImageRoute) {
        this.idCategory = idCategory;
        this.category = category;
        this.idImageRoute = idImageRoute;
    }

    public String getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(String idCategory) {
        this.idCategory = idCategory;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIdImageRoute() {
        return idImageRoute;
    }

    public void setIdImageRoute(String idImageRoute) {
        this.idImageRoute = idImageRoute;
    }
}
