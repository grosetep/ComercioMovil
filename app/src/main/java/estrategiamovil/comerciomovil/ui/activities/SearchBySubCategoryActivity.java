package estrategiamovil.comerciomovil.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

public class SearchBySubCategoryActivity extends AppCompatActivity {
    private static final String TAG = SearchByCategoryActivity.class.getSimpleName();
    private RecyclerView recycler_search_subcategory;
    private LinearLayout layout_all_subcategories;
    private SubCategory[] subcategories;
    private Gson gson = new Gson();
    private SearchSubCategoryAdapter mAdapter;
    private SubCategory  subcategory;
    private TextView text_change_subcategory;
    private CategoryViewModel categoryViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_sub_category);
        recycler_search_subcategory = (RecyclerView) findViewById(R.id.recycler_search_subcategory);
        layout_all_subcategories = (LinearLayout) findViewById(R.id.layout_all_subcategories);
        Intent intent = getIntent();
        categoryViewModel = (CategoryViewModel)intent.getSerializableExtra(Constants.CATEGORY_SELECTED);
        layout_all_subcategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle args = new Bundle();
                SubCategory cat = new SubCategory();
                cat.setSubcategory(categoryViewModel.getCategory());
                cat.setIdCategory(categoryViewModel.getIdCategory());
                cat.setIdSubCategory("0");
                args.putSerializable(Constants.SUBCATEGORY_SELECTED, cat);
                intent.putExtras(args);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search_subcategory);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recycler_search_subcategory = (RecyclerView) findViewById(R.id.recycler_search_subcategory);
        recycler_search_subcategory.addItemDecoration(new DividerItemDecoration(SearchBySubCategoryActivity.this));
        text_change_subcategory = (TextView) findViewById(R.id.text_change_subcategory);
        //get CategoryViewModel selected before

        if (categoryViewModel !=null) {
            text_change_subcategory.setText(categoryViewModel.getCategory());
            setupListItems(categoryViewModel);
        }
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
    public void setupListItems(CategoryViewModel categoryViewModel) {
        Log.d(TAG,Constants.GET_CATEGORIES+"?method=getSubCategoriesByCategory&idCategory="+ categoryViewModel.getIdCategory());
        //obtener datos de la publicacion mas datos de las url de imagenes
        JsonObjectRequest request =  new JsonObjectRequest(Request.Method.GET,
                Constants.GET_CATEGORIES+"?method=getSubCategoriesByCategory&idCategory="+ categoryViewModel.getIdCategory(),
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
                        String message = VolleyErrorHelper.getErrorType(error, SearchBySubCategoryActivity.this);
                        Log.d(TAG, "Error Volley: " + error.toString() + " ----- " + error.getCause() + " mensaje usr: " + message);
                        Toast.makeText(
                                SearchBySubCategoryActivity.this,
                                message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        VolleySingleton.
                getInstance(SearchBySubCategoryActivity.this).
                addToRequestQueue(
                        request
                );
    }
    private void processingResponse(JSONObject response) {
        Log.d(TAG, "response:" + response.toString());
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    // Obtener array "publications" Json
                    JSONArray mensaje = response.getJSONArray("result");
                    // Parsear con Gson
                    subcategories = gson.fromJson(mensaje.toString(), SubCategory[].class);
                    // Inicializar adaptador
                    recycler_search_subcategory.setLayoutManager(new LinearLayoutManager(recycler_search_subcategory.getContext()));
                    mAdapter = new SearchSubCategoryAdapter(SearchBySubCategoryActivity.this, Arrays.asList(subcategories));
                    recycler_search_subcategory.setAdapter(mAdapter);
                    break;
                case "2": // FALLIDO
                    String mensaje2 = response.getString("message");
                    Toast.makeText(
                            SearchBySubCategoryActivity.this,
                            mensaje2,
                            Toast.LENGTH_LONG).show();
                    break;
                case "3": // FALLIDO
                    String mensaje3 = response.getString("message");
                    Toast.makeText(
                            SearchBySubCategoryActivity.this,
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
                    args.putSerializable(Constants.SUBCATEGORY_SELECTED,getCategoryViewModel());
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

    public CategoryViewModel getCategoryViewModel() {
        return categoryViewModel;
    }

    /***********************************************Adapter*****************************************************/
    class SearchSubCategoryAdapter extends RecyclerView.Adapter<SearchSubCategoryAdapter.SubCategoryViewHolder> {
        private final LayoutInflater mInflater;
        private final List<SubCategory> mModels;
        private int mBackground;
        private final TypedValue mTypedValue = new TypedValue();
        private Activity activityContext;

        public class SubCategoryViewHolder extends RecyclerView.ViewHolder {

            private final TextView text_label;
            private String idSubCategory;
            private String idCategory;


            public SubCategoryViewHolder(View itemView) {
                super(itemView);
                text_label = (TextView) itemView.findViewById(R.id.text_label);
                idSubCategory = "";
                idCategory = "";
            }

            public void bind(SubCategory model) {
                text_label.setText(model.getSubcategory());
                idSubCategory = model.getIdSubCategory();
                idCategory = model.getIdCategory();
            }


        }

        public SearchSubCategoryAdapter(Activity context, List<SubCategory> models) {
            activityContext = context;
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mInflater = LayoutInflater.from(context);
            mModels = new ArrayList<>(models);
        }

        @Override
        public SubCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = mInflater.inflate(R.layout.item_text_layout, parent, false);
            itemView.setBackgroundResource(mBackground);
            return new SubCategoryViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final SubCategoryViewHolder holder, int position) {
            final SubCategory model = mModels.get(position);
            holder.bind(model);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /* old
                    Intent intent = new Intent();
                    Bundle args = new Bundle();
                    args.putSerializable(Constants.SUBCATEGORY_SELECTED,model);
                    intent.putExtras(args);
                    setResult(Activity.RESULT_OK, intent);
                    finish();*/
                    Intent intent = new Intent(getApplicationContext(), SelectSubSubCategoryActivity.class);
                    intent.putExtra(Constants.SUBCATEGORY_SELECTED, model.getIdSubCategory());
                    intent.putExtra(FindBusinessFragment.TYPE_FLOW_CATEGORY,FindBusinessFragment.FLOW_SEARCHING);
                    startActivityForResult(intent, PublishFragment.SELECT_SUBSUBCATEGORY);
                }
            });
        }
        @Override
        public int getItemCount() {
            return mModels.size();
        }

    }


}
