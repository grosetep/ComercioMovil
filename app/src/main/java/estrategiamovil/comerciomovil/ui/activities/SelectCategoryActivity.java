package estrategiamovil.comerciomovil.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.CategoryViewModel;
import estrategiamovil.comerciomovil.modelo.SubCategory;
import estrategiamovil.comerciomovil.modelo.SubSubCategory;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.DividerItemDecoration;
import estrategiamovil.comerciomovil.tools.VolleyErrorHelper;
import estrategiamovil.comerciomovil.ui.fragments.FindBusinessFragment;
import estrategiamovil.comerciomovil.ui.fragments.PublishFragment;
import estrategiamovil.comerciomovil.web.VolleySingleton;

public class SelectCategoryActivity extends AppCompatActivity {
    private static final String TAG = SelectCategoryActivity.class.getSimpleName();
    private RecyclerView recyclerview_category;
    private CategoryViewModel[] categories;
    private Gson gson = new Gson();
    private SelectCategoryAdapter mAdapter;
    private ProgressBar pbLoading_sel_category;
    private CategoryViewModel category_selected;
    private LinearLayout layout_all_subcategories;
    private String type_flow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_category);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerview_category = (RecyclerView) findViewById(R.id.recyclerview_category);
        recyclerview_category.addItemDecoration(new DividerItemDecoration(SelectCategoryActivity.this));
        pbLoading_sel_category = (ProgressBar) findViewById(R.id.pbLoading_sel_category);
        initGUI();
        /*getting type flow to show or hide options*/
        Intent i = getIntent();
        type_flow = i.getStringExtra(FindBusinessFragment.TYPE_FLOW_CATEGORY);
        if (type_flow.equals(FindBusinessFragment.FLOW_SEARCHING)) {//show all_categories layout
            layout_all_subcategories.setVisibility(View.VISIBLE);
        }
        initProcess(true);
        setupListItems();
    }
    private void initGUI(){
        layout_all_subcategories = (LinearLayout) findViewById(R.id.layout_all_subcategories);
        layout_all_subcategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle args = new Bundle();
                //category
                CategoryViewModel category_selected_all = new CategoryViewModel("0",getString(R.string.promt_all_categories),"","");
                //subcategory
                SubCategory sub_category = new SubCategory("-1","-1","","");
                //sub_sub_category
                SubSubCategory sub_sub_category = new SubSubCategory("-1","-1","");
                //setting objects
                Log.d(TAG,"retorno de categorias todas: cat:" + category_selected_all.getIdCategory()+ " sub:"+sub_category.getIdSubCategory()+" subsub:"+sub_sub_category.getIdSubSubCategory());
                args.putSerializable(Constants.CATEGORY_SELECTED, category_selected_all);
                args.putSerializable(Constants.SUBCATEGORY_SELECTED,sub_category);
                args.putSerializable(Constants.SUBSUBCATEGORY_SELECTED,sub_sub_category);
                intent.putExtras(args);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setupListItems() {//obtener datos de la publicacion mas datos de las url de imagenes
        String url = Constants.GET_CATEGORIES+"?method=getCategories";
        JsonObjectRequest request =  new JsonObjectRequest(Request.Method.GET,
                url,
                (String) null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        processingResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        String message = VolleyErrorHelper.getErrorType(error, SelectCategoryActivity.this);
                        Log.d(TAG, "Error Volley: " + error.toString() + " ----- " + error.getCause() + " mensaje usr: " + message);
                        Toast.makeText(
                                SelectCategoryActivity.this,
                                message,
                                Toast.LENGTH_SHORT).show();

                    }
                });
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        VolleySingleton.
                getInstance(SelectCategoryActivity.this).
                addToRequestQueue(
                        request
                );
    }
    private void processingResponse(JSONObject response) {
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    Log.d(TAG,"CategoryPreferencesActivity.success");
                    // Obtener array "publications" Json
                    JSONArray mensaje = response.getJSONArray("categories");
                    // Parsear con Gson
                    categories = gson.fromJson(mensaje.toString(), CategoryViewModel[].class);
                    // Inicializar adaptador
                    recyclerview_category.setLayoutManager(new LinearLayoutManager(recyclerview_category.getContext()));
                    mAdapter = new SelectCategoryAdapter(SelectCategoryActivity.this, Arrays.asList(categories));
                    mAdapter.setIdsDrawableCategories(setIconsCategories(categories));
                    recyclerview_category.setAdapter(mAdapter);
                    initProcess(false);
                    break;
                case "2": // FALLIDO
                    String mensaje2 = response.getString("message");
                    Toast.makeText(
                            SelectCategoryActivity.this,
                            mensaje2,
                            Toast.LENGTH_LONG).show();
                    break;
                case "3": // FALLIDO
                    String mensaje3 = response.getString("message");
                    Toast.makeText(
                            SelectCategoryActivity.this,
                            mensaje3,
                            Toast.LENGTH_LONG).show();
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

    }
    private HashMap<String , String> setIconsCategories( CategoryViewModel[] categories ){
        HashMap<String , String> map=new HashMap<>();
        for(CategoryViewModel c:categories){
            //Log.d(TAG,"id:"+c.getIdCategory()+" image:" + c.getImageCategory() + " path: " + c.getPathImageCategory() + " category:"+c.getCategory());
            if (c.getPathImageCategory().equals(Constants.imageCategoryInLocalPath)) {//imagen desde recursos locales
                int resID = getResources().getIdentifier(c.getImageCategory().toString(), "drawable", getPackageName());
                map.put(c.getIdCategory(), "" + resID);
            }
            else{//imagen de servidor remoto
                map.put(c.getIdCategory(),c.getPathImageCategory() + c.getImageCategory());
            }

        }
        return map;
    }

    private void initProcess(boolean flag){
       if (pbLoading_sel_category!=null && recyclerview_category!=null){
           recyclerview_category.setVisibility(flag?View.GONE:View.VISIBLE);
           pbLoading_sel_category.setVisibility(flag?View.VISIBLE:View.GONE);

       }
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode) {
            case PublishFragment.SELECT_SUBCATEGORY:
                    if (resultCode == Activity.RESULT_OK){
                        SubSubCategory sub_sub_category =(SubSubCategory) data.getSerializableExtra(Constants.SUBSUBCATEGORY_SELECTED);
                        SubCategory sub_category =(SubCategory) data.getSerializableExtra(Constants.SUBCATEGORY_SELECTED);
                        Intent intent = new Intent();
                        Bundle args = new Bundle();
                        args.putSerializable(Constants.SUBSUBCATEGORY_SELECTED,sub_sub_category);
                        args.putSerializable(Constants.SUBCATEGORY_SELECTED,sub_category);
                        args.putSerializable(Constants.CATEGORY_SELECTED,category_selected);
                        intent.putExtras(args);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }

    }

    /****************************************Adapter************************************************************************/
    class SelectCategoryAdapter extends RecyclerView.Adapter<SelectCategoryAdapter.CategoryViewHolder> {

        private final LayoutInflater mInflater;
        private final List<CategoryViewModel> mModels;
        private int mBackground;
        private final TypedValue mTypedValue = new TypedValue();
        private HashMap<String , String> idsDragableCategories = new HashMap<>();
        private Activity activity;
        public class CategoryViewHolder extends RecyclerView.ViewHolder {

            private final TextView tvText;
            private String idCategory;
            private ImageView imageViewCircle;

            public CategoryViewHolder(View itemView) {
                super(itemView);
                tvText = (TextView) itemView.findViewById(R.id.text_category);
                idCategory = "";
                imageViewCircle = (ImageView)itemView.findViewById(R.id.image_category);
            }

            public void bind(CategoryViewModel model) {
                tvText.setText(model.getCategory());
                idCategory = model.getIdCategory();
            }


        }

        public SelectCategoryAdapter(Activity act, List<CategoryViewModel> models) {
            activity =act;
            act.getApplicationContext().getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mInflater = LayoutInflater.from(activity);
            mModels = new ArrayList<>(models);
        }

        @Override
        public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = mInflater.inflate(R.layout.select_category_layout, parent, false);
            itemView.setBackgroundResource(mBackground);
            return new CategoryViewHolder(itemView);
        }
        private int getRandomShape() {
            int dg;
            Random random = new Random();

            switch( random.nextInt(1)){
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
            Context context = holder.imageViewCircle.getContext();
            final CategoryViewModel model = mModels.get(position);

            holder.bind(model);
            holder.imageViewCircle.setBackgroundResource(getRandomShape());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Intent intent = new Intent();
                    Bundle args = new Bundle();
                    args.putSerializable(Constants.CATEGORY_SELECTED, model);
                    intent.putExtras(args);
                    setResult(Activity.RESULT_OK, intent);
                    finish();*/
                    category_selected = model;
                    Intent intent = new Intent(v.getContext(), SelectSubCategoryActivity.class);
                    intent.putExtra(Constants.CATEGORY_SELECTED, model.getIdCategory());
                    intent.putExtra(FindBusinessFragment.TYPE_FLOW_CATEGORY,type_flow);
                    activity.startActivityForResult(intent, PublishFragment.SELECT_SUBCATEGORY);
                }
            });

            Glide.with(holder.imageViewCircle.getContext())
                    .load(mModels.get(position).getPathImageCategory().equals(Constants.imageCategoryInLocalPath)? new Integer(idsDragableCategories.get(mModels.get(position).getIdCategory())):idsDragableCategories.get(mModels.get(position).getIdCategory())) //mModels.get(position).getPathImageCategory() + mModels.get(position).getImageCategory()
                    .fitCenter()
                    .into(holder.imageViewCircle);
        }
        public void setIdsDrawableCategories(HashMap<String , String> mapIconsDrawables){
            this.idsDragableCategories = mapIconsDrawables;
        }
        @Override
        public int getItemCount() {
            return mModels.size();
        }

    }

}
