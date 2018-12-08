package estrategiamovil.comerciomovil.modelo;

/**
 * Created by administrator on 28/09/2016.
 */
public class SuscriptionDetail {
    private String idUser;
    private String valid;
    private String days_active;
    private String date_active;
    private String status_suscription;
    private String price_suscription;
    private String price_cupons;
    private String days_total;
    private String suscription_type;
    private String suscription_desc;
    private String exist_suscription_ads;
    private String status;
    private String message;

    @Override
    public String toString() {
    return getIdUser() + " , "+ getValid() + " , "+ getDays_active() + ", " + getDate_active() + ", " + getStatus_suscription() + ", " + getPrice_suscription() + ", "+getPrice_cupons() + " , " + getDays_total() + ", tipo:" + getSuscription_type() + ", desc:"+getSuscription_desc() +" ,"+getExist_suscription_ads()+" , " + getStatus() + ","+ getMessage();
    }

    public String getExist_suscription_ads() {
        return exist_suscription_ads;
    }

    public void setExist_suscription_ads(String exist_suscription_ads) {
        this.exist_suscription_ads = exist_suscription_ads;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public String getDays_active() {
        return days_active;
    }

    public void setDays_active(String days_active) {
        this.days_active = days_active;
    }

    public String getDate_active() {
        return date_active;
    }

    public void setDate_active(String date_active) {
        this.date_active = date_active;
    }

    public String getStatus_suscription() {
        return status_suscription;
    }

    public void setStatus_suscription(String status_suscription) {
        this.status_suscription = status_suscription;
    }

    public String getPrice_suscription() {
        return price_suscription;
    }

    public void setPrice_suscription(String price_suscription) {
        this.price_suscription = price_suscription;
    }

    public String getPrice_cupons() {
        return price_cupons;
    }

    public void setPrice_cupons(String price_cupons) {
        this.price_cupons = price_cupons;
    }

    public String getDays_total() {
        return days_total;
    }

    public void setDays_total(String days_total) {
        this.days_total = days_total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSuscription_type() {
        return suscription_type;
    }

    public void setSuscription_type(String suscription_type) {
        this.suscription_type = suscription_type;
    }

    public String getSuscription_desc() {
        return suscription_desc;
    }

    public void setSuscription_desc(String suscription_desc) {
        this.suscription_desc = suscription_desc;
    }
}
