package cl.felipebarriga.android.smartbandlogger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import cl.felipebarriga.android.utils.User;
import cl.felipebarriga.android.utils.session;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class KeyId extends Activity {


    private static final String LOG_TAG = "SmartBandLogger";
    private final String CLASS = getClass().getSimpleName();
    private OkHttpClient client = new OkHttpClient();


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
        String userKey = sessionManagement.getSession();

        if(userKey !="-1"){
            //user id logged in and so move to mainActivity
            moveToMainActivity();
        }
        else{
            //do nothing
        }
    }

    private void moveToMainActivity() {
        Intent intent = new Intent(KeyId.this, FirstCaregiver.class);
        startActivity(intent);
    }

    public void login(View view) {
        // 1.Read Text view Idkey and identificatio
        // 2. Post to server to validate that Idkey exist
        // 2. log in to app and save session of user
        // 3. move to mainActivity

        //1. Read textview
        TextView KeyView = (TextView) findViewById( R.id.Key);
        TextView idView = (TextView) findViewById( R.id.id);
        String Key = KeyView.getText().toString();
        String id = idView.getText().toString();

        // 2. Post to server

        RequestBody form = new FormBody.Builder().add("Key", Key).build();
        //Request request = new Request.Builder().url("http://10.20.35.106:3000/sign").post(form).build();
        Request request = new Request.Builder().url("http://3.16.124.69:3000/sign").post(form).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
             //   Log.w("server", "falló");
                Log.d(LOG_TAG, CLASS + ": SERVER IS NOT WORKING");

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String r = response.body().string();
                Log.d(LOG_TAG, CLASS + "response " + r);

                if(r.equals(id)){
                    // 3.login and save session
                    User user = new User(Integer.parseInt(id),Key);
                    session sessionManagement = new session(KeyId.this);
                    sessionManagement.saveSession(user);
                    //2. step
                    moveToMainActivity();
                }
                else{
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Key o id no valida", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        });

    }
}
