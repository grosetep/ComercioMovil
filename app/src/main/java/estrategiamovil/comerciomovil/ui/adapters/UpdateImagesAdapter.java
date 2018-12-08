package estrategiamovil.comerciomovil.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.ImageSliderPublication;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.ImagePicker;
import estrategiamovil.comerciomovil.ui.activities.GalleryActivity;
import estrategiamovil.comerciomovil.ui.fragments.EditImagesFragment;

/**
 * Created by administrator on 13/06/2017.
 */
public class UpdateImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ImageSliderPublication> images;
    private ArrayList<ImageSliderPublication> images_to_remove = new ArrayList<>() ;
    private Activity activity;
    private EditImagesFragment fragment;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_NEW = 1;
    private static final String TAG = UpdateImagesAdapter.class.getSimpleName();

    public ArrayList<ImageSliderPublication> getImages_to_remove() {
        return images_to_remove;
    }

    public void resetImagesToRemove() {
        if (this.images_to_remove!=null)
            this.images_to_remove.clear();
    }

    public UpdateImagesAdapter(List<ImageSliderPublication> myDataset, Activity activity,EditImagesFragment fragment) {
        images = myDataset;
        this.activity = activity;
        this.fragment = fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_edit_layout, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;

        } else {
            View view = LayoutInflater.from(activity).inflate(R.layout.add_new_photo_layout, parent, false);
            return new AddPhotoViewHolder(view);
        }
    }



    @Override
    public int getItemViewType(int position) {
        return images.get(position) == null ? VIEW_TYPE_NEW : VIEW_TYPE_ITEM;
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder p_holder, final int position) {
        if (p_holder instanceof UpdateImagesAdapter.ViewHolder) {
            final UpdateImagesAdapter.ViewHolder holder = (UpdateImagesAdapter.ViewHolder) p_holder;
            holder.checkbox_image.setChecked(false);//limpia siempre la seleccion de usuario
            if (images.get(position).getResource().equals(Constants.resource_remote)) {
                Glide.with(holder.photo.getContext())
                        .load(images.get(position).getPath() + images.get(position).getImageName())
                        .fitCenter()
                        .into(holder.photo);
            }else{
                Glide.with(holder.photo.getContext())
                        .load(new File(images.get(position).getPath()))
                        .error(R.drawable.ic_account_circle)
                        .into(holder.photo);
            }
            holder.checkbox_image.setVisibility(images.get(position).getEnableDeletion().equals(String.valueOf(true))?View.VISIBLE:View.GONE);
            if (images.get(position).getEnableDeletion().equals(String.valueOf(true))){
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.checkbox_image.isChecked()){
                            holder.checkbox_image.setChecked(false);
                            images_to_remove.remove(images.get(position));
                        }else{
                            holder.checkbox_image.setChecked(true);
                            images_to_remove.add(images.get(position));
                        }
                    }
                });
                holder.checkbox_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.checkbox_image.isChecked()){
                            images_to_remove.add(images.get(position));
                        }else{
                            images_to_remove.remove(images.get(position));
                        }
                    }
                });
            }else{
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startGalleryActivity();
                    }
                });
            }

        } else if (p_holder instanceof AddPhotoViewHolder) {
            AddPhotoViewHolder newPhotoViewHolder = (AddPhotoViewHolder) p_holder;
            int available = (Constants.publication_images_number + 1) - images.size();
            String text = activity.getString(R.string.prompt_available_photos, available);
            newPhotoViewHolder.text_available.setText(text);
            newPhotoViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fragment!=null)
                        fragment.pickImage();
                }
            });
        }

    }

    public void startGalleryActivity(){
        Intent intent = new Intent(activity, GalleryActivity.class);
        Bundle bundle = new Bundle();
        ArrayList<ImageSliderPublication> images_copy = new ArrayList();
        images_copy.addAll(images);
        images_copy.removeAll(Collections.singleton(null));
        ImageSliderPublication[] gallery = new ImageSliderPublication[images_copy.size()];
        for (int i = 0;i<images_copy.size();i++){
            if (images_copy.get(i)!=null) {
                gallery[i] = images_copy.get(i);
            }
        }
        bundle.putSerializable(Constants.GALLERY,gallery);
        intent.putExtras(bundle);
        activity.startActivity(intent);
        images_copy.clear();
        images_copy = null;
    }
    @Override
    public int getItemCount() {
        return images.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {


        public ImageView photo;
        public AppCompatCheckBox checkbox_image;


        public ViewHolder(View v) {
            super(v);
            photo = (ImageView) v.findViewById(R.id.photo);
            checkbox_image = (AppCompatCheckBox) v.findViewById(R.id.checkbox_image);

        }
    }
    static class AddPhotoViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public TextView text_available;

        public AddPhotoViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
            text_available = (TextView) itemView.findViewById(R.id.text_available);
        }
    }
}
