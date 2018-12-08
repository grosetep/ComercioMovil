package estrategiamovil.comerciomovil.modelo;

import java.io.Serializable;

/**
 * Created by administrator on 12/10/2016.
 */
public class PreOrder implements Serializable{
    private String idUser;
    private String idMerchant;
    private String idPublication;
    private String numItems;
    private String totalAmount;
    private String paymentMethod;
    private String product;
    private String price;
    private String price_unit;
    private String price_publication;
    private String iva;
    private String email_user;
    private String email_publication;
    private String name_suscriptor;
    private String phone_suscriptor;
    private String name_user;
    private String last_name_user;
    public PreOrder(){}

    public String getPhone_suscriptor() {
        return phone_suscriptor;
    }

    public void setPhone_suscriptor(String phone_suscriptor) {
        this.phone_suscriptor = phone_suscriptor;
    }

    public String getIdMerchant() {
        return idMerchant;
    }

    public void setIdMerchant(String idMerchant) {
        this.idMerchant = idMerchant;
    }

    public String getLast_name_user() {
        return last_name_user;
    }

    public void setLast_name_user(String last_name_user) {
        this.last_name_user = last_name_user;
    }

    public String getPrice_publication() {
        return price_publication;
    }

    public void setPrice_publication(String price_publication) {
        this.price_publication = price_publication;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdPublication() {
        return idPublication;
    }

    public void setIdPublication(String idPublication) {
        this.idPublication = idPublication;
    }

    public String getNumItems() {
        return numItems;
    }

    public void setNumItems(String numItems) {
        this.numItems = numItems;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice_unit() {
        return price_unit;
    }

    public void setPrice_unit(String price_unit) {
        this.price_unit = price_unit;
    }

    public String getIva() {
        return iva;
    }

    public void setIva(String iva) {
        this.iva = iva;
    }

    public String getEmail_user() {
        return email_user;
    }

    public void setEmail_user(String email_user) {
        this.email_user = email_user;
    }

    public String getEmail_publication() {
        return email_publication;
    }

    public void setEmail_publication(String email_publication) {
        this.email_publication = email_publication;
    }

    public String getName_suscriptor() {
        return name_suscriptor;
    }

    public void setName_suscriptor(String name_suscriptor) {
        this.name_suscriptor = name_suscriptor;
    }

    public String getName_user() {
        return name_user;
    }

    public void setName_user(String name_user) {
        this.name_user = name_user;
    }
}
