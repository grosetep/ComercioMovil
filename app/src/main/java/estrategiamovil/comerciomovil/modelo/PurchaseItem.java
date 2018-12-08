package estrategiamovil.comerciomovil.modelo;

import java.io.Serializable;

/**
 * Created by administrator on 27/10/2016.
 */
public class PurchaseItem implements Serializable{
    private String idPurchase;
    private String idUser;
    private String idPublication;
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
    private String coverDescription;
    private String path;
    private String image;
    private String cupons;
    private String info;
    private String rated;
    private String idMerchant;
    private String productSent;
    private String detailSent;
    private String productReceived;
    private String receivedDetail;
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

    public String getProductSent() {
        return productSent;
    }

    public void setProductSent(String productSent) {
        this.productSent = productSent;
    }

    public String getDetailSent() {
        return detailSent;
    }

    public void setDetailSent(String detailSent) {
        this.detailSent = detailSent;
    }

    public PurchaseItem() {
    }

    public String getRated() {
        return rated;
    }

    public void setRated(String rated) {
        this.rated = rated;
    }

    public String getCoverDescription() {
        return coverDescription;
    }

    public void setCoverDescription(String coverDescription) {
        this.coverDescription = coverDescription;
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

    public String getIdPurchase() {
        return idPurchase;
    }

    public void setIdPurchase(String idPurchase) {
        this.idPurchase = idPurchase;
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

    public String getCupons() {
        return cupons;
    }

    public void setCupons(String cupons) {
        this.cupons = cupons;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getIdMerchant() {
        return idMerchant;
    }

    public void setIdMerchant(String idMerchant) {
        this.idMerchant = idMerchant;
    }
}
