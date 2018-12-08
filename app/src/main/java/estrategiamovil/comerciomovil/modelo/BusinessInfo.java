package estrategiamovil.comerciomovil.modelo;

import java.io.Serializable;

/**
 * Created by administrator on 07/09/2016.
 */
public class BusinessInfo implements Serializable {
    private String idUser;
    private String company;
    private String imageRoute;
    private String nameImage;
    private String registerDate;
    private String web;
    private String category;
    private String phone;
    private String locations;
    private String email;
    private String state;
    private String idPerson;
    private String businessDescription;

    @Override
    public String toString(){
        return "id: "+getIdUser() + ", \ncompany:" + getCompany() + ", \npath:"+getImageRoute() + ", \nImage:"+getNameImage()+
                ", \nRegisterDate:"+getRegisterDate() + ", \nweb:"+getWeb() + ", \ncategoria:"+getCategory()+", \nPhone:"+getPhone()+
                ", \nLocations:"+getLocations()+", \nEmail:"+getEmail()+", \nState:"+getState()+", \nIdPerson:"+getIdPerson()+", \nInfo:"+getBusinessDescription();
    }

    public String getBusinessDescription() {
        return businessDescription;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setBusinessDescription(String businessDescription) {
        this.businessDescription = businessDescription;
    }

    public String getIdPerson() {
        return idPerson;
    }

    public void setIdPerson(String idPerson) {
        this.idPerson = idPerson;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocations() {
        return locations;
    }

    public void setLocations(String locations) {
        this.locations = locations;
    }

    public String getWeb() {
        return web;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getIdUser() {
        return idUser;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getImageRoute() {
        return imageRoute;
    }

    public void setImageRoute(String imageRoute) {
        this.imageRoute = imageRoute;
    }

    public String getNameImage() {
        return nameImage;
    }

    public void setNameImage(String nameImage) {
        this.nameImage = nameImage;
    }
}
