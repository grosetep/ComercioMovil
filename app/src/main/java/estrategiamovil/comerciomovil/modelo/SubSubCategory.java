package estrategiamovil.comerciomovil.modelo;

import java.io.Serializable;

/**
 * Created by administrator on 01/02/2017.
 */
public class SubSubCategory implements Serializable {
    private String idSubSubCategory;
    private String idSubCategory;
    private String subsubcategory;

    public SubSubCategory(String idSubSubCategory, String idSubCategory, String subsubcategory) {
        this.idSubSubCategory = idSubSubCategory;
        this.idSubCategory = idSubCategory;
        this.subsubcategory = subsubcategory;
    }

    public SubSubCategory() {
    }

    public String getIdSubSubCategory() {
        return idSubSubCategory;
    }

    public void setIdSubSubCategory(String idSubSubCategory) {
        this.idSubSubCategory = idSubSubCategory;
    }

    public String getIdSubCategory() {
        return idSubCategory;
    }

    public void setIdSubCategory(String idSubCategory) {
        this.idSubCategory = idSubCategory;
    }

    public String getSubsubcategory() {
        return subsubcategory;
    }

    public void setSubsubcategory(String subsubcategory) {
        this.subsubcategory = subsubcategory;
    }
}
