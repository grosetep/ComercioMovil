package estrategiamovil.comerciomovil.modelo;



import java.io.Serializable;

/**
 * Created by administrator on 05/07/2016.
 */
public class User implements Serializable{

    private String email;
    private String password;
    private String name;
    private String first;
    private String last;
    private String avatarPath;

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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


    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    @Override
    public String toString(){
        return getEmail() + ","+getFirst()+","+getFirst()+","+getLast()+","+getPassword()+",Avatar Path:"+(getAvatarPath()!=null?getAvatarPath():"No selected");
    }
}
