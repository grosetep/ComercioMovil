package estrategiamovil.comerciomovil.modelo;

/**
 * Created by administrator on 18/07/2016.
 */
public class LoginResponse {
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
    private String remotePath;
    private String remoteImage;
    private String categories;

    public LoginResponse() {
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
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

    public String getAdministrator() {
        return administrator;
    }

    public void setAdministrator(String administrator) {
        this.administrator = administrator;
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
    @Override
    public String toString(){
        return  "Email:"+getEmail()+",Name:"+
                getName()+",First:"+
                getFirst()+",Last:"+
                getLast()+",status:"+
                getStatus()+",address:"+
                getAddress()+",avatarPath:"+
                getAvatarPath()+",company:"+
                getCompany()+",idPersona:"+
                getIdPersona()+",idPersonType:"+
                getIdPersonType()+",idUsuario:"+
                getIdUsuario()+",userType:"+
                getUserType()+",phone:"+
                getPhone()+",sector:"+
                getWebsite()+",administrator:"+
                getAdministrator()+",remotePath:"+
                getRemotePath()+",remoteImage:"+
                getRemoteImage()+",categories:"+
                getCategories();
    }
}
