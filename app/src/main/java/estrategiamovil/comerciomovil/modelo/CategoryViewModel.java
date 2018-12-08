package estrategiamovil.comerciomovil.modelo;

import java.io.Serializable;

/**
 * Created by administrator on 02/06/2016.
 */
public class CategoryViewModel implements Serializable {
    private String idCategory;
    private String category;
    private String imageCategory;
    private String pathImageCategory;
    public CategoryViewModel(String idCategory,
                             String category,
                             String imageCategory,
                             String pathImageCategory){
        this.idCategory = idCategory;
        this.category = category;
        this.imageCategory = imageCategory;
        this.pathImageCategory = pathImageCategory;
    }

    public CategoryViewModel(){}
    public String getIdCategory() {
        return idCategory;
    }

    public String getCategory() {
        return category;
    }

    public String getImageCategory() {
        return imageCategory;
    }

    public void setIdCategory(String idCategory) {
        this.idCategory = idCategory;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setImageCategory(String imageCategory) {
        this.imageCategory = imageCategory;
    }

    public void setPathImageCategory(String pathImageCategory) {
        this.pathImageCategory = pathImageCategory;
    }

    public String getPathImageCategory() {
        return pathImageCategory;
    }
}
