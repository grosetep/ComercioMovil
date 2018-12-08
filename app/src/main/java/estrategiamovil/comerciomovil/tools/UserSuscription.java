package estrategiamovil.comerciomovil.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import estrategiamovil.comerciomovil.modelo.LoginResponse;
import estrategiamovil.comerciomovil.modelo.SuscriptionDetail;

/**
 * Created by administrator on 28/09/2016.
 */
public class UserSuscription {
    private static final String TAG = UserSuscription.class.getSimpleName();

    public static void saveSuscriptionDetails(Context context, SuscriptionDetail response) {
        //Log.d(TAG, "saveSuscriptionDetails----------------------------");
        //insert if not exists, update if exists
        UserSQLiteHelper userDbHelper = new UserSQLiteHelper(context, "DBUser", null, Constants.SQLiteDatabase_version);
        SQLiteDatabase db_user = userDbHelper.getReadableDatabase();
        String[] args = new String[]{response.getIdUser()};
        Cursor c = db_user.rawQuery(" SELECT * FROM suscription where idUser = ?", args);
        ContentValues newRow = new ContentValues();
        if (c.moveToFirst()) {//informacion del usuario ya existe, se procede a la actualizacion
            Log.d(TAG, "saveSuscriptionDetails--------actualizacion");
            newRow.put("idUser", response.getIdUser());
            newRow.put("valid", response.getValid());
            newRow.put("days_active", response.getDays_active());
            newRow.put("date_active", response.getDate_active());
            newRow.put("status_suscription", response.getStatus_suscription());
            newRow.put("price_suscription", response.getPrice_suscription());
            newRow.put("price_cupons", response.getPrice_cupons());
            newRow.put("days_total", response.getDays_total());

            newRow.put("suscription_type", response.getSuscription_type());
            newRow.put("suscription_desc", response.getSuscription_desc());
            newRow.put("exist_suscription_ads", response.getExist_suscription_ads());
            newRow.put("status", response.getStatus());
            newRow.put("message", response.getMessage());
            db_user.update(Constants.suscription_table, newRow, null, null);
        } else {//no existe la informacion, se crea por primera vez..
            Log.d(TAG, "saveSuscriptionDetails--------crear registro primera vez");
            newRow.put("idUser", response.getIdUser());
            newRow.put("valid", response.getValid());
            newRow.put("days_active", response.getDays_active());
            newRow.put("date_active", response.getDate_active());
            newRow.put("status_suscription", response.getStatus_suscription());
            newRow.put("price_suscription", response.getPrice_suscription());
            newRow.put("price_cupons", response.getPrice_cupons());
            newRow.put("days_total", response.getDays_total());

            newRow.put("suscription_type", response.getSuscription_type());
            newRow.put("suscription_desc", response.getSuscription_desc());
            newRow.put("exist_suscription_ads", response.getExist_suscription_ads());
            newRow.put("status", response.getStatus());
            newRow.put("message", response.getMessage());
            db_user.insert(Constants.suscription_table, null, newRow);
        }
        db_user.close();
    }

    public static SuscriptionDetail getSuscriptionDetails(Context context) {
        SuscriptionDetail detail = null;
        //Log.d(TAG, "getSuscriptionDetails----------------------------");
        UserSQLiteHelper userDbHelper = new UserSQLiteHelper(context, "DBUser", null, Constants.SQLiteDatabase_version);
        SQLiteDatabase db_user = userDbHelper.getReadableDatabase();
        String localUserId = ApplicationPreferences.getLocalStringPreference(context, Constants.localUserId);
        if (localUserId != null && !localUserId.equals("")) {
            String[] args = new String[]{localUserId};
            Cursor c = db_user.rawQuery(" SELECT * FROM suscription where idUser = ?", args);
            if (c.moveToFirst()) {
                detail = new SuscriptionDetail();
                detail.setIdUser(c.getString(c.getColumnIndex("idUser")));
                detail.setValid(c.getString(c.getColumnIndex("valid")));
                detail.setDays_active(c.getString(c.getColumnIndex("days_active")));
                detail.setDate_active(c.getString(c.getColumnIndex("date_active")));
                detail.setStatus_suscription(c.getString(c.getColumnIndex("status_suscription")));
                detail.setPrice_suscription(c.getString(c.getColumnIndex("price_suscription")));
                detail.setPrice_cupons(c.getString(c.getColumnIndex("price_cupons")));
                detail.setDays_total(c.getString(c.getColumnIndex("days_total")));

                detail.setSuscription_type(c.getString(c.getColumnIndex("suscription_type")));
                detail.setSuscription_desc(c.getString(c.getColumnIndex("suscription_desc")));
                detail.setExist_suscription_ads(c.getString(c.getColumnIndex("exist_suscription_ads")));
                detail.setStatus(c.getString(c.getColumnIndex("status")));
                detail.setMessage(c.getString(c.getColumnIndex("message")));
            }
        }
        if (detail != null)
            Log.d(TAG, "detail suscription:" + detail.toString());
        else
            Log.d(TAG, "No hay datos de suscripcion..." + detail);
        db_user.close();
        return detail;
    }

    public static int deleteSuscriptionDetails(Context context) {
        Log.d(TAG, "deleteSuscriptionDetails----------------------------");
        int result = 0;
        UserSQLiteHelper userDbHelper = new UserSQLiteHelper(context, "DBUser", null, Constants.SQLiteDatabase_version);
        SQLiteDatabase db_user = userDbHelper.getReadableDatabase();
        String localUserId = ApplicationPreferences.getLocalStringPreference(context, Constants.localUserId);
        if (localUserId != null && !localUserId.equals("")) {
            String[] args = new String[]{localUserId};
            result = db_user.delete(Constants.suscription_table, "idUser = ?", args);
        }
        db_user.close();
        return result;
    }

}
