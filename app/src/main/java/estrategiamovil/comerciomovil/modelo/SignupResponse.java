package estrategiamovil.comerciomovil.modelo;

/**
 * Created by administrator on 17/07/2016.
 */
public class SignupResponse {
    private int newUserId;
    private int status;
    private String message;

    public SignupResponse() {
    }

    public int getNewUserId() {
        return newUserId;
    }

    public void setNewUserId(int newUserId) {
        this.newUserId = newUserId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString(){
        return getNewUserId() + ","+getStatus()+","+getMessage();
    }
}
