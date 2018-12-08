package estrategiamovil.comerciomovil.modelo;

import java.io.Serializable;

/**
 * Created by administrator on 23/11/2016.
 */
public class Filter implements Serializable {
    private String dateFrom;
    private String dateTo;
    private boolean all;
    private String captureLine;

    public Filter(String dateFrom, String dateTo, boolean all,String captureLine) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.all = all;
        this.captureLine = captureLine;
    }

    public String getCaptureLine() {
        return captureLine;
    }

    public void setCaptureLine(String captureLine) {
        this.captureLine = captureLine;
    }

    public boolean isAll() {
        return all;
    }

    public Filter() {
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }
    @Override
    public String toString(){
        return getDateFrom()+","+getDateTo()+",filtered:"+(isAll()?"false":"true");
    }
}
