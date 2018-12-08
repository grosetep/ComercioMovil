package estrategiamovil.comerciomovil.modelo;

/**
 * Created by administrator on 26/08/2016.
 */
public class NotificationTopic {
    private String idTopic;
    private String topic;
    private String idCategory;

    public String getIdTopic() {
        return idTopic;
    }

    public void setIdTopic(String idTopic) {
        this.idTopic = idTopic;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(String idCategory) {
        this.idCategory = idCategory;
    }

    public NotificationTopic(String idTopic, String topic, String idCategory) {
        this.idTopic = idTopic;
        this.topic = topic;
        this.idCategory = idCategory;
    }
}
