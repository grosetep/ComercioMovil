package estrategiamovil.comerciomovil.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.CategoryHelp;

public class HelpContentActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private CategoryHelp category = null;
    private TextView text_title_help;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_content);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        category = (CategoryHelp)intent.getSerializableExtra(HelpLevelCategoriesActivity.CATEGORY);
        progressBar = (ProgressBar) findViewById(R.id.pbLoading);
        progressBar.setVisibility(View.VISIBLE);
        text_title_help = (TextView) findViewById(R.id.text_title_help);
        TextView text = (TextView) findViewById(R.id.text);

        if (category!=null) {
            text.setText(fromHtml(category.getInfo()!=null?category.getInfo():""));
            text_title_help.setText(category.getCategory()!=null?category.getCategory():"");
            progressBar.setVisibility(View.GONE);
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
    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }
}
