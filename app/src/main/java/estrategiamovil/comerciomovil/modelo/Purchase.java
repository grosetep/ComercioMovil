package estrategiamovil.comerciomovil.modelo;

import java.io.Serializable;

/**
 * Created by administrator on 11/10/2016.
 */
public class Purchase implements Serializable {
    private String id_purchase;
    private String capture_line;
    private String transaction_date;
    private String transaction_amount;
    private String transaction_method;
    private String transaction_status;
    private String transaction_method_id;
    private String resourceUrl;

    public Purchase(String id_purchase, String capture_line, String transaction_date, String transaction_amount, String transaction_method, String transaction_status, String transaction_method_id) {
        this.id_purchase = id_purchase;
        this.capture_line = capture_line;
        this.transaction_date = transaction_date;
        this.transaction_amount = transaction_amount;
        this.transaction_method = transaction_method;
        this.transaction_status = transaction_status;
        this.transaction_method_id = transaction_method_id;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public String getTransaction_method_id() {
        return transaction_method_id;
    }

    public void setTransaction_method_id(String transaction_method_id) {
        this.transaction_method_id = transaction_method_id;
    }

    public String getId_purchase() {
        return id_purchase;
    }

    public void setId_purchase(String id_purchase) {
        this.id_purchase = id_purchase;
    }

    public String getCapture_line() {
        return capture_line;
    }

    public void setCapture_line(String capture_line) {
        this.capture_line = capture_line;
    }

    public String getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }

    public String getTransaction_amount() {
        return transaction_amount;
    }

    public void setTransaction_amount(String transaction_amount) {
        this.transaction_amount = transaction_amount;
    }

    public String getTransaction_method() {
        return transaction_method;
    }

    public void setTransaction_method(String transaction_method) {
        this.transaction_method = transaction_method;
    }

    public String getTransaction_status() {
        return transaction_status;
    }

    public void setTransaction_status(String transaction_status) {
        this.transaction_status = transaction_status;
    }
}
