package estrategiamovil.comerciomovil.modelo;

import java.io.Serializable;

/**
 * Created by administrator on 27/12/2016.
 */
public class ProfileUser implements Serializable {
    private String idPersona;
    private String name;
    private String first;
    private String last;
    private String phone;
    private String company;
    private String website;
    private String address;
    private String idUsuario;
    private String email;
    private String status;
    private String avatarPath;
    private String administrator;
    private String idUserType;
    private String userType;
    private String idPersonType;
    private String personType;
    private String idRouteImage;
    private String remotePath;
    private String remoteImage;
    private String businessDescription;

    public ProfileUser(String idPersona, String name, String first, String last, String phone, String company, String website, String address, String idUsuario, String email, String status, String avatarPath, String administrator, String idUserType, String userType, String idPersonType, String personType, String idRouteImage, String remotePath, String remoteImage, String businessDescription) {
        this.idPersona = idPersona;
        this.name = name;
        this.first = first;
        this.last = last;
        this.phone = phone;
        this.company = company;
        this.website = website;
        this.address = address;
        this.idUsuario = idUsuario;
        this.email = email;
        this.status = status;
        this.avatarPath = avatarPath;
        this.administrator = administrator;
        this.idUserType = idUserType;
        this.userType = userType;
        this.idPersonType = idPersonType;
        this.personType = personType;
        this.idRouteImage = idRouteImage;
        this.remotePath = remotePath;
        this.remoteImage = remoteImage;
        this.businessDescription = businessDescription;
    }

    public String getBusinessDescription() {
        return businessDescription;
    }

    public void setBusinessDescription(String businessDescription) {
        this.businessDescription = businessDescription;
    }

    public String getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(String idPersona) {
        this.idPersona = idPersona;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getAdministrator() {
        return administrator;
    }

    public void setAdministrator(String administrator) {
        this.administrator = administrator;
    }

    public String getIdUserType() {
        return idUserType;
    }

    public void setIdUserType(String idUserType) {
        this.idUserType = idUserType;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getIdPersonType() {
        return idPersonType;
    }

    public void setIdPersonType(String idPersonType) {
        this.idPersonType = idPersonType;
    }

    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }

    public String getIdRouteImage() {
        return idRouteImage;
    }

    public void setIdRouteImage(String idRouteImage) {
        this.idRouteImage = idRouteImage;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    public String getRemoteImage() {
        return remoteImage;
    }

    public void setRemoteImage(String remoteImage) {
        this.remoteImage = remoteImage;
    }
}
