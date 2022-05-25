package cl.felipebarriga.android.smartbandlogger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import cl.felipebarriga.android.utils.User;
import cl.felipebarriga.android.utils.session;


public class KeyId extends Activity {


    private static final String LOG_TAG = "SmartBandLogger";
    private final String CLASS = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, CLASS + " onCreate: called");
        setContentView(R.layout.keyid);
    }
    @Override
    protected void onStart() {
        super.onStart();
        checkSession();
    }

    private void checkSession() {
        //check if user is logged in
        //if user is logged in --> move to mainActivity
        session sessionManagement = new session(KeyId.this);
        int userID = sessionManagement.getSession();
        if(userID != -1){
            //user id logged in and so move to mainActivity
            moveToMainActivity();
        }
        else{
            //do nothing
        }
    }

    private void moveToMainActivity() {
        Intent intent = new Intent(KeyId.this, MainActivity.class);
        startActivity(intent);
    }

    public void login(View view) {
        // 1.log in to app and save session of user
        // 2. move to mainActivity

        //1. login and save session
        User user = new User(1234,"Daritza Teheran");
        session sessionManagement = new session(KeyId.this);
        sessionManagement.saveSession(user);

        //2. step
        moveToMainActivity();
    }
}
