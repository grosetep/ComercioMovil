package estrategiamovil.comerciomovil.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.ui.fragments.EditImagesFragment;

public class EditImagesActivity extends AppCompatActivity {
    String idPublication = "";
    private boolean remove_mode = false;
    private static final String TAG = DetailPublicationActivity.class.getSimpleName();
    private MenuItem edit_menu;
    private MenuItem remove_menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_images_publication);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_edit_images);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        idPublication = intent.getStringExtra(DetailPublicationActivity.EXTRA_PUBLICACION);

        EditImagesFragment fragment = (savedInstanceState == null) ? EditImagesFragment.createInstance(idPublication) :
                (EditImagesFragment) getSupportFragmentManager().findFragmentByTag("EditImagesFragment");
        if (savedInstanceState == null) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_edit_images, fragment, "EditImagesFragment")
                .commit();
        }else{
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_edit_images, fragment, "EditImagesFragment")
                    .commit();
        }

    }
    @Override
    public void onBackPressed() {
        if (!remove_mode) {
            finish();
        } else {
            remove_mode = false;
            enableEditionControls(remove_mode);
            addNewPhotoCheck();
            enableContinueValidation();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if (!remove_mode) {
                    finish();
                } else {
                    remove_mode = false;
                    enableEditionControls(remove_mode);
                    addNewPhotoCheck();
                    enableContinueValidation();
                }
                return true;
            case R.id.action_edit_images:
                remove_mode = true;
                enableEditionControls(remove_mode);
                return true;
            case R.id.action_remove_images:
                remove_mode = false;
                removeAndUpdateList();
                enableEditionControls(remove_mode);
                addNewPhotoCheck();
                //check for modifitactions, enable or disable continue button
                enableContinueValidation();
                break;
}

        return super.onOptionsItemSelected(item);
    }
    public void enableContinueValidation(){
        //update items
        EditImagesFragment fragment = (EditImagesFragment)
                getSupportFragmentManager().findFragmentByTag("EditImagesFragment");

        if (fragment != null) {
            fragment.enableContinueValidation();
        }
    }
    private void addNewPhotoCheck(){
        //update items
        EditImagesFragment fragment = (EditImagesFragment)
                getSupportFragmentManager().findFragmentByTag("EditImagesFragment");

        if (fragment != null) {
            fragment.addNewPhotoCheck();
        }
    }
    private void removeAndUpdateList(){
        EditImagesFragment fragment = (EditImagesFragment)
                getSupportFragmentManager().findFragmentByTag("EditImagesFragment");

        if (fragment != null) {
            fragment.removeAndUpdateList();
        }
    }
    private void enableEditionControls(boolean flag){
        //update options
        edit_menu.setVisible((flag)?false:true);
        remove_menu.setVisible((flag)?true:false);
        //update items
        EditImagesFragment fragment = (EditImagesFragment)
                getSupportFragmentManager().findFragmentByTag("EditImagesFragment");

        if (fragment != null) {
            fragment.enableDeletion(flag);
        }
    }
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.edit_menu = menu.findItem(R.id.action_edit_images);
        this.remove_menu = menu.findItem(R.id.action_remove_images);
        if (remove_mode) {
            edit_menu.setVisible(false);
            remove_menu.setVisible(true);
        } else {
            edit_menu.setVisible(true);
            remove_menu.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_images, menu);
        return super.onCreateOptionsMenu(menu);
    }
}