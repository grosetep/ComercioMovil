package estrategiamovil.comerciomovil.ui.activities;

import android.app.Activity;
import android.content.Context;
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
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.DividerItemDecoration;
import estrategiamovil.comerciomovil.tools.VolleyErrorHelper;
import estrategiamovil.comerciomovil.web.VolleySingleton;

public class SearchByCategoryActivity extends AppCompatActivity {
    private static final String TAG = SearchByCategoryActivity.class.getSimpleName();
    private RecyclerView recycler_search_category;
    private LinearLayout layout_all_categories;
    private CategoryViewModel[] categories;
    private Gson gson = new Gson();
    private SearchCategoryAdapter mAdapter;
    private static final int SUBCATEGORY_SELECTED = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_category);
        recycler_search_category = (RecyclerView) findViewById(R.id.recycler_search_category);
        layout_all_categories = (LinearLayout) findViewById(R.id.layout_all_categories);
        layout_all_categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle args = new Bundle();
                SubCategory cat = new SubCategory();
                cat.setSubcategory(getResources().getString(R.string.promt_all_categories));
                cat.setIdCategory("0");
                cat.setIdSubCategory("0");
                args.putSerializable(Constants.SUBCATEGORY_SELECTED, cat);
                intent.putExtras(args);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search_category);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recycler_search_category = (RecyclerView) findViewById(R.id.recycler_search_category);
        recycler_search_category.addItemDecoration(new DividerItemDecoration(SearchByCategoryActivity.this));
        setupListItems();
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
        Log.d(TAG,"getCategories:"+Constants.GET_CATEGORIES+"?method=getCategories");
        JsonObjectRequest request =  new JsonObjectRequest(Request.Method.GET,
                Constants.GET_CATEGORIES+"?method=getCategories",
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
                        String message = VolleyErrorHelper.getErrorType(error, SearchByCategoryActivity.this);
                        Log.d(TAG, "Error Volley: " + error.toString() + " ----- " + error.getCause() + " mensaje usr: " + message);
                        Toast.makeText(
                                SearchByCategoryActivity.this,
                                message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        VolleySingleton.
                getInstance(SearchByCategoryActivity.this).
                addToRequestQueue(
                        request
                );
    }
    private void processingResponse(JSONObject response) {
        Log.d(TAG,"REPSONSE:"+ response.toString());
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    Log.d(TAG,"CategoryPreferencesActivity.success");
                    JSONArray mensaje = response.getJSONArray("categories");
                    categories = gson.fromJson(mensaje.toString(), CategoryViewModel[].class);
                    recycler_search_category.setLayoutManager(new LinearLayoutManager(recycler_search_category.getContext()));
                    mAdapter = new SearchCategoryAdapter(SearchByCategoryActivity.this, Arrays.asList(categories));
                    recycler_search_category.setAdapter(mAdapter);
                    break;
                case "2": // FALLIDO
                    String mensaje2 = response.getString("message");
                    Toast.makeText(
                            SearchByCategoryActivity.this,
                            mensaje2,
                            Toast.LENGTH_LONG).show();
                    break;
                case "3": // FALLIDO
                    String mensaje3 = response.getString("message");
                    Toast.makeText(
                            SearchByCategoryActivity.this,
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
            case SUBCATEGORY_SELECTED:
                if (resultCode == Activity.RESULT_OK){
                    SubCategory subcategory = (SubCategory) data.getSerializableExtra(Constants.SUBCATEGORY_SELECTED);
                    if (subcategory!=null){
                        Intent intent = new Intent();
                        Bundle args = new Bundle();
                        args.putSerializable(Constants.SUBCATEGORY_SELECTED, subcategory);
                        intent.putExtras(args);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
    /****************************************Adapter************************************************************************/
    class SearchCategoryAdapter extends RecyclerView.Adapter<SearchCategoryAdapter.CategoryViewHolder> {

        private final LayoutInflater mInflater;
        private final List<CategoryViewModel> mModels;
        private int mBackground;
        private final TypedValue mTypedValue = new TypedValue();

        public class CategoryViewHolder extends RecyclerView.ViewHolder {

            private final TextView tvText;
            private String idCategory;

            public CategoryViewHolder(View itemView) {
                super(itemView);
                tvText = (TextView) itemView.findViewById(R.id.text_category);
                idCategory = "";
            }

            public void bind(CategoryViewModel model) {
                tvText.setText(model.getCategory());
                idCategory = model.getIdCategory();
            }


        }

        public SearchCategoryAdapter(Context context, List<CategoryViewModel> models) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mInflater = LayoutInflater.from(context);
            mModels = new ArrayList<>(models);
        }

        @Override
        public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = mInflater.inflate(R.layout.search_category_layout, parent, false);
            itemView.setBackgroundResource(mBackground);
            return new CategoryViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final CategoryViewHolder holder, int position) {
            Context context = holder.itemView.getContext();
            final CategoryViewModel model = mModels.get(position);

            holder.bind(model);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context ctx = v.getContext();
                    Intent intent = new Intent(ctx,SearchBySubCategoryActivity.class);
                    Bundle args = new Bundle();
                    args.putSerializable(Constants.CATEGORY_SELECTED, model);
                    intent.putExtras(args);
                    startActivityForResult(intent,SUBCATEGORY_SELECTED);

                }
            });
        }
        @Override
        public int getItemCount() {
            return mModels.size();
        }

    }


}
