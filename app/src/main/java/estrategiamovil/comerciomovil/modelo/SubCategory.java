package estrategiamovil.comerciomovil.modelo;

import java.io.Serializable;

/**
 * Created by administrator on 18/08/2016.
 */
public class SubCategory implements Serializable{
    private String idSubCategory;
    private String idCategory;
    private String subcategory;
    private String hasChildren;

    public SubCategory(String idSubCategory, String idCategory, String subcategory, String hasChildren) {
        this.idSubCategory = idSubCategory;
        this.idCategory = idCategory;
        this.subcategory = subcategory;
        this.hasChildren = hasChildren;
    }

    public SubCategory() {
    }

    public String getHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(String hasChildren) {
        this.hasChildren = hasChildren;
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

    public String getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(String idCategory) {
        this.idCategory = idCategory;
    }
}
