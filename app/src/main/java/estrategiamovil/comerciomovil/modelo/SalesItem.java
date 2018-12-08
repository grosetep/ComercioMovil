package estrategiamovil.comerciomovil.modelo;

import java.io.Serializable;

/**
 * Created by administrator on 03/11/2016.
 */

public class SalesItem implements Serializable {
    private String idPurchase;
    private String coverDescription;
    private String idUserPayer;
    private String namePayer;
    private String first;
    private String last;
    private String numItems;
    private String amount;
    private String idPaymentMethod;
    private String paymentMethod;
    private String idStatus;
    private String status;
    private String captureLine;
    private String toPay;
    private String dateCreated;
    private String dateUpdated;
    private String idPublication;
    private String path;
    private String image;
    private String created;
    private String cupons;
    private String moneyRelease;
    private String commission;
    private String productSent;
    private String sentDetail;
    private String productReceived;
    private String receivedDetail;
    private String moneyReleaseDirect;
    private String idVoucher;
    private String routeVoucher;
    private String imageVoucher;

    public String getIdVoucher() {
        return idVoucher;
    }

    public void setIdVoucher(String idVoucher) {
        this.idVoucher = idVoucher;
    }

    public String getRouteVoucher() {
        return routeVoucher;
    }

    public void setRouteVoucher(String routeVoucher) {
        this.routeVoucher = routeVoucher;
    }

    public String getImageVoucher() {
        return imageVoucher;
    }

    public void setImageVoucher(String imageVoucher) {
        this.imageVoucher = imageVoucher;
    }

    public String getMoneyReleaseDirect() {
        return moneyReleaseDirect;
    }

    public void setMoneyReleaseDirect(String moneyReleaseDirect) {
        this.moneyReleaseDirect = moneyReleaseDirect;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getProductSent() {
        return productSent;
    }

    public void setProductSent(String productSent) {
        this.productSent = productSent;
    }

    public String getSentDetail() {
        return sentDetail;
    }

    public void setSentDetail(String sentDetail) {
        this.sentDetail = sentDetail;
    }

    public String getProductReceived() {
        return productReceived;
    }

    public void setProductReceived(String productReceived) {
        this.productReceived = productReceived;
    }

    public String getReceivedDetail() {
        return receivedDetail;
    }

    public void setReceivedDetail(String receivedDetail) {
        this.receivedDetail = receivedDetail;
    }

    public String getMoneyRelease() {
        return moneyRelease;
    }

    public void setMoneyRelease(String moneyRelease) {
        this.moneyRelease = moneyRelease;
    }

    public String getCupons() {
        return cupons;
    }

    public void setCupons(String cupons) {
        this.cupons = cupons;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public SalesItem() {
    }

    public String getIdPurchase() {
        return idPurchase;
    }

    public String getCoverDescription() {
        return coverDescription;
    }

    public void setCoverDescription(String coverDescription) {
        this.coverDescription = coverDescription;
    }

    public void setIdPurchase(String idPurchase) {
        this.idPurchase = idPurchase;
    }

    public String getIdUserPayer() {
        return idUserPayer;
    }

    public void setIdUserPayer(String idUserPayer) {
        this.idUserPayer = idUserPayer;
    }

    public String getNamePayer() {
        return namePayer;
    }

    public void setNamePayer(String namePayer) {
        this.namePayer = namePayer;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getNumItems() {
        return numItems;
    }

    public void setNumItems(String numItems) {
        this.numItems = numItems;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getIdPaymentMethod() {
        return idPaymentMethod;
    }

    public void setIdPaymentMethod(String idPaymentMethod) {
        this.idPaymentMethod = idPaymentMethod;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(String idStatus) {
        this.idStatus = idStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCaptureLine() {
        return captureLine;
    }

    public void setCaptureLine(String captureLine) {
        this.captureLine = captureLine;
    }

    public String getToPay() {
        return toPay;
    }

    public void setToPay(String toPay) {
        this.toPay = toPay;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getIdPublication() {
        return idPublication;
    }

    public void setIdPublication(String idPublication) {
        this.idPublication = idPublication;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


}
