package cl.felipebarriga.android.smartbandlogger;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import cl.felipebarriga.android.utils.User;
import cl.felipebarriga.android.utils.session;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FirstCaregiver extends Activity {

    private static final String LOG_TAG = "SmartBandLogger";
    private final String CLASS = getClass().getSimpleName();

    public OkHttpClient client = new OkHttpClient();

    TextView etName,etCel;
    Button btnadd, btnReg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.first_caregiver );
        Objects.requireNonNull(getActionBar()).hide();

        etName= (TextView) findViewById(R.id.textVName);
        etCel= (TextView) findViewById(R.id.textVPhone);
        btnadd= (Button) findViewById(R.id.btnAdd);
        btnReg= (Button) findViewById(R.id.btnRegister);

        /*btnadd.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent(Intent.ACTION_PICK);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);  //acceder tanto al nombre como el # de contacto

                startActivityForResult(intent, 1);
            }
        });*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkSession();
    }

    private void checkSession() {
        session sessionManagement = new session(FirstCaregiver.this);
        String session_caregiver = sessionManagement.getCaregiver();
        Log.d(LOG_TAG, CLASS + " caregiver " + session_caregiver);
        if(!session_caregiver.contains("-1")){
            moveToMainActivity();
        }
        else{
            }
        }

    public void firstCaregiver(View view) {
        Intent intent =new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);  //acceder tanto al nombre como el # de contacto
        startActivityForResult(intent, 1);
    }

    private void moveToMainActivity() {
        Intent intent = new Intent(FirstCaregiver.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK){
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int indiceName= cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int indiceNumber =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String nombre = cursor.getString(indiceName);
                String number = cursor.getString(indiceNumber);

                etName.setText(nombre);
                etCel.setText(number);
                Log.d(LOG_TAG, CLASS +  " nombre = " + nombre);
                Log.d(LOG_TAG, CLASS +  " numero = " + number);

                session sessionManagement = new session(   FirstCaregiver.this);
                String userKey = sessionManagement.getSession();

                Log.d(LOG_TAG, CLASS +  "userKey = " + userKey);

                RequestBody form = new FormBody.Builder().add("Key", userKey).add("contact",nombre).add("numero", number).build();
                //Request request = new Request.Builder().url("http://10.20.35.109:3000/sign").post(form).build();
                Request request = new Request.Builder().url("http://3.16.124.69:3000/add_caregiver").post(form).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.d(LOG_TAG, CLASS + ": SERVER IS NOT WORKING");

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String r = response.body().string();
                        Log.d(LOG_TAG, CLASS + "response " + r);
                        if (r.equals("Contact added successfully")){
                            sessionManagement.setCaregiver("1");

                            moveToMainActivity();
                        }
                        else{
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(),r, Toast.LENGTH_LONG).show();
                                }
                            });
                        }


                    }
                });
            }
        }
    }
}
