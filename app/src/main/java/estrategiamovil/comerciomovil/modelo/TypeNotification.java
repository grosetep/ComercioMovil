package estrategiamovil.comerciomovil.modelo;

import java.io.Serializable;

/**
 * Created by administrator on 23/12/2016.
 */
public class TypeNotification implements Serializable {
    private String notificationType;
    private String predefinedId;

    public TypeNotification(String notificationType, String predefinedId) {
        this.notificationType = notificationType;
        this.predefinedId = predefinedId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getPredefinedId() {
        return predefinedId;
    }

    public void setPredefinedId(String predefinedId) {
        this.predefinedId = predefinedId;
    }
}
