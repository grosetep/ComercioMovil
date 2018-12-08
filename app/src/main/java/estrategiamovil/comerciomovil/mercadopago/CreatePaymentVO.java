package estrategiamovil.comerciomovil.mercadopago;

import android.app.Activity;

import com.mercadopago.model.Discount;
import com.mercadopago.model.PaymentMethod;

import estrategiamovil.comerciomovil.modelo.PreOrder;
import estrategiamovil.comerciomovil.modelo.Purchase;

/**
 * Created by administrator on 14/10/2016.
 */
public class CreatePaymentVO {
    private Activity activity;
    private String token;
    private Integer installments;
    private Long cardIssuerId;
    private Long campaign;
    private PaymentMethod paymentMethod;
    private Discount discount;
    private Purchase purchase;
    private PreOrder preOrder;

    public CreatePaymentVO(Activity activity, String token, Integer installments, Long cardIssuerId, Long campaign, PaymentMethod paymentMethod, Discount discount, Purchase purchase, PreOrder preOrder) {
        this.activity = activity;
        this.token = token;
        this.installments = installments;
        this.cardIssuerId = cardIssuerId;
        this.campaign = campaign;
        this.paymentMethod = paymentMethod;
        this.discount = discount;
        this.purchase = purchase;
        this.preOrder = preOrder;
    }

    public Long getCampaign() {
        return campaign;
    }

    public void setCampaign(Long campaign) {
        this.campaign = campaign;
    }

    public CreatePaymentVO(){}

    public PreOrder getPreOrder() {
        return preOrder;
    }

    public void setPreOrder(PreOrder preOrder) {
        this.preOrder = preOrder;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public Long getCardIssuerId() {
        return cardIssuerId;
    }

    public void setCardIssuerId(Long cardIssuerId) {
        this.cardIssuerId = cardIssuerId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }
}
