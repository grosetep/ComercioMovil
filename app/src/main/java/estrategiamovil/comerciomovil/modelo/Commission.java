package estrategiamovil.comerciomovil.modelo;

/**
 * Created by administrator on 07/06/2017.
 */
public class Commission {
    private String cash;
    private String mp_commission;
    private String message_commissions;

    public String getMessage_commissions() {
        return message_commissions;
    }

    public void setMessage_commissions(String message_commissions) {
        this.message_commissions = message_commissions;
    }

    public String getCash() {
        return cash;
    }

    public void setCash(String cash) {
        this.cash = cash;
    }

    public String getMp_commission() {
        return mp_commission;
    }

    public void setMp_commission(String mp_commission) {
        this.mp_commission = mp_commission;
    }
}
