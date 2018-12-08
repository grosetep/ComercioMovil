package estrategiamovil.comerciomovil.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import java.util.List;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.PublicationCardViewModel;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.StringOperations;
import estrategiamovil.comerciomovil.ui.activities.DetailPublicationActivity;

/**
 * Created by administrator on 14/12/2016.
 */
public class MerchantPubsAdapter extends RecyclerView.Adapter<MerchantPubsAdapter.ViewHolder>  {
    private List<PublicationCardViewModel> publications;
    private Activity activity;
    private static final String TAG = MerchantPubsAdapter.class.getSimpleName();
    public MerchantPubsAdapter(List<PublicationCardViewModel> myDataset,Activity activity) {
        publications = myDataset;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.merchant_pubs_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String ImagePath = publications.get(position).getPath() + publications.get(position).getImageName();
        holder.idPublication = publications.get(position).getIdPublication();
        holder.company = publications.get(position).getCompany();
        holder.isAds = publications.get(position).getIsAds();
        Glide.with(activity.getApplicationContext())
                .load(ImagePath)
                .into(holder.image);
        if (publications.get(position).getIsAds().equals("0")) {//cupon
            holder.text_price.setText(StringOperations.getStringWithDe(StringOperations.getStringWithCashSymbol(publications.get(position).getRegularPrice())) +"  "+
                    StringOperations.getStringWithA(StringOperations.getStringWithCashSymbol(publications.get(position).getOfferPrice())));
        }else{//ads
            holder.text_price.setText(StringOperations.getStringWithCashSymbol(publications.get(position).getOfferPrice()));
        }
        holder.text_cover.setText(publications.get(position).getCoverDescription());
        holder.text_date.setText(publications.get(position).getEffectiveDate());
        holder.text_location.setText(publications.get(position).getState());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, DetailPublicationActivity.class);
                intent.putExtra(DetailPublicationActivity.EXTRA_PUBLICACION, holder.idPublication);
                intent.putExtra(DetailPublicationActivity.EXTRA_TITLE, holder.company);
                intent.putExtra(DetailPublicationActivity.EXTRA_IMAGETITLE, ImagePath);
                intent.putExtra(DetailPublicationActivity.EXTRA_TYPE_PUBLICATION,holder.isAds.equals("0")? Constants.type_publicacion_cupons:Constants.type_publication_ads);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return publications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private String idPublication;
        private String company;
        private String isAds;
        private ImageView image;
        private TextView text_cover;
        private TextView text_price;
        private TextView text_location;
        private TextView text_date;


        public ViewHolder(View v) {
            super(v);
            idPublication = "";
            company = "";
            isAds = "";
            image = (ImageView) v.findViewById(R.id.image);
            text_cover = (TextView) v.findViewById(R.id.text_cover);
            text_price = (TextView) v.findViewById(R.id.text_price);
            text_location = (TextView) v.findViewById(R.id.text_location);
            text_date = (TextView) v.findViewById(R.id.text_date);
        }
    }

}
