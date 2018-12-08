package estrategiamovil.comerciomovil.modelo;

/**
 * Created by administrator on 12/06/2016.
 */
public class Section {
    private String idSection;
    private String section;
    private String emergent;
    public Section(String idSection, String section, String emergent){
        this.idSection = idSection;
        this.section = section;
        this.emergent = emergent;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getIdSection() {
        return idSection;
    }

    public String getSection() {
        return section;
    }

    public String getEmergent() {
        return emergent;
    }
}
