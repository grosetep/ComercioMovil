package estrategiamovil.comerciomovil.tools;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import estrategiamovil.comerciomovil.modelo.CategoryResultFilter;

public class JsonParse {
    private static final String TAG = JsonParse.class.getSimpleName();
    double current_latitude,current_longitude;
    public JsonParse(){}
    public JsonParse(double current_latitude,double current_longitude){
        this.current_latitude=current_latitude;
        this.current_longitude=current_longitude;
    }
    public List<String> getParseJsonWCF(String sName)
    {
        List<String> ListData = new ArrayList<String>();
        try {
            String temp=sName.replace(" ", "%20");
            URL js = new URL(Constants.GET_CATEGORIES+"?method=getCategoriesByDescription&description="+temp);
            URLConnection jc = js.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(jc.getInputStream()));
            String line = reader.readLine();
            JSONObject jsonResponse = new JSONObject(line);
            String status = jsonResponse.getString("status");
            if (status!=null && status.equals("1")) {
                JSONArray jsonArray = jsonResponse.getJSONArray("result");
                Gson gson = new Gson();
                CategoryResultFilter[] categories = gson.fromJson(jsonArray.toString(), CategoryResultFilter[].class);
                if (categories != null)
                    for (int i = 0; i < categories.length; i++) {
                        String subsubcategory = categories[i].getSubsubcategory()!=null && categories[i].getSubsubcategory().length()>0?categories[i].getSubsubcategory():"";
                        ListData.add(categories[i].getSubcategory().concat(!subsubcategory.isEmpty()?"->"+subsubcategory:""));
                    }
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return ListData;

    }


}
