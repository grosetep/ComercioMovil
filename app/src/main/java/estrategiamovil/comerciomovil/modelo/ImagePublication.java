package estrategiamovil.comerciomovil.modelo;

import android.graphics.Bitmap;

/**
 * Created by administrator on 17/08/2016.
 */
public class ImagePublication {
    private Bitmap bitmap;
    private String name;
    private String path;

    public ImagePublication(Bitmap img,String name, String path){
        this.bitmap = img;
        this.name = name;
        this.path =  path;

    }
    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
