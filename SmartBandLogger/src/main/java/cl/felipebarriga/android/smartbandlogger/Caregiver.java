package cl.felipebarriga.android.smartbandlogger;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class Caregiver extends Activity{
    public String alert;
    private static final String LOG_TAG = "SmartBandLogger";
    private final String CLASS = getClass().getSimpleName();
    public OkHttpClient client = new OkHttpClient();
    public static class Global {
        public static String Number;
        public static String message;
    }

    EditText etMsj,etCel;
    Button btnsend,btnSelec, btnCall;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // SharedPreferences mp = PreferenceManager.getDefaultSharedPreferences(this);



        alert = "Alguien está presentando un evento anormal";
        setContentView( R.layout.caregiver );

        etMsj= (EditText) findViewById(R.id.textMensaje);
        etCel= (EditText) findViewById(R.id.textNumero);
        btnsend= (Button) findViewById(R.id.button);
        btnSelec= (Button) findViewById(R.id.btnAgregar);
        btnCall= (Button) findViewById(R.id.btnCall);

        btnSelec.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent =new Intent(Intent.ACTION_PICK);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);  //acceder tanto al nombre como el # de contacto

                /*SharedPreferences.Editor editor = mp.edit();
                editor.putFloat("user", 925266422);
                editor.apply();
                Float user = mp.getFloat("user", -1);
                System.out.println(user);*/
                startActivityForResult(intent, 1);
            }
        });

        if(ActivityCompat.checkSelfPermission(Caregiver.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Caregiver.this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }
        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SmsManager smsManager = SmsManager.getDefault();
                Global.Number = etCel.getText().toString();
                Global.message = etMsj.getText().toString();
                smsManager.sendTextMessage("3016740782", null, alert , null, null);

                Toast.makeText(Caregiver.this, "Mensaje enviado", Toast.LENGTH_LONG).show();
            }


        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent (Intent.ACTION_CALL, Uri.parse("tel:"+etCel.getText().toString()));
                if(ActivityCompat.checkSelfPermission(Caregiver.this,Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(Caregiver.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                }

                startActivity(i);
            }
        });
    }

    public void logout(View view) {
        //this method will remove session and open login screen
        session sessionManagement = new session(   Caregiver.this);
        sessionManagement.removeSession();
        sessionManagement.removeCaregiver();
        moveToLogin();
    }

    private void moveToLogin() {
        Intent intent = new Intent(Caregiver.this, KeyId.class);
        startActivity(intent);
    }

    public void resetCaregiver(View view) {
        //this method will remove session and open login screen
        session sessionManagement = new session(   Caregiver.this);
        sessionManagement.removeCaregiver();
        String session_caregiver = sessionManagement.getCaregiver();
        Log.d(LOG_TAG, CLASS + " caregiver " + session_caregiver);
        moveToFirst();
    }

    private void moveToFirst() {
        Intent intent = new Intent(Caregiver.this, FirstCaregiver.class);
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

                etMsj.setText(nombre);
                etCel.setText(number);
                Log.d(LOG_TAG, CLASS +  " nombre = " + nombre);
                Log.d(LOG_TAG, CLASS +  " numero = " + number);

                session sessionManagement = new session(   Caregiver.this);
                String userKey = sessionManagement.getSession();

                Log.d(LOG_TAG, CLASS +  "userKey = " + userKey);

                RequestBody form = new FormBody.Builder().add("Key", userKey).add("contact",nombre).add("número", number).build();
                //Request request = new Request.Builder().url("http://10.20.35.106:3000/sign").post(form).build();
                Request request = new Request.Builder().url("http://3.16.124.69:3000/addCaregiver").post(form).build();
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


                    }
                });
            }
        }
    }

}
