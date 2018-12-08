package estrategiamovil.comerciomovil.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by administrator on 17/07/2016.
 */
public class ApplicationPreferences {
    private static SharedPreferences mPrefs;
    public ApplicationPreferences(){}
    public static void saveLocalPreference(Context context,String propertyName, String propertyValue) {
        if (mPrefs==null)
            mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(propertyName, propertyValue);
        editor.commit();
    }

    public static String getLocalStringPreference(Context context,String namePreference){
        if (mPrefs == null)
            mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return mPrefs.getString(namePreference,"");
    }
    public static Set<String> getLocalSetPreference(Context context,String namePreference){
        if (mPrefs == null)
            mPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        return mPrefs.getStringSet(namePreference,null);
    }

    public static void saveLocalSetPreference(Context context, String propertyName, Set<String> propertyValue) {
        if (mPrefs==null)
            mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putStringSet(propertyName, propertyValue);
        editor.commit();
    }

    public static HashMap<String, String> getParametersRecommendedSection(Context context){
        HashMap<String, String> params = new HashMap<>();
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String idCountry = mPrefs.getString(Constants.countryUser, "");
        String idCity = mPrefs.getString(Constants.cityUser,"");
        String listCategories= mPrefs.getString(Constants.categoriesUser,"");
        String nameCity = mPrefs.getString(Constants.nameCityUser,"Cambiar ciudad...");
        params.put("idCountry", idCountry);
        params.put("idCity", idCity);
        params.put("listCategories", listCategories);
        params.put("cityName",nameCity);
        params.put(Constants.search_flow,"true");
        return params;
    }
}
