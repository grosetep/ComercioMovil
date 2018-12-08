package estrategiamovil.comerciomovil.ui.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.tools.Constants;

public class SelectCharacteristicsActivity extends AppCompatActivity {
    private static final String TAG = SelectCharacteristicsActivity.class.getSimpleName();
    private ProgressBar progressBar;
    private EditText text;
    private TextView text_conditions_counter;
    private int max_length = 980;
    private View mCustomView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_characteristics);
        progressBar = (ProgressBar) findViewById(R.id.pbLoading_characteristics);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_characteristics);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayOptions(
                getSupportActionBar().DISPLAY_SHOW_CUSTOM,
                getSupportActionBar().DISPLAY_SHOW_CUSTOM | getSupportActionBar().DISPLAY_SHOW_HOME
                        | getSupportActionBar().DISPLAY_SHOW_TITLE);

        LayoutInflater mInflater = LayoutInflater.from(this);
        mCustomView = mInflater.inflate(R.layout.actionbar_custom_view_done_cancel, null);
        mCustomView.findViewById(R.id.actionbar_done).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // "Done"
                        Intent data = new Intent();
                        data.putExtra(Constants.CHARACTERISTICS_SELECTED, text.getText().toString());
                        setResult(Activity.RESULT_OK, data);
                        finish();
                    }
                });
        mCustomView.findViewById(R.id.actionbar_cancel).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // "Cancel"
                        open();

                    }
                });
        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        text_conditions_counter = (TextView) findViewById(R.id.text_conditions_counter);
        text = (EditText) findViewById(R.id.text);
        text.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                int length = s.length();
                text_conditions_counter.setTextColor(Color.parseColor(length>=max_length?Constants.colorError:Constants.colorSuccess));
                text_conditions_counter.setText(length + "/"+max_length + " Caracteres Restantes");//which is placed at the top roght corner
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        Intent intent = getIntent();
        String description = intent.getStringExtra(Constants.CHARACTERISTICS_SELECTED);
        if (description!=null && description.length()>0)
            text.setText(description);
        progressBar.setVisibility(ProgressBar.GONE);
    }
    public void open(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Se descartarán todos los cambios.");
        alertDialogBuilder.setTitle("Descartar Cambios");
        alertDialogBuilder.setPositiveButton("DESCARTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });

        alertDialogBuilder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(LocationsActivity.this, "You clicked yes button", Toast.LENGTH_LONG).show();

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
}
