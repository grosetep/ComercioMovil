package estrategiamovil.comerciomovil.modelo;

import java.io.Serializable;

/**
 * Created by administrator on 10/02/2017.
 */
public class CategoryResultFilter implements Serializable {
    private String idCategory;
    private String category;
    private String idSubCategory;
    private String subcategory;
    private String idSubSubCategory;
    private String subsubcategory;

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

    public String getIdSubCategory() {
        return idSubCategory;
    }

    public void setIdSubCategory(String idSubCategory) {
        this.idSubCategory = idSubCategory;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getIdSubSubCategory() {
        return idSubSubCategory;
    }

    public void setIdSubSubCategory(String idSubSubCategory) {
        this.idSubSubCategory = idSubSubCategory;
    }

    public String getSubsubcategory() {
        return subsubcategory;
    }

    public void setSubsubcategory(String subsubcategory) {
        this.subsubcategory = subsubcategory;
    }
}
