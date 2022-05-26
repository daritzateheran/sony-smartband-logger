package cl.felipebarriga.android.smartbandlogger;

import android.content.Context;
import android.content.SharedPreferences;

import cl.felipebarriga.android.utils.User;

public class caregiversPreferences {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREF_NAME = "caregivers";
    String SESSION_KEY = "caregiverList";

    public caregiversPreferences(Context context){
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    public void addCaregiver(caregiverManager caregiver){
        //save session of user whenever user is logged in
        String caregiverInfo  = caregiver.toString(); //Obtener el vector con el ; datos listos para ingresar
        editor.putString(SESSION_KEY,caregiverInfo).commit();
    }

    public String getcaregiverList(){
        //return user id whose session is saved
        return sharedPreferences.getString(SESSION_KEY, "");
    }

}

