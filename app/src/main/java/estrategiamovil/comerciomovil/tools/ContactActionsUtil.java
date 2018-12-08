package estrategiamovil.comerciomovil.tools;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.Ask;
import estrategiamovil.comerciomovil.modelo.BusinessInfo;
import estrategiamovil.comerciomovil.modelo.LoginResponse;
import estrategiamovil.comerciomovil.web.VolleySingleton;

/**
 * Created by administrator on 15/12/2016.
 */
public class ContactActionsUtil {
    private static final String TAG = ContactActionsUtil.class.getSimpleName();
    public static void confirmAction(final Activity activity,String message, String title, String positive, String negative, final String phone) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setMessage(message);
        if (!title.equals(""))
            alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                try {
                    if (phone != null && !phone.equals(""))
                        makeCall(activity,phone);
                    else
                        Toast.makeText(activity, activity.getResources().getString(R.string.promt_error_phone),
                                Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(activity, "Problema con la llamada...",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        alertDialogBuilder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static void makeCall(Activity activity,String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone));
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            String[] PERMISSIONS = {Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE};
            if (!UtilPermissions.hasPermissions(activity, PERMISSIONS)) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS, UtilPermissions.PERMISSION_ALL);
            }

        }
        activity.startActivity(intent);
    }
    public static void ShowEmail_Popup(final Activity activity, final BusinessInfo businessInfo) {

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(activity);
        final View content = layoutInflaterAndroid.inflate(R.layout.contact_email_merchant_layout, null);
        final View mView = layoutInflaterAndroid.inflate(R.layout.dialog_template, null);
        LinearLayout fields = (LinearLayout)mView.findViewById(R.id.content);
        fields.addView(content);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(activity);

        alertDialogBuilderUserInput.setView(mView);
        //load information
        TextView text_company = (TextView)content.findViewById(R.id.text_company);
        final EditText text_name = (EditText) content.findViewById(R.id.text_name);
        final EditText text_email = (EditText)content.findViewById(R.id.text_email);
        final EditText text_phone = (EditText) content.findViewById(R.id.text_phone);
        final EditText text_message = (EditText) content.findViewById(R.id.text_message);

        text_company.setText(businessInfo.getCompany());

        //customize title
        ((TextView)mView.findViewById(R.id.text_title)).setText(activity.getResources().getString(R.string.promt_title_send_email));
        ((TextView)mView.findViewById(R.id.text_title)).setBackgroundColor(Color.parseColor(Constants.colorTitle));
        ((TextView)mView.findViewById(R.id.text_title)).setTextColor(Color.parseColor(Constants.colorBackgroundEnabled));
        //populate data if user is logged
        LoginResponse login = UserLocalProfile.getUserProfile(activity.getApplicationContext());
        if (login!=null){
            text_name.setText(login.getName());
            text_email.setText(login.getEmail());
            text_phone.setText(login.getPhone());
        }


        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(activity.getResources().getString(R.string.action_send_email), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })

                .setNegativeButton(activity.getResources().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
        Button b = alertDialogAndroid.getButton(AlertDialog.BUTTON_POSITIVE);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = true;

                if (text_name.getText().toString().isEmpty()){
                    text_name.setError("Campo obligatorio");
                    valid = false;
                } else {
                    text_name.setError(null);
                }


                if (text_email.getText().toString().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(text_email.getText().toString()).matches()) {
                    text_email.setError("Email inválido");
                    valid = false;
                } else {
                    text_email.setError(null);
                }


                if (text_message.getText().toString().isEmpty()) {
                    text_message.setError("Campo obligatorio");
                    valid = false;
                } else {
                    text_message.setError(null);
                }
                if (valid) {
                    Ask ask = new Ask();
                    ask.setName_client(text_name.getText().toString());
                    ask.setEmail_client(text_email.getText().toString());
                    ask.setPhone_client(text_phone.getText().toString());
                    ask.setMessage_client(text_message.getText().toString());
                    ask.setCompany_suscriptor(businessInfo.getCompany());
                    ask.setEmail_suscriptor(businessInfo.getEmail());
                    sendAskEmail(ask,alertDialogAndroid, activity);
                }

            }
        });
    }
    private static void sendAskEmail(Ask ask,AlertDialog dialog,Activity activity) {

        HashMap<String, String> params = new HashMap<>();
        params.put("method", "sendMessage");
        params.put("name_client", ask.getName_client());
        params.put("email_client", ask.getEmail_client());
        params.put("phone_client", ask.getPhone_client());
        params.put("message_client", ask.getMessage_client());
        params.put("company_suscriptor", ask.getCompany_suscriptor());
        params.put("email_suscriptor", ask.getEmail_suscriptor());
        VolleyPostRequest(Constants.EMAIL, params,dialog,activity);
    }
    private static void processEmailResponse(JSONObject response, AlertDialog dialog,Activity activity){
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");
            switch (status) {
                case "1":
                    dialog.dismiss();
                    showConfirmationMessage(activity.getResources().getString(R.string.prompt_email_ask),activity);
                    break;
                case "2":
                    showConfirmationMessage(activity.getResources().getString(R.string.prompt_email_sent_error),activity);
                    break;
                default:
                    showConfirmationMessage(activity.getResources().getString(R.string.prompt_email_sent_error),activity);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showConfirmationMessage(activity.getResources().getString(R.string.prompt_email_sent_error),activity);
        }
    }
    private static void showConfirmationMessage(final String message,final Activity activity){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity,message,Toast.LENGTH_LONG).show();
            }
        });
    }
    private static void VolleyPostRequest(String url, HashMap<String, String> params,final AlertDialog dialog,final Activity activity) {
        JSONObject jobject = new JSONObject(params);
        Log.d(TAG, "VolleyPostRequest:" + jobject.toString());

        VolleySingleton.getInstance(activity).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar la respuesta del servidor
                                processEmailResponse(response,dialog,activity);


                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley: " + error.getMessage() + "  " + error.getCause());
                                //dismissProgressDialog();
                                String mensaje2 = "Verifique su conexión a Internet.";
                                Toast.makeText(
                                        activity,
                                        mensaje2,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("Accept", "application/json");
                        return headers;
                    }


                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8" + getParamsEncoding();
                    }
                }
        );
    }
    public static void openWebSite(Activity activity, String link){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(link));
        activity.startActivity(i);
    }
}
