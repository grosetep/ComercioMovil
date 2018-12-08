package estrategiamovil.comerciomovil.ui.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.HashMap;
import java.util.Map;

import estrategiamovil.comerciomovil.R;

public class FCMPluginActivity extends AppCompatActivity {

    private static String TAG = "FCMPlugin";
    /*
  * this activity will be started if the user touches a notification that we own.
  * We send it's data off to the push plugin for processing.
  * If needed, we boot up the main activity to kickstart the application.
  * @see android.app.Activity#onCreate(android.os.Bundle)
  */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fcmplugin_activity);
        Log.d(TAG, "==> FCMPluginActivity onCreate");

        Map<String, Object> data = new HashMap<String, Object>();
        if (getIntent().getExtras() != null) {
            Log.d(TAG, "==> USER TAPPED NOTFICATION");
            data.put("wasTapped", true);
            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);
                Log.d(TAG, "\tKey: " + key + " Value: " + value);
                data.put(key, value);
            }
        }

       // FCMPlugin.sendPushPayload(data); //enviar los datos de la notificacion

        finish();

        forceMainActivityReload();



    }

    private void forceMainActivityReload() {
        PackageManager pm = getPackageManager();
        Intent launchIntent = pm.getLaunchIntentForPackage(getApplicationContext().getPackageName());
        startActivity(launchIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "==> FCMPluginActivity onResume");
        final NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "==> FCMPluginActivity onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "==> FCMPluginActivity onStop");
    }
}
