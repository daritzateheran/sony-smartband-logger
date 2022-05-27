package cl.felipebarriga.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class session {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREF_NAME = "sessionKey";
    String SESSION_KEY = "Key_session";
    String SESSION_CAREGIVER = "Caregiver_session";

    public session(Context context){
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


    public void saveSession(User user){
        //save session of user whenever user is logged in
        String Key = user.getKey();
        editor.putString(SESSION_KEY,Key).commit();
    }

    public String getSession(){
        //return user id whose session is saved
        return sharedPreferences.getString(SESSION_KEY, "-1");
    }

    public void removeSession(){
        editor.putString(SESSION_KEY,"-1").commit();
    }

    public void removeCaregiver(){
        editor.putString(SESSION_CAREGIVER,"-1").commit();
    }


    public String getCaregiver(){
        //return user id whose session is saved
        return sharedPreferences.getString(SESSION_CAREGIVER, "-1");
    }

    public void setCaregiver(String c){
        editor.putString(SESSION_CAREGIVER,c).commit();
    }
}
