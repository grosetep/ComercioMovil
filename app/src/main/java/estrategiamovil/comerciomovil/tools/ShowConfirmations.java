package estrategiamovil.comerciomovil.tools;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.ui.interfaces.DialogCallbackInterface;

/**
 * Created by administrator on 10/11/2016.
 */
public class ShowConfirmations {
    private static final String TAG = ShowConfirmations.class.getSimpleName();
    private Activity activity;
    private DialogCallbackInterface actions;
    public static AlertDialog alertDialog;
    public ShowConfirmations(Activity activity, DialogCallbackInterface action){
        this.activity = activity;
        this.actions = action;
    }

    public static void showConfirmationMessage(final String message,final Activity activity){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity,message,Toast.LENGTH_LONG).show();
            }
        });
    }
    public void openRetry(){
        ApplicationPreferences.saveLocalPreference(activity,Constants.CONNECTIVITY_DIALOG_SHOWED,"1");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setMessage(activity.getResources().getString(R.string.no_connection));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("REINTENTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if(turnOffFlag())
                    actions.method_positive();

            }
        });

        alertDialogBuilder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(turnOffFlag())
                    actions.method_negative(activity);
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
    public static boolean existDialogAlertRetry(){
        if (alertDialog!=null){
            if (alertDialog.isShowing()) return true;
            else return false;
        }else{
            return false;
        }
    }
    public void openSimple(){
        ApplicationPreferences.saveLocalPreference(activity,Constants.CONNECTIVITY_DIALOG_SHOWED,"1");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setMessage(activity.getResources().getString(R.string.no_connection));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("ANULAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if(turnOffFlag()) {
                    actions.method_positive();
                }
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
    private boolean turnOffFlag(){
        ApplicationPreferences.saveLocalPreference(activity,Constants.CONNECTIVITY_DIALOG_SHOWED,"0");
        return true;
    }
    public static void ShowNotification_Popup(final Activity activity,String title,String message,final String constantToUpdate) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(activity);
        final View mView = layoutInflaterAndroid.inflate(R.layout.dialog_show_tip, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(activity);
        alertDialogBuilderUserInput.setView(mView);

        TextView text_tip_message = (TextView) mView.findViewById(R.id.text_tip_message);
        TextView dialogTitle = (TextView) mView.findViewById(R.id.dialogTitle);
        if(message!=null && message.length()>0) text_tip_message.setText(message);
        if (title!=null && title.length()>0) dialogTitle.setText(title);
        //listeners
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(activity.getResources().getString(R.string.promt_button_show_tip_image), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        //action buttons

                            final CheckBox checkbox_show_tip = (CheckBox) mView.findViewById(R.id.checkbox_show_tip);
                            if (checkbox_show_tip.isChecked())
                                ApplicationPreferences.saveLocalPreference(activity,constantToUpdate, String.valueOf(true));
                    }
                });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }


    public static void ShowNotification_Popup_Hightlight(final Activity activity,String title,String message, final String constantToUpdate) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(activity);
        final View content = layoutInflaterAndroid.inflate(R.layout.layout_show_tip, null);
        final View mView = layoutInflaterAndroid.inflate(R.layout.dialog_template, null);
        LinearLayout fields = (LinearLayout)mView.findViewById(R.id.content);
        fields.addView(content);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(activity);
        alertDialogBuilderUserInput.setView(mView);

        final TextView text_message = (TextView) mView.findViewById(R.id.text_message);
        final TextView text_error = (TextView) mView.findViewById(R.id.text_error);
        final CheckBox checkbox_show_tip = (CheckBox) mView.findViewById(R.id.checkbox_show_tip);
        //customize title
        ((TextView)mView.findViewById(R.id.text_title)).setText(title);
        ((TextView)mView.findViewById(R.id.text_title)).setBackgroundColor(Color.parseColor(Constants.colorTitle));
        ((TextView)mView.findViewById(R.id.text_title)).setTextColor(Color.parseColor(Constants.colorBackgroundEnabled));
        AppCompatButton button = (AppCompatButton) mView.findViewById(R.id.button_done);
        //set content
        text_message.setText(message);
        text_error.setText("");

        final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (checkbox_show_tip.isChecked()) {
                   ApplicationPreferences.saveLocalPreference(activity, constantToUpdate, String.valueOf(true));
               }
                alertDialogAndroid.dismiss();
            }
        });

    }
}
