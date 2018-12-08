package estrategiamovil.comerciomovil.web;

/**
 * Creado por Hermosa Programación.
 */

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;


import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * Creado por Hermosa Programación.
 * <p>
 * Clase que representa un cliente HTTP Volley
 */

public final class VolleySingleton {
    private static final String TAG = VolleySingleton.class.getSimpleName();
    // Atributos
    private static VolleySingleton singleton;
    private RequestQueue requestQueue;
    private static Context context;
    private HurlStack mStack;

    private VolleySingleton(Context context) {
        VolleySingleton.context = context;
        requestQueue = getRequestQueue();
    }

    /**
     * Retorna la instancia unica del singleton
     *
     * @param context contexto donde se ejecutarán las peticiones
     * @return Instancia
     */
    public static synchronized VolleySingleton getInstance(Context context) {
        if (singleton == null) {
            singleton = new VolleySingleton(context.getApplicationContext());
        }
        return singleton;
    }

    /**
     * Obtiene la instancia de la cola de peticiones
     *
     * @return cola de peticiones
     */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            mStack = new HurlStack(null, createSslSocketFactory());
            if (mStack!=null) {
                requestQueue = Volley.newRequestQueue(context.getApplicationContext(), mStack, -1);
            }

        }
        return requestQueue;
    }
    private static SSLSocketFactory createSslSocketFactory() {
        TrustManager[] byPassTrustManagers = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                try {
                    //Log.d(TAG,"Checking Certificate validity ..............");
                    chain[0].checkValidity();
                    //Log.d(TAG,"ok ..............");
                } catch (Exception e) {
                    throw new CertificateException("WeeSpare Certificate not valid or trusted.");
                }
            }
        }};

        SSLContext sslContext = null;
        SSLSocketFactory sslSocketFactory = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, byPassTrustManagers, new SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();

        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return sslSocketFactory;
    }
    /**
     * Añade la petición a la cola
     *
     * @param req petición
     * @param <T> Resultado final de tipo T
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}