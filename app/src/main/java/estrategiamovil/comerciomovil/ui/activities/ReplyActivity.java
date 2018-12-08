package estrategiamovil.comerciomovil.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.support.v4.app.NotificationCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.notifications.MyFirebaseMessagingService;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.web.VolleySingleton;

public class ReplyActivity extends AppCompatActivity {
    private static final String TAG = "ReplyActivity";
    private static final String KEY_MESSAGE_ID = "key_message_id";
    private static final String KEY_NOTIFY_ID = "key_notify_id";
    private static final String KEY_MERCHANT_ID = "key_id_merchant";

    private int mMessageId;
    private int mNotifyId;
    private String merchantId;

    private ImageButton mSendButton;
    private EditText mEditReply;

    public static Intent getReplyMessageIntent(Context context, int notifyId, int messageId,String id_merchant) {
        Intent intent = new Intent(context, ReplyActivity.class);
        intent.setAction(MyFirebaseMessagingService.REPLY_ACTION);
        intent.putExtra(KEY_MESSAGE_ID, messageId);
        intent.putExtra(KEY_NOTIFY_ID, notifyId);
        intent.putExtra(KEY_MERCHANT_ID, id_merchant);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        Intent intent = getIntent();

        if (MyFirebaseMessagingService.REPLY_ACTION.equals(intent.getAction())) {
            mMessageId = intent.getIntExtra(KEY_MESSAGE_ID, 0);
            mNotifyId = intent.getIntExtra(KEY_NOTIFY_ID, 0);
            merchantId = intent.getStringExtra(KEY_MERCHANT_ID);
        }

        mEditReply = (EditText) findViewById(R.id.edit_reply);
        mSendButton = (ImageButton) findViewById(R.id.button_send);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //make request
                notifyMerchant();
            }
        });
    }

    private void sendMessage(int notifyId,  int messageId) {
        // update notification
        updateNotification(notifyId);

        String message = mEditReply.getText().toString().trim();
        // handle send message
        //Toast.makeText(this, "Message ID: " + messageId + "\nMessage: " + message,
              //  Toast.LENGTH_SHORT).show();


    }

    private void updateNotification(int notifyId) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        //updating notification after response
        String message = mEditReply.getText().toString().trim();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_cloud)
                .setColor(0x0288D1)
                .setAutoCancel(true)
                .setContentTitle("Respuesta enviada");

        notificationManager.notify(notifyId, builder.build());
    }
    private void notifyMerchant(){
        Log.d(TAG,"notifyMerchant---------->>");
        JSONObject data = new JSONObject();
        String message = mEditReply.getText().toString().trim();
        HashMap<String,String> params = new HashMap<>();
        String idUser = ApplicationPreferences.getLocalStringPreference(getApplicationContext(), Constants.localUserId);
        params.put("idMerchant",merchantId);//merchant to notify
        params.put("idCustomer", idUser);//customer
        params.put("message", message);//message to customer
        params.put("method", "notifyMerchant");
        try {
            data.put("type_notification",Constants.notifycation_types.get("merchant").getNotificationType());
            data.put("reply","0");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("process_data", data.toString());
        VolleyPostRequest(Constants.GET_CUSTOMERS, params);
    }
    public void VolleyPostRequest(String url, final HashMap<String, String> args){
        JSONObject jobject = new JSONObject(args);
        Log.d(TAG,"VolleyPostRequest:" +  jobject.toString());
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jobject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Procesar la respuesta del servidor
                        processResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        sendMessage(mNotifyId, mMessageId);
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
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
    private void processResponse(JSONObject response){
        Log.d(TAG, "response customers:"+response.toString());
        try {
            String status = response.getString("status");
            String result = response.toString().contains("message_id")?"1":"2";


            switch (result) {
                case "1":
                    sendMessage(mNotifyId, mMessageId);
                    finish();
                    break;
                case "2":
                    Log.d(TAG,"No se realizo la operacion.");
                    sendMessage(mNotifyId, mMessageId);
                    break;

                default:

                    break;

            }
        } catch (JSONException e) {
            e.printStackTrace();
            sendMessage(mNotifyId, mMessageId);
        }
    }

}