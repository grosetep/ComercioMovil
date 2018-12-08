package estrategiamovil.comerciomovil.notifications;

/**
 * Created by administrator on 25/08/2016.
 */
/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import estrategiamovil.comerciomovil.modelo.GenericResponse;
import estrategiamovil.comerciomovil.tools.ApplicationPreferences;
import estrategiamovil.comerciomovil.tools.Constants;
import estrategiamovil.comerciomovil.web.VolleySingleton;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        ApplicationPreferences.saveLocalPreference(getApplicationContext(), Constants.firebase_token, refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        String idUser = ApplicationPreferences.getLocalStringPreference(getApplicationContext(),Constants.localUserId);
        HashMap<String,String> params = new HashMap<>();
        params.put("method","registerDevice");
        params.put("token",token);
        params.put("idUser",idUser);
        params.put("already_registered",idUser!=null && idUser.length()>0?"true":"false");
        VolleyPostRequest(Constants.NOTIFICATIONS,params);
    }
    public void VolleyPostRequest(String url, final HashMap<String, String> args){
        JSONObject jobject = new JSONObject(args);
        Log.d(TAG,"VolleyPostRequest:" +  jobject.toString());

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(
                new JsonObjectRequest(
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
                                Log.d(TAG, "Error Volley: " + error.getMessage() + "  " + error.getCause());
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
    private void processResponse(JSONObject response){
        Log.d(TAG, "Register device:"+response.toString());
        try {
            String status = response.getString("status");
            switch (status) {
                case "1":
                    Gson gson = new Gson();
                    JSONObject object = response.getJSONObject("result");

                    if (object.getString("status").equals("1")) {
                        GenericResponse result = gson.fromJson(object.toString(), GenericResponse.class);
                    }else {
                        Log.d(TAG,"Token generado pero no almacenado...");


                    }
                    break;
                case "2":
                    Log.d(TAG,"Token generado pero no almacenado...");

                    break;

                default:
                    break;

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG,"Token generado pero no almacenado...");
        }
    }
}