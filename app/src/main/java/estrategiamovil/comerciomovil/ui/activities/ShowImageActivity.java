package estrategiamovil.comerciomovil.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;

import java.io.File;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.tools.UtilPermissions;

public class ShowImageActivity extends AppCompatActivity {
    private static final String TAG = ShowImageActivity.class.getSimpleName();
    public static final String EXTRA_PATH = "path";
    public static final String EXTRA_TAG = "tag";
    private ProgressBar progressBar;
    private View mCustomView;
    private ImageView image_showed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if(!UtilPermissions.hasPermissions(ShowImageActivity.this, PERMISSIONS)){
            ActivityCompat.requestPermissions(ShowImageActivity.this, PERMISSIONS, UtilPermissions.PERMISSION_ALL);
        }
        progressBar = (ProgressBar) findViewById(R.id.pbLoading_image);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        Intent intent = getIntent();
        final String path = intent.getStringExtra(EXTRA_PATH);
        final String tag = intent.getStringExtra(EXTRA_TAG);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_showimage);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayOptions(
                getSupportActionBar().DISPLAY_SHOW_CUSTOM,
                getSupportActionBar().DISPLAY_SHOW_CUSTOM |  getSupportActionBar().DISPLAY_SHOW_HOME
                        |  getSupportActionBar().DISPLAY_SHOW_TITLE);

        LayoutInflater mInflater = LayoutInflater.from(this);
        mCustomView = mInflater.inflate(R.layout.actionbar_custom_view_delete_close, null);
        mCustomView.findViewById(R.id.actionbar_delete).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // "Done"
                        Intent data = new Intent();
                        data.putExtra(EXTRA_TAG, tag);
                        data.putExtra(EXTRA_PATH,path);
                        setResult(Activity.RESULT_OK, data);
                        finish();
                    }
                });
        mCustomView.findViewById(R.id.actionbar_close).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // "Cancel"
                        Intent data = new Intent();
                        data.putExtra(EXTRA_TAG, tag);
                        setResult(Activity.RESULT_CANCELED, data);
                        finish();

                    }
                });
        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        image_showed = (ImageView) findViewById(R.id.image_showed);
        if (path!=null){
            Glide.with(image_showed.getContext())
                    .load(new File(path))
                    .fitCenter()
                    .error(R.drawable.ic_account_circle)
                    .into(image_showed);
            progressBar.setVisibility(View.GONE);
        }

    }

}
