package estrategiamovil.comerciomovil.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import estrategiamovil.comerciomovil.modelo.LoginResponse;

/**
 * Created by administrator on 23/07/2016.
 */
public class UserLocalProfile {
    private static final String TAG = UserLocalProfile.class.getSimpleName();

    public static void saveSessionValuesUser(Context context, LoginResponse response) {
        Log.d(TAG, "saveSessionValuesUser----------------------------");
        //insert if not exists, update if exists
        UserSQLiteHelper userDbHelper = new UserSQLiteHelper(context, "DBUser", null, Constants.SQLiteDatabase_version);
        SQLiteDatabase db_user = userDbHelper.getReadableDatabase();
        String[] args = new String[]{response.getIdUsuario()};
        Cursor c = db_user.rawQuery(" SELECT * FROM user where idUsuario = ?", args);
        ContentValues newRow = new ContentValues();
        if (c.moveToFirst()) {//informacion del usuario ya existe, se procede a la actualizacion
            Log.d(TAG, "saveSessionValuesUser--------actualizacion");
            newRow.put("idPersona", response.getIdPersona());
            newRow.put("name", response.getName());
            newRow.put("first", response.getFirst());
            newRow.put("last", response.getLast());
            newRow.put("phone", response.getPhone());
            newRow.put("company", response.getCompany());
            newRow.put("website", response.getWebsite());
            newRow.put("address", response.getAddress());
            newRow.put("idUsuario", response.getIdUsuario());
            newRow.put("email", response.getEmail());
            newRow.put("status", response.getStatus());
            newRow.put("avatarPath", response.getAvatarPath());
            newRow.put("administrator", response.getAdministrator());
            newRow.put("idUserType", response.getIdUserType());
            newRow.put("userType", response.getUserType());
            newRow.put("idPersonType", response.getIdPersonType());
            newRow.put("personType", response.getPersonType());
            newRow.put("remotePath", response.getRemotePath());
            newRow.put("remoteImage", response.getRemoteImage());
            db_user.update(Constants.user_table, newRow, null, null);
        } else {//no existe la informacion, se crea por primera vez..
            Log.d(TAG, "saveSessionValuesUser--------crear registro primera vez");
            newRow.put("idPersona", response.getIdPersona());
            newRow.put("name", response.getName());
            newRow.put("first", response.getFirst());
            newRow.put("last", response.getLast());
            newRow.put("phone", response.getPhone());
            newRow.put("company", response.getCompany());
            newRow.put("website", response.getWebsite());
            newRow.put("address", response.getAddress());
            newRow.put("idUsuario", response.getIdUsuario());
            newRow.put("email", response.getEmail());
            newRow.put("status", response.getStatus());
            newRow.put("avatarPath", response.getAvatarPath());
            newRow.put("administrator", response.getAdministrator());
            newRow.put("idUserType", response.getIdUserType());
            newRow.put("userType", response.getUserType());
            newRow.put("idPersonType", response.getIdPersonType());
            newRow.put("personType", response.getPersonType());
            newRow.put("remotePath", response.getRemotePath());
            newRow.put("remoteImage", response.getRemoteImage());
            db_user.insert(Constants.user_table, null, newRow);
        }
        db_user.close();
    }
    public static void updateSessionValuesUser(Context context, LoginResponse response) {
        Log.d(TAG, "updateSessionValuesUser----------------------------");
        //insert if not exists, update if exists
        UserSQLiteHelper userDbHelper = new UserSQLiteHelper(context, "DBUser", null, Constants.SQLiteDatabase_version);
        SQLiteDatabase db_user = userDbHelper.getReadableDatabase();
        String[] args = new String[]{response.getIdUsuario()};
        Cursor c = db_user.rawQuery(" SELECT * FROM user where idUsuario = ?", args);
        ContentValues newRow = new ContentValues();
        if (c.moveToFirst()) {//informacion del usuario ya existe, se procede a la actualizacion
            Log.d(TAG, "updateSessionValuesUser--------actualizacion");
            newRow.put("phone", response.getPhone());
            newRow.put("company", response.getCompany());
            newRow.put("website", response.getWebsite());
            newRow.put("address", response.getAddress());
            newRow.put("status", response.getStatus());
            newRow.put("idUserType", response.getIdUserType());
            newRow.put("userType", response.getUserType());
            db_user.update(Constants.user_table, newRow, null, null);
        }
        db_user.close();
    }

    public static LoginResponse getUserProfile(Context context) {
        LoginResponse profile = null;
        Log.d(TAG, "getUserProfile----------------------------");
        UserSQLiteHelper userDbHelper = new UserSQLiteHelper(context, "DBUser", null, Constants.SQLiteDatabase_version);
        SQLiteDatabase db_user = userDbHelper.getReadableDatabase();
        String localUserId = ApplicationPreferences.getLocalStringPreference(context, Constants.localUserId);
        Log.d(TAG, "localUserId----------------------------" + localUserId);
        if (localUserId != null && !localUserId.equals("")) {
            String[] args = new String[]{localUserId};
            Cursor c = db_user.rawQuery(" SELECT * FROM user where idUsuario = ?", args);
            if (c.moveToFirst()) {
                profile = new LoginResponse();
                profile.setIdPersona(c.getString(c.getColumnIndex("idPersona")));
                profile.setName(c.getString(c.getColumnIndex("name")));
                profile.setFirst(c.getString(c.getColumnIndex("first")));
                profile.setLast(c.getString(c.getColumnIndex("last")));
                profile.setPhone(c.getString(c.getColumnIndex("phone")));
                profile.setCompany(c.getString(c.getColumnIndex("company")));
                profile.setWebsite(c.getString(c.getColumnIndex("website")));
                profile.setAddress(c.getString(c.getColumnIndex("address")));
                profile.setIdUsuario(c.getString(c.getColumnIndex("idUsuario")));
                profile.setEmail(c.getString(c.getColumnIndex("email")));
                profile.setStatus(c.getString(c.getColumnIndex("status")));
                profile.setAvatarPath(c.getString(c.getColumnIndex("avatarPath")));
                profile.setAdministrator(c.getString(c.getColumnIndex("administrator")));
                profile.setIdUserType(c.getString(c.getColumnIndex("idUserType")));
                profile.setUserType(c.getString(c.getColumnIndex("userType")));
                profile.setIdPersonType(c.getString(c.getColumnIndex("idPersonType")));
                profile.setPersonType(c.getString(c.getColumnIndex("personType")));
                profile.setRemotePath(c.getString(c.getColumnIndex("remotePath")));
                profile.setRemoteImage(c.getString(c.getColumnIndex("remoteImage")));
            }
        }
        if (profile != null)
            Log.d(TAG, "profile user:" + profile.toString());
        else
            Log.d(TAG, "No hay datos de usaurios logueados..." + profile);
        db_user.close();
        return profile;
    }

    public static int deleteUserProfile(Context context) {
        Log.d(TAG, "getUserProfile----------------------------");
        int result = 0;
        UserSQLiteHelper userDbHelper = new UserSQLiteHelper(context, "DBUser", null, Constants.SQLiteDatabase_version);
        SQLiteDatabase db_user = userDbHelper.getReadableDatabase();
        String localUserId = ApplicationPreferences.getLocalStringPreference(context, Constants.localUserId);
        if (localUserId != null && !localUserId.equals("")) {
            String[] args = new String[]{localUserId};
            result = db_user.delete("user", "idUsuario = ?", args);
        }
        db_user.close();
        return result;
    }
}
