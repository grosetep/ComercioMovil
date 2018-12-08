package estrategiamovil.comerciomovil.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * Created by administrator on 18/07/2016.
 */
public class UserSQLiteHelper extends SQLiteOpenHelper {
    String query = "CREATE TABLE user(idPersona TEXT,name TEXT,first TEXT,last TEXT,phone TEXT,company TEXT,website TEXT,address TEXT,idUsuario TEXT,email TEXT,status TEXT,avatarPath TEXT, administrator INTEGER,idUserType INTEGER,userType TEXT,idPersonType INTEGER,personType TEXT,remotePath TEXT,remoteImage TEXT)";
    String suscription = "CREATE TABLE suscription(idUser TEXT,valid TEXT,days_active TEXT,date_active TEXT,status_suscription TEXT,price_suscription TEXT,price_cupons TEXT,days_total TEXT,suscription_type TEXT,suscription_desc TEXT,exist_suscription_ads TEXT,status TEXT,message TEXT)";

    public UserSQLiteHelper(Context context,String dbname,CursorFactory factory,int version){
        super(context,dbname,factory,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(query);
        db.execSQL(suscription);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS suscription");
        //table new version
        db.execSQL(query);
        //table new version
        db.execSQL(suscription);
    }
}
