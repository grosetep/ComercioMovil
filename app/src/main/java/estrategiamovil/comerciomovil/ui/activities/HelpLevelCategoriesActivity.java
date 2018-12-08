package estrategiamovil.comerciomovil.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import estrategiamovil.comerciomovil.modelo.CategoryHelp;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.tools.DividerItemDecoration;
import estrategiamovil.comerciomovil.tools.ShowConfirmations;
import estrategiamovil.comerciomovil.tools.VolleyErrorHelper;
import estrategiamovil.comerciomovil.web.VolleySingleton;

public class HelpLevelCategoriesActivity extends AppCompatActivity {
    public static String CATEGORY = "object_category";
    public static int GET_CATEGORY_LEVEL_1 = 1;
    private RecyclerView mRecycler;
    private CategoryHelpLevelAdapter mAdapter;
    private CategoryHelp[] categories;
    private Gson gson = new Gson();
    private ProgressBar loading_help;
    private TextView text_title_help;
    private CategoryHelp category = null;


    private static final String TAG = HelpLevelCategoriesActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_level_categories);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_category_help_level);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        category = (CategoryHelp)intent.getSerializableExtra(HelpLevelCategoriesActivity.CATEGORY);
        initGUI();
        initProcess(true);
        if (category!=null) {
            //calculate url
            StringBuffer url = new StringBuffer();
            url.append(Constants.GET_CATEGORIES_HELP);
            switch (category.getLevel()){
                case "level_0":url.append("?method=getCategories_level_1&id_category=").append(category.getId());break;
                case "level_1":url.append("?method=getCategories_level_2&id_category=").append(category.getId());break;
                case "level_2":url.append("?method=getCategories_level_3&id_category=").append(category.getId());break;
            }

            setupListItems(url.toString());
        }else{
            ShowConfirmations.showConfirmationMessage(getString(R.string.generic_server_down),HelpLevelCategoriesActivity.this);
        }
    }
    private void initGUI(){
        mRecycler = (RecyclerView) findViewById(R.id.recycler_category_help);
        mRecycler.addItemDecoration(new DividerItemDecoration(HelpLevelCategoriesActivity.this));
        loading_help = (ProgressBar) findViewById(R.id.loading_help);
        text_title_help = (TextView) findViewById(R.id.text_title_help);
        if (category != null) text_title_help.setText(category.getCategory()!=null?category.getCategory():getString(R.string.prompt_help_title));
    }
    private void initProcess(boolean flag){
        if (loading_help!=null && mRecycler!=null){
            mRecycler.setVisibility(flag? View.GONE:View.VISIBLE);
            loading_help.setVisibility(flag?View.VISIBLE:View.GONE);
        }
    }
    public void setupListItems(String url) {
        Log.d(TAG,"URL:"+url);
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
                        String message = VolleyErrorHelper.getErrorType(error, HelpLevelCategoriesActivity.this);
                        Log.d(TAG, "Error Volley: " + error.toString() + " ----- " + error.getCause() + " mensaje usr: " + message);
                        Toast.makeText(
                                HelpLevelCategoriesActivity.this,
                                message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        VolleySingleton.
                getInstance(HelpLevelCategoriesActivity.this).
                addToRequestQueue(
                        request
                );
    }
    private void processingResponse(JSONObject response) {
        Log.d(TAG, "processingResponse:"+response);
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    Log.d(TAG,"success");
                    // Obtener array "publications" Json
                    JSONArray mensaje = response.getJSONArray("result");
                    // Parsear con Gson
                    categories = gson.fromJson(mensaje.toString(), CategoryHelp[].class);
                    // Inicializar adaptador
                    mRecycler.setLayoutManager(new LinearLayoutManager(mRecycler.getContext()));
                    mAdapter = new CategoryHelpLevelAdapter(HelpLevelCategoriesActivity.this, Arrays.asList(categories));
                    mRecycler.setAdapter(getmAdapter());
                    initProcess(false);
                    break;
                case "2": // FALLIDO
                    Log.d(TAG,"CategoryPreferencesActivity.fail");
                    String mensaje2 = response.getString("message");
                    Toast.makeText(
                            HelpLevelCategoriesActivity.this,
                            mensaje2,
                            Toast.LENGTH_LONG).show();
                    break;
                case "3": // FALLIDO
                    Log.d(TAG,"CategoryPreferencesActivity.fail");
                    String mensaje3 = response.getString("message");
                    Toast.makeText(
                            HelpLevelCategoriesActivity.this,
                            mensaje3,
                            Toast.LENGTH_LONG).show();
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            Toast.makeText(
                    HelpLevelCategoriesActivity.this,
                    getResources().getString(R.string.generic_error),
                    Toast.LENGTH_LONG).show();
        }

    }
    public CategoryHelpLevelAdapter getmAdapter() {
        return mAdapter;
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
    class CategoryHelpLevelAdapter extends RecyclerView.Adapter<CategoryHelpLevelAdapter.CategoryHelpViewHolder> {
        private final LayoutInflater mInflater;
        private final List<CategoryHelp> mModels;
        private int mBackground;
        private final TypedValue mTypedValue = new TypedValue();
        private Activity activityContext;
        public class CategoryHelpViewHolder extends RecyclerView.ViewHolder {

            private final TextView text_category;
            private final ImageView image;
            private CategoryHelp category = null;


            public CategoryHelpViewHolder(View itemView) {
                super(itemView);
                text_category = (TextView) itemView.findViewById(R.id.text_category);
                image = (ImageView) itemView.findViewById(R.id.image);
            }

            public void bind(CategoryHelp model) {
                text_category.setText(model.getCategory());
                category = model;
            }


        }

        public CategoryHelpLevelAdapter(Activity context, List<CategoryHelp> models) {
            activityContext = context;
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mInflater = LayoutInflater.from(context);
            mModels = new ArrayList<>(models);
        }

        @Override
        public CategoryHelpViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = mInflater.inflate(R.layout.select_subcategory_layout, parent, false);
            itemView.setBackgroundResource(mBackground);
            return new CategoryHelpViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final CategoryHelpViewHolder holder, int position) {
            final CategoryHelp model = mModels.get(position);
            holder.bind(model);
            final int hasChild = Integer.parseInt(model.getHasChildren());
            if (hasChild>0) holder.image.setVisibility(View.VISIBLE);
            else holder.image.setVisibility(View.GONE);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if  (hasChild>0) {
                        Intent intent = new Intent(getApplicationContext(), HelpLevelCategoriesActivity.class);
                        intent.putExtra(HelpLevelCategoriesActivity.CATEGORY,model);
                        startActivityForResult(intent, HelpLevelCategoriesActivity.GET_CATEGORY_LEVEL_1);
                    }else{
                        Intent intent = new Intent(getApplicationContext(), HelpContentActivity.class);
                        intent.putExtra(HelpLevelCategoriesActivity.CATEGORY,model);
                        startActivityForResult(intent, HelpLevelCategoriesActivity.GET_CATEGORY_LEVEL_1);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mModels.size();
        }
    }
}
