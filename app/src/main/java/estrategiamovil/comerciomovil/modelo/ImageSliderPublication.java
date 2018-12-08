package estrategiamovil.comerciomovil.modelo;

import java.io.Serializable;

/**
 * Created by administrator on 23/08/2016.
 */
public class ImageSliderPublication implements Serializable{
    private String idPathImage;
    private String path;
    private String imageName;
    private String enableDeletion;
    private String resource;

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getEnableDeletion() {
        return enableDeletion;
    }

    public void setEnableDeletion(String enableDeletion) {
        this.enableDeletion = enableDeletion;
    }

    public ImageSliderPublication(String idPathImage, String path, String imageName, String enableDeletion, String resource) {
        this.idPathImage = idPathImage;
        this.path = path;
        this.imageName = imageName;
        this.enableDeletion = enableDeletion;
        this.resource = resource;
    }

    public ImageSliderPublication() {

    }
    public String getIdPathImage() {
        return idPathImage;
    }

    public void setIdPathImage(String idPathImage) {
        this.idPathImage = idPathImage;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
    @Override
    public String toString(){
        return "id:"+getIdPathImage() + ",path:" + getPath() + ",image:"+getImageName()+",deletion:"+getEnableDeletion()+",source:"+getResource();
    }
}
