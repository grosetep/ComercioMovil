package estrategiamovil.comerciomovil.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.CategoryViewModel;
import estrategiamovil.comerciomovil.modelo.SubCategory;
import estrategiamovil.comerciomovil.modelo.SubSubCategory;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.DividerItemDecoration;
import estrategiamovil.comerciomovil.tools.VolleyErrorHelper;
import estrategiamovil.comerciomovil.ui.fragments.FindBusinessFragment;
import estrategiamovil.comerciomovil.ui.fragments.PublishFragment;
import estrategiamovil.comerciomovil.web.VolleySingleton;

public class SelectSubCategoryActivity extends AppCompatActivity {
    private static final String TAG = SelectSubCategoryActivity.class.getSimpleName();
    private RecyclerView recyclerview_subcategory;
    private SubCategory[] subcategories;
    private Gson gson = new Gson();
    private SelectSubCategoryAdapter mAdapter;
    private ProgressBar pbLoading_sel_subcategory;
    private SubCategory subcategory_selected = null;
    private String type_flow;
    private LinearLayout layout_all_subcategories;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_sub_category);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_subcategory);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerview_subcategory = (RecyclerView) findViewById(R.id.recyclerview_subcategory);
        recyclerview_subcategory.addItemDecoration(new DividerItemDecoration(SelectSubCategoryActivity.this));
        pbLoading_sel_subcategory = (ProgressBar) findViewById(R.id.pbLoading_sel_subcategory);
        initGUI();
        Intent intent = getIntent();
        String idCategory = intent.getStringExtra(Constants.CATEGORY_SELECTED);
        type_flow = intent.getStringExtra(FindBusinessFragment.TYPE_FLOW_CATEGORY);
        if (type_flow.equals(FindBusinessFragment.FLOW_SEARCHING)) {//show all_categories layout
            layout_all_subcategories.setVisibility(View.VISIBLE);
        }
        /*getting type flow to show or hide options*/
        String url = Constants.GET_CATEGORIES;
        initProcess(true);
        setupListItems(url+"?method=getSubCategoriesByCategory&idCategory="+idCategory);
    }
    private void initGUI(){
        layout_all_subcategories = (LinearLayout) findViewById(R.id.layout_all_subcategories);
        layout_all_subcategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle args = new Bundle();
                //subcategory
                SubCategory sub_category = new SubCategory();
                sub_category.setIdSubCategory("0");
                sub_category.setSubcategory(getString(R.string.promt_all_subcategories));
                //sub_sub_category
                SubSubCategory sub_sub_category = new SubSubCategory("-1","-1","");
                //setting objects
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

    public void setupListItems(String url) {//obtener datos de la publicacion mas datos de las url de imagenes
        Log.d(TAG,"setupListItems:"+url);
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
                        String message = VolleyErrorHelper.getErrorType(error, SelectSubCategoryActivity.this);
                        Log.d(TAG, "Error Volley: " + error.toString() + " ----- " + error.getCause() + " mensaje usr: " + message);
                        Toast.makeText(
                                SelectSubCategoryActivity.this,
                                message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        VolleySingleton.
                getInstance(SelectSubCategoryActivity.this).
                addToRequestQueue(
                        request
                );
    }
    private void initProcess(boolean flag){
        if (pbLoading_sel_subcategory!=null && recyclerview_subcategory!=null){
            recyclerview_subcategory.setVisibility(flag?View.GONE:View.VISIBLE);
            pbLoading_sel_subcategory.setVisibility(flag?View.VISIBLE:View.GONE);

        }
    }

    public SubCategory getSubcategory_selected() {
        return subcategory_selected;
    }

    public void setSubcategory_selected(SubCategory subcategory_selected) {
        this.subcategory_selected = subcategory_selected;
    }

    private void processingResponse(JSONObject response) {
        Log.d(TAG, "response:" + response.toString());
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    Log.d(TAG,"SelectSubCategoryActivity.success");
                    // Obtener array "publications" Json
                    JSONArray mensaje = response.getJSONArray("result");
                    // Parsear con Gson
                    subcategories = gson.fromJson(mensaje.toString(), SubCategory[].class);
                    // Inicializar adaptador
                    recyclerview_subcategory.setLayoutManager(new LinearLayoutManager(recyclerview_subcategory.getContext()));
                    mAdapter = new SelectSubCategoryAdapter(SelectSubCategoryActivity.this, Arrays.asList(subcategories));
                    recyclerview_subcategory.setAdapter(mAdapter);
                    initProcess(false);
                    break;
                case "2": // FALLIDO
                    String mensaje2 = response.getString("message");
                    Toast.makeText(
                            SelectSubCategoryActivity.this,
                            mensaje2,
                            Toast.LENGTH_LONG).show();
                    break;
                case "3": // FALLIDO
                    String mensaje3 = response.getString("message");
                    Toast.makeText(
                            SelectSubCategoryActivity.this,
                            mensaje3,
                            Toast.LENGTH_LONG).show();
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        switch(requestCode) {
            case PublishFragment.SELECT_SUBSUBCATEGORY:
                if (resultCode == Activity.RESULT_OK){
                    SubSubCategory sub_sub_category =(SubSubCategory) data.getSerializableExtra(Constants.SUBSUBCATEGORY_SELECTED);
                    Intent intent = new Intent();
                    Bundle args = new Bundle();
                    args.putSerializable(Constants.SUBSUBCATEGORY_SELECTED,sub_sub_category);
                    args.putSerializable(Constants.SUBCATEGORY_SELECTED,getSubcategory_selected());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchable_menu_category, menu);
        final MenuItem item = menu.findItem(R.id.menu_search_category);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new listener());
        return super.onCreateOptionsMenu(menu);
    }
/***********************************************Adapter*****************************************************/
class SelectSubCategoryAdapter extends RecyclerView.Adapter<SelectSubCategoryAdapter.SubCategoryViewHolder> {
    private final LayoutInflater mInflater;
    private final List<SubCategory> mModels;
    private int mBackground;
    private final TypedValue mTypedValue = new TypedValue();
    private Activity activityContext;

    public class SubCategoryViewHolder extends RecyclerView.ViewHolder {

        private final TextView text_category;
        private final ImageView image;
        private String idSubCategory;
        private String idCategory;


        public SubCategoryViewHolder(View itemView) {
            super(itemView);
            text_category = (TextView) itemView.findViewById(R.id.text_category);
            image = (ImageView) itemView.findViewById(R.id.image);
            idSubCategory = "";
            idCategory = "";
        }

        public void bind(SubCategory model) {
            text_category.setText(model.getSubcategory());
            idSubCategory = model.getIdSubCategory();
            idCategory = model.getIdCategory();
        }


    }

    public SelectSubCategoryAdapter(Activity context, List<SubCategory> models) {
        activityContext = context;
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        mInflater = LayoutInflater.from(context);
        mModels = new ArrayList<>(models);
    }

    @Override
    public SubCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mInflater.inflate(R.layout.select_subcategory_layout, parent, false);
        itemView.setBackgroundResource(mBackground);
        return new SubCategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SubCategoryViewHolder holder, int position) {
        final SubCategory model = mModels.get(position);
        holder.bind(model);
        final int hasChild = Integer.parseInt(model.getHasChildren());
        if (hasChild>0) holder.image.setVisibility(View.VISIBLE);
        else holder.image.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSubcategory_selected(model);
                if  (hasChild>0) {
                    Intent intent = new Intent(getApplicationContext(), SelectSubSubCategoryActivity.class);
                    intent.putExtra(Constants.SUBCATEGORY_SELECTED, model.getIdSubCategory());
                    intent.putExtra(FindBusinessFragment.TYPE_FLOW_CATEGORY, type_flow);
                    startActivityForResult(intent, PublishFragment.SELECT_SUBSUBCATEGORY);
                }else{
                    SubSubCategory sub_sub_category = new SubSubCategory();
                    sub_sub_category.setIdSubSubCategory("-1");
                    sub_sub_category.setSubsubcategory("");
                    Intent intent = new Intent();
                    Bundle args = new Bundle();
                    args.putSerializable(Constants.SUBSUBCATEGORY_SELECTED,sub_sub_category);
                    args.putSerializable(Constants.SUBCATEGORY_SELECTED,model);
                    intent.putExtras(args);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public void animateTo(List<SubCategory> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<SubCategory> newModels) {
        for (int i = mModels.size() - 1; i >= 0; i--) {
            final SubCategory model = mModels.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<SubCategory> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final SubCategory model = newModels.get(i);
            if (!mModels.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<SubCategory> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final SubCategory model = newModels.get(toPosition);
            final int fromPosition = mModels.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public SubCategory removeItem(int position) {
        final SubCategory model = mModels.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, SubCategory model) {
        mModels.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final SubCategory model = mModels.remove(fromPosition);
        mModels.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }


}
class listener implements  SearchView.OnQueryTextListener{
    @Override
    public boolean onQueryTextChange(String query) {
        final List<SubCategory> filteredModelList = filter(Arrays.asList(subcategories), query);
        mAdapter.animateTo(filteredModelList);
        recyclerview_subcategory.scrollToPosition(0);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<SubCategory> filter(List<SubCategory> models, String query) {
        query = query.toLowerCase();
        final List<SubCategory> filteredModelList = new ArrayList<>();
        for (SubCategory model : models) {
            final String text = model.getSubcategory().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }

        return filteredModelList;
    }
}
}
