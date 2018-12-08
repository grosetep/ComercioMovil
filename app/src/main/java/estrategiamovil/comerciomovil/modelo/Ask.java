package estrategiamovil.comerciomovil.modelo;

import java.io.Serializable;

/**
 * Created by administrator on 03/10/2016.
 */
public class Ask implements Serializable {
    private String name_client;
    private String email_client;
    private String phone_client;
    private String message_client;
    private String copy_client;
    private String cover_publication;
    private String company_suscriptor;
    private String email_suscriptor;

    public String getEmail_suscriptor() {
        return email_suscriptor;
    }

    public void setEmail_suscriptor(String email_suscriptor) {
        this.email_suscriptor = email_suscriptor;
    }

    public String getName_client() {
        return name_client;
    }

    public void setName_client(String name_client) {
        this.name_client = name_client;
    }

    public String getEmail_client() {
        return email_client;
    }

    public void setEmail_client(String email_client) {
        this.email_client = email_client;
    }

    public String getPhone_client() {
        return phone_client;
    }

    public void setPhone_client(String phone_client) {
        this.phone_client = phone_client;
    }

    public String getMessage_client() {
        return message_client;
    }

    public void setMessage_client(String message_client) {
        this.message_client = message_client;
    }

    public String getCopy_client() {
        return copy_client;
    }

    public void setCopy_client(String copy_client) {
        this.copy_client = copy_client;
    }

    public String getCover_publication() {
        return cover_publication;
    }

    public void setCover_publication(String cover_publication) {
        this.cover_publication = cover_publication;
    }

    public String getCompany_suscriptor() {
        return company_suscriptor;
    }

    public void setCompany_suscriptor(String company_suscriptor) {
        this.company_suscriptor = company_suscriptor;
    }
}
