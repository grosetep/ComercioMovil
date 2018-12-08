package estrategiamovil.comerciomovil.tools;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Iterator;
import java.util.Set;

import estrategiamovil.comerciomovil.modelo.LoginResponse;

/**
 * Created by administrator on 05/10/2016.
 */
public class FireBaseOperations {
    private static final String TAG = FireBaseOperations.class.getSimpleName();
    public static  void subscribeUser(Context context, boolean flag){
        Log.d(TAG,"subscribeUser...."+flag);
        LoginResponse login = UserLocalProfile.getUserProfile(context);
        String idUser = ApplicationPreferences.getLocalStringPreference(context,Constants.localUserId);
        String firebase_token = ApplicationPreferences.getLocalStringPreference(context,Constants.firebase_token);
        Log.d(TAG,"firebase_token:"+firebase_token);
        if (firebase_token!=null){
            // [START subscribe_topics]
            Set<String> topics = ApplicationPreferences.getLocalSetPreference(context,Constants.topicsUser);
            if (topics!=null){//subscribe to each topic
                Iterator iter = topics.iterator();
                while (iter.hasNext()){
                    String topic = (String)iter.next();
                    //Log.d(TAG,"subscribe to Topic:" + topic +" ------- "+ ((flag)?"true":"false"));
                    if (flag)
                        FirebaseMessaging.getInstance().subscribeToTopic(topic);
                    else
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);

                }
            }
            if (flag) {
                Log.d(TAG, "subscribe to Topic:" + Constants.topicEmergent);
                FirebaseMessaging.getInstance().subscribeToTopic(Constants.topicEmergent);
                if (idUser != null && idUser.length()>0 && login!=null && login.getIdUserType()!=null ){//user logged
                    if (login.getIdUserType().equals(Constants.user_type_merchant)){
                        Log.d(TAG, "userType= "+ login.getIdUserType()+" ....subscribe to Topic:" + Constants.topicMerchant);
                        FirebaseMessaging.getInstance().subscribeToTopic(Constants.topicMerchant);
                    }else{
                        Log.d(TAG, "userType= "+ login.getIdUserType()+" ....UN subscribe to Topic:" + Constants.topicMerchant);
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.topicMerchant);}
                }else{
                    //reset to avoid inconsistence
                    UserLocalProfile.deleteUserProfile(context);
                    ApplicationPreferences.saveLocalPreference(context,Constants.localUserId,"");
                    Log.d(TAG, "user no logged, delete localProfile..");
                }
            }else{
                Log.d(TAG, "un subscribe to Topic:" + Constants.topicMerchant);
                FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.topicEmergent);
                FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.topicMerchant);
            }
            // [END subscribe_topics]
        }else{
            Log.d(TAG,"User already subscribed to emergent  :)");
        }

    }
}
