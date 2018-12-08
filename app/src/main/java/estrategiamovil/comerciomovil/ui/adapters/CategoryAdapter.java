package estrategiamovil.comerciomovil.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.CategoryViewModel;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private static final String TAG = CategoryAdapter.class.getSimpleName();
    private final LayoutInflater mInflater;
    private final List<CategoryViewModel> mModels;
    private HashMap<String , String> idsDragableCategories = new HashMap<>();
    private Context ctx;
    String categories = "";

    private int mBackground;
    private final TypedValue mTypedValue = new TypedValue();
    private HashMap<String,String> categoriesSelected = new HashMap<String,String>();
    public HashMap<String,String> getCategoriesSelected(){ return categoriesSelected;}
    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvText;
        public String mBoundString;
        private CheckBox checkBox;
        private String idCategory;
        private ImageView image_category;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.text_category);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox_category);
            idCategory = "";
            image_category = (ImageView)itemView.findViewById(R.id.image_category);
        }

        public void bind(CategoryViewModel model) {
            Log.d(TAG,"bind->categories:"+categories);
            tvText.setText(model.getCategory());
            mBoundString = model.getIdCategory();
            idCategory = model.getIdCategory();
            if (getCategoriesSelected().containsKey(model.getIdCategory())){
                Log.d(TAG,"Categoria: "+ model.getIdCategory() + " registrada..");
                checkBox.setChecked(true);
            }else{
                Log.d(TAG,"Categoria: "+ model.getIdCategory() + " no registrada..");
                checkBox.setChecked(false);
            }
        }


    }
    private void imprime(HashMap<String,String> map){
        Log.d(TAG,"VALUES:--------------");
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            Log.d(TAG,"VALUE:"+e.getValue());
        }
    }

    public CategoryAdapter(Context context, List<CategoryViewModel> models) {
        ctx = context;
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        mInflater = LayoutInflater.from(context);
        mModels = new ArrayList<>(models);
        initListCategoriesSelected();
    }
    private void initListCategoriesSelected(){
        categories = ApplicationPreferences.getLocalStringPreference(ctx,Constants.categoriesUser);
        Log.d(TAG,"lISTA DE CATEGORIAS SELECCIONADAS POR EL USUARIO EN PREFERENCIAS:"+categories+"<<");
        if (!categories.equals("")){
            Log.d(TAG,"YA HAY PREFERENCIAS GUARDADAS:"+categories);
            String[] listCategoriesSelected = categories.split(",");
            for(String s:listCategoriesSelected){
                categoriesSelected.put(s,s);
            }
        }
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mInflater.inflate(R.layout.list_category_layout, parent, false);
        itemView.setBackgroundResource(mBackground);
        return new CategoryViewHolder(itemView);
    }
    private int getRandomShape() {
        int dg;
        Random random = new Random();

         switch( random.nextInt(5)){
             case 1: dg = R.drawable.shape_icon_blue;
                 break;
             case 2: dg = R.drawable.shape_icon_green;
                 break;
             case 3: dg = R.drawable.shape_icon_orange;
                 break;
             case 4: dg = R.drawable.shape_icon_yellow;
                 break;
             case 5: dg = R.drawable.shape_icon_red;
                 break;
             default:
                 dg = R.drawable.shape_icon_blue;
         }

        return dg;
    }
    @Override
    public void onBindViewHolder(final CategoryViewHolder holder, int position) {
        final CategoryViewModel model = mModels.get(position);
        holder.bind(model);
        //if (model.getPathImageCategory().equals(Constants.imageCategoryInLocalPath))
        holder.image_category.setBackgroundResource(getRandomShape());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkbox_category);
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                    categoriesSelected.remove(holder.idCategory);
                    Log.d(TAG,"setChecked(false), categoriesSelected.remove("+holder.idCategory+")");
                    //imprime(categoriesSelected);
                } else {
                    checkBox.setChecked(true);
                    categoriesSelected.put(holder.idCategory, holder.idCategory);
                    Log.d(TAG,"setChecked(true), categoriesSelected.put("+holder.idCategory+")");
                    //imprime(categoriesSelected);
                }
            }
        });

        Glide.with(holder.image_category.getContext())
                .load(mModels.get(position).getPathImageCategory().equals(Constants.imageCategoryInLocalPath)? new Integer(idsDragableCategories.get(mModels.get(position).getIdCategory())):idsDragableCategories.get(mModels.get(position).getIdCategory())) //mModels.get(position).getPathImageCategory() + mModels.get(position).getImageCategory()
                .fitCenter()
                .into(holder.image_category);


    }
    public void setIdsDrawableCategories(HashMap<String , String> mapIconsDrawables){
        this.idsDragableCategories = mapIconsDrawables;
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public void animateTo(List<CategoryViewModel> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<CategoryViewModel> newModels) {
        for (int i = mModels.size() - 1; i >= 0; i--) {
            final CategoryViewModel model = mModels.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<CategoryViewModel> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final CategoryViewModel model = newModels.get(i);
            if (!mModels.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<CategoryViewModel> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final CategoryViewModel model = newModels.get(toPosition);
            final int fromPosition = mModels.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public CategoryViewModel removeItem(int position) {
        final CategoryViewModel model = mModels.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, CategoryViewModel model) {
        mModels.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final CategoryViewModel model = mModels.remove(fromPosition);
        mModels.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }


}
