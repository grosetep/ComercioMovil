package estrategiamovil.comerciomovil.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

/**
 * Created by administrator on 11/08/2016.
 */
public class NetworkStatus {

    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) { // connected to the internet
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    return true;

                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return true;

                }
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
