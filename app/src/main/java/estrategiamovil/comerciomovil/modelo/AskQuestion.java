package estrategiamovil.comerciomovil.modelo;

import java.io.Serializable;

/**
 * Created by administrator on 27/04/2017.
 */
public class AskQuestion implements Serializable {
    private String idAsk;
    private String idPublication;
    private String question;
    private String response;
    private String answered;
    private String days;
    private String answerable;

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getAnswerable() {
        return answerable;
    }

    public void setAnswerable(String answerable) {
        this.answerable = answerable;
    }

    public String getIdAsk() {
        return idAsk;
    }

    public void setIdAsk(String idAsk) {
        this.idAsk = idAsk;
    }

    public String getIdPublication() {
        return idPublication;
    }

    public void setIdPublication(String idPublication) {
        this.idPublication = idPublication;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getAnswered() {
        return answered;
    }

    public void setAnswered(String answered) {
        this.answered = answered;
    }
}
