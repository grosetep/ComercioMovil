package estrategiamovil.comerciomovil.tools;

import android.content.Context;
import android.util.Log;

import com.mercadopago.util.MercadoPagoUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import estrategiamovil.comerciomovil.modelo.Section;

/**
 * Created by administrator on 19/08/2016.
 */
public class StringOperations {
    private static final String TAG = StringOperations.class.getSimpleName();
    public static String getStringBeforeDelimiter(String original, String delimiter){
        String value = "";

        int index = original.indexOf(delimiter);
        value = original.substring(0,index);
        return value;
    }
    public static String getAmountFormat(String amount){
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
        return format.format(Double.parseDouble(amount));
    }
    public static String getAmountFormatWithNoDecimals(String amount){
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
        format.setMaximumFractionDigits(0);
        return format.format(Double.parseDouble(amount));
    }
    public static String getStringWithCashSymbol(String amount){
        return "$ "+amount;
    }
    public static String getPercentageFormat(String number){
        return number.concat(" %");
    }
    public static String getStringWithDe(String s){return "De: " + s;}
    public static String getStringWithA(String s){return "A: " + s;}
    public static String getDecimalFormat(String amount){
        DecimalFormat df = new DecimalFormat();
        Float number = 0f;
        try {
            number = Float.parseFloat(amount);
            df.setMaximumFractionDigits(2);
        }catch(NumberFormatException e){
            return "0.00";
        }
        return df.format(number);
    }
    public static String getDecimalFormatWithNoDecimals(String amount){
        DecimalFormat df = new DecimalFormat();
        Float number = 0f;
        try {
            number = Float.parseFloat(amount);
            df.setMaximumFractionDigits(0);
        }catch(NumberFormatException e){
            return "0.00";
        }
        return df.format(number);
    }
    public static JSONArray getJSONArrayFromArray(Section[] sections){
        JSONArray array = new JSONArray();
        try {
            for (Section s : sections) {
                //tabsNames.add(s.getSection());
                JSONObject o = new JSONObject();
                o.put("idSection", s.getIdSection());
                o.put("section", s.getSection());
                o.put("emergent", s.getEmergent());
                array.put(o);
            }
            JSONObject objects = new JSONObject();
            objects.put("sections", array);
            return array;
        }catch (JSONException e) {
            return null;
        }
    }
    public static Date getDate( String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Date d = sdf.parse(date);
            return d;
        } catch (ParseException e) {
            Log.d(TAG,"error:"+e.getMessage());
            return null;
        }
    }
}
