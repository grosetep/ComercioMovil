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

public class SelectOutstandingActivity extends AppCompatActivity {
    private static final String TAG = SelectOutstandingActivity.class.getSimpleName();
    private ProgressBar progressBar;
    private EditText text;
    private TextView text_outstanding_counter;
    private int max_length = 1980;
    private View mCustomView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_outstanding);
        progressBar = (ProgressBar) findViewById(R.id.pbLoading_outstanding);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_outstanding);
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
                        data.putExtra(Constants.OUTSTANDING_SELECTED, text.getText().toString());
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
        text_outstanding_counter = (TextView) findViewById(R.id.text_outstanding_counter);
        text = (EditText) findViewById(R.id.text);
        text.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                int length = s.length();
                text_outstanding_counter.setTextColor(Color.parseColor(length>=max_length?Constants.colorError:Constants.colorSuccess));
                text_outstanding_counter.setText(length + "/"+max_length + " Caracteres Restantes");//which is placed at the top roght corner
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
        String description = intent.getStringExtra(Constants.OUTSTANDING_SELECTED);
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
