package estrategiamovil.comerciomovil.modelo;

/**
 * Created by administrator on 29/06/2016.
 */
public class Sector {
    private String idSector;
    private String sector;
    private String description;
    public Sector(){}

    public Sector(String idSector, String sector, String description) {
        this.idSector = idSector;
        this.sector = sector;
        this.description = description;
    }

    public void setIdSector(String idSector) {
        this.idSector = idSector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdSector() {
        return idSector;
    }

    public String getSector() {
        return sector;
    }

    public String getDescription() {
        return description;
    }

}
