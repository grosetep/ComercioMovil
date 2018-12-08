package estrategiamovil.comerciomovil.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Calendar;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.Filter;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.ui.fragments.DatePickerFragment;
import estrategiamovil.comerciomovil.ui.fragments.PublishFragment;

public class FilterActivity extends AppCompatActivity implements DatePickerFragment.OnDateSelectedListener {
    private static final String TAG = FilterActivity.class.getSimpleName();
    public static final String FILTER = "filter";
    private EditText text_date_from;
    private EditText text_date_to;
    private EditText text_capture_line;
    private Calendar from;
    private Calendar to;
    private AppCompatCheckBox check_filter;
    private ProgressBar pbLoading_update;
    private AppCompatButton button_accept;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_filter);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initGUI();
    }
    @Override
    public void onDateSelected(int ano, int mes, int dia) {
        Log.d(TAG,"fecha: " + ano + " / " + mes + " / " + dia);
        actualizarFecha(ano, mes, dia);
    }
    public void actualizarFecha(int ano, int mes, int dia) {
        // Setear en el textview la fecha
        DatePickerFragment f1=(DatePickerFragment)getSupportFragmentManager().findFragmentByTag("date_from");
        DatePickerFragment f2=(DatePickerFragment)getSupportFragmentManager().findFragmentByTag("date_to");
        String date = ano + "-" + (mes + 1) + "-" + dia;
        if (f1!=null || f2 !=null) {
            Calendar c = createDateCalendar(ano, mes, dia);
            if (f1 != null) {
                text_date_from.setText(ano + "-" + (mes + 1) + "-" + dia);
                from = c;
            } else if (f2 != null) {
                text_date_to.setText(ano + "-" + (mes + 1) + "-" + dia);
                to = c;
            }
        }
    }

    private void initGUI(){
        check_filter = (AppCompatCheckBox) findViewById(R.id.check_filter);
        check_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_filter.isChecked()){
                    text_date_from.setEnabled(false);
                    text_date_to.setEnabled(false);
                }else{
                    text_date_from.setEnabled(true);
                    text_date_to.setEnabled(true);
                }

            }
        });
        text_date_from = (EditText) findViewById(R.id.text_date_from);
        text_date_from.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        text_capture_line.setText("");
                        DialogFragment picker = new DatePickerFragment();
                        picker.show(getSupportFragmentManager(), "date_from");

                    }
                }
        );

        text_date_to = (EditText) findViewById(R.id.text_date_to);
        text_date_to.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        text_capture_line.setText("");
                        DialogFragment picker = new DatePickerFragment();
                        picker.show(getSupportFragmentManager(), "date_to");

                    }
                }
        );
        text_capture_line = (EditText) findViewById(R.id.text_capture_line);
        text_capture_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_date_from.setText("");
                text_date_to.setText("");
            }
        });



        pbLoading_update = (ProgressBar) findViewById(R.id.pbLoading_update);
        button_accept = (AppCompatButton) findViewById(R.id.button_accept);
        button_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;
                if (!check_filter.isChecked()) {
                    if (text_capture_line.getText().toString().length() > 0) {//validate capture line
                        if (!(text_capture_line.getText().toString().length() > 0)) {
                            valid = false;
                            text_capture_line.setError(getString(R.string.error_field_required));
                        } else {
                            valid = true;
                            text_capture_line.setError(null);
                        }
                    } else {


                if (!(text_date_from.getText().toString().trim().length()>0)){
                    valid = false;
                    text_date_from.setError(getString(R.string.error_field_required));
                }else{
                    valid = true;
                    text_date_from.setError(null);
                }
                if (!(text_date_to.getText().toString().trim().length()>0)){
                    valid = false;
                    text_date_to.setError(getString(R.string.error_field_required));
                }else{
                    valid = true;
                    text_date_to.setError(null);
                }
                if (from != null && to !=null){
                    if (!(from.getTimeInMillis()<=to.getTimeInMillis())){
                        valid = false;
                        text_date_from.setError(getString(R.string.error_field_not_valid));
                    }else{
                        valid = true;
                        text_date_from.setError(null);
                    }
                }

                    }
                }
                if (valid) {
                    Intent data = new Intent();
                    data.putExtra(FILTER, new Filter(text_date_from.getText().toString().trim(),text_date_to.getText().toString().trim(),check_filter.isChecked(),text_capture_line.getText().toString()));
                    setResult(Activity.RESULT_OK, data);
                    finish();
                }
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

    private Calendar createDateCalendar(int year,int month, int day){
        final Calendar c = Calendar.getInstance();
        c.set(year,month,day);
        return c;
    }
}
