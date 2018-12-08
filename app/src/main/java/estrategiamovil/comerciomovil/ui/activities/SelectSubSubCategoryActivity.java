package estrategiamovil.comerciomovil.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import estrategiamovil.comerciomovil.modelo.SubCategory;
import estrategiamovil.comerciomovil.modelo.SubSubCategory;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.DividerItemDecoration;
import estrategiamovil.comerciomovil.tools.VolleyErrorHelper;
import estrategiamovil.comerciomovil.ui.fragments.FindBusinessFragment;
import estrategiamovil.comerciomovil.ui.fragments.PublishFragment;
import estrategiamovil.comerciomovil.web.VolleySingleton;

public class SelectSubSubCategoryActivity extends AppCompatActivity {
    private static final String TAG = SelectSubSubCategoryActivity.class.getSimpleName();
    private RecyclerView recyclerview_subsubcategory;
    private SubSubCategory[] subsubcategories;
    private Gson gson = new Gson();
    private SelectSubSubCategoryAdapter mAdapter;
    private ProgressBar pbLoading_sel_subsubcategory;
    private RelativeLayout no_results_layout;
    private String type_flow;
    private LinearLayout layout_all_subcategories;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_sub_sub_category);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_subsubcategory);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        no_results_layout = (RelativeLayout) findViewById(R.id.no_results_layout);
        recyclerview_subsubcategory = (RecyclerView) findViewById(R.id.recyclerview_subsubcategory);
        recyclerview_subsubcategory.addItemDecoration(new DividerItemDecoration(SelectSubSubCategoryActivity.this));
        pbLoading_sel_subsubcategory = (ProgressBar) findViewById(R.id.pbLoading_sel_subsubcategory);
        initGUI();
        Intent intent = getIntent();
        String idCategory = intent.getStringExtra(Constants.SUBCATEGORY_SELECTED);
        type_flow = intent.getStringExtra(FindBusinessFragment.TYPE_FLOW_CATEGORY);
        if (type_flow.equals(FindBusinessFragment.FLOW_SEARCHING)) {//show all_categories layout
            layout_all_subcategories.setVisibility(View.VISIBLE);
        }
        String url = Constants.GET_CATEGORIES;
        initProcess(true);
        setupListItems(url+"?method=getSubSubCategoriesBySubCategory&idSubCategory="+idCategory);
    }
    private void initGUI(){
        layout_all_subcategories = (LinearLayout) findViewById(R.id.layout_all_subcategories);
        layout_all_subcategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle args = new Bundle();
                //sub_sub_category
                SubSubCategory sub_sub_category = new SubSubCategory();
                sub_sub_category.setIdSubSubCategory("0");
                sub_sub_category.setSubsubcategory(getString(R.string.promt_all_categories));
                //setting objects
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
                        String message = VolleyErrorHelper.getErrorType(error, SelectSubSubCategoryActivity.this);
                        Log.d(TAG, "Error Volley: " + error.toString() + " ----- " + error.getCause() + " mensaje usr: " + message);
                        initProcess(false);
                        no_results_layout.setVisibility(View.VISIBLE);
                    }
                });
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        VolleySingleton.
                getInstance(SelectSubSubCategoryActivity.this).
                addToRequestQueue( request );
    }
    private void initProcess(boolean flag){
        if (pbLoading_sel_subsubcategory!=null && recyclerview_subsubcategory!=null){
            recyclerview_subsubcategory.setVisibility(flag? View.GONE:View.VISIBLE);
            pbLoading_sel_subsubcategory.setVisibility(flag?View.VISIBLE:View.GONE);
        }
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
                    subsubcategories = gson.fromJson(mensaje.toString(), SubSubCategory[].class);
                    // Inicializar adaptador
                    recyclerview_subsubcategory.setLayoutManager(new LinearLayoutManager(recyclerview_subsubcategory.getContext()));
                    mAdapter = new SelectSubSubCategoryAdapter(SelectSubSubCategoryActivity.this, Arrays.asList(subsubcategories));
                    recyclerview_subsubcategory.setAdapter(mAdapter);
                    initProcess(false);
                    no_results_layout.setVisibility(View.GONE);
                    break;
                case "2": // FALLIDO
                    String mensaje2 = response.getString("message");
                    initProcess(false);
                    no_results_layout.setVisibility(View.VISIBLE);
                    break;

            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            initProcess(false);
            no_results_layout.setVisibility(View.VISIBLE);
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
    class SelectSubSubCategoryAdapter extends RecyclerView.Adapter<SelectSubSubCategoryAdapter.SubSubCategoryViewHolder> {
        private final LayoutInflater mInflater;
        private final List<SubSubCategory> mModels;
        private int mBackground;
        private final TypedValue mTypedValue = new TypedValue();
        private Activity activityContext;

        public class SubSubCategoryViewHolder extends RecyclerView.ViewHolder {

            private final TextView text_label;
            private String idSubSubCategory;
            private String idSubCategory;


            public SubSubCategoryViewHolder(View itemView) {
                super(itemView);
                text_label = (TextView) itemView.findViewById(R.id.text_label);
                idSubSubCategory = "";
                idSubCategory = "";
            }

            public void bind(SubSubCategory model) {
                text_label.setText(model.getSubsubcategory());
                idSubSubCategory = model.getIdSubSubCategory();
                idSubCategory = model.getIdSubCategory();
            }


        }

        public SelectSubSubCategoryAdapter(Activity context, List<SubSubCategory> models) {
            activityContext = context;
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mInflater = LayoutInflater.from(context);
            mModels = new ArrayList<>(models);
        }

        @Override
        public SubSubCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = mInflater.inflate(R.layout.item_text_layout, parent, false);
            itemView.setBackgroundResource(mBackground);
            return new SubSubCategoryViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final SubSubCategoryViewHolder holder, int position) {
            final SubSubCategory model = mModels.get(position);
            holder.bind(model);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Intent intent = new Intent();
                Bundle args = new Bundle();
                args.putSerializable(Constants.SUBSUBCATEGORY_SELECTED,model);
                intent.putExtras(args);
                setResult(Activity.RESULT_OK, intent);
                finish();
                }
            });


        }

        @Override
        public int getItemCount() {
            return mModels.size();
        }

        public void animateTo(List<SubSubCategory> models) {
            applyAndAnimateRemovals(models);
            applyAndAnimateAdditions(models);
            applyAndAnimateMovedItems(models);
        }

        private void applyAndAnimateRemovals(List<SubSubCategory> newModels) {
            for (int i = mModels.size() - 1; i >= 0; i--) {
                final SubSubCategory model = mModels.get(i);
                if (!newModels.contains(model)) {
                    removeItem(i);
                }
            }
        }

        private void applyAndAnimateAdditions(List<SubSubCategory> newModels) {
            for (int i = 0, count = newModels.size(); i < count; i++) {
                final SubSubCategory model = newModels.get(i);
                if (!mModels.contains(model)) {
                    addItem(i, model);
                }
            }
        }

        private void applyAndAnimateMovedItems(List<SubSubCategory> newModels) {
            for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
                final SubSubCategory model = newModels.get(toPosition);
                final int fromPosition = mModels.indexOf(model);
                if (fromPosition >= 0 && fromPosition != toPosition) {
                    moveItem(fromPosition, toPosition);
                }
            }
        }

        public SubSubCategory removeItem(int position) {
            final SubSubCategory model = mModels.remove(position);
            notifyItemRemoved(position);
            return model;
        }

        public void addItem(int position, SubSubCategory model) {
            mModels.add(position, model);
            notifyItemInserted(position);
        }

        public void moveItem(int fromPosition, int toPosition) {
            final SubSubCategory model = mModels.remove(fromPosition);
            mModels.add(toPosition, model);
            notifyItemMoved(fromPosition, toPosition);
        }


    }
    class listener implements  SearchView.OnQueryTextListener{
        @Override
        public boolean onQueryTextChange(String query) {
            final List<SubSubCategory> filteredModelList = filter(Arrays.asList(subsubcategories), query);
            mAdapter.animateTo(filteredModelList);
            recyclerview_subsubcategory.scrollToPosition(0);
            return false;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        private List<SubSubCategory> filter(List<SubSubCategory> models, String query) {
            query = query.toLowerCase();
            final List<SubSubCategory> filteredModelList = new ArrayList<>();
            for (SubSubCategory model : models) {
                final String text = model.getSubsubcategory().toLowerCase();
                if (text.contains(query)) {
                    filteredModelList.add(model);
                }
            }

            return filteredModelList;
        }
    }
}
