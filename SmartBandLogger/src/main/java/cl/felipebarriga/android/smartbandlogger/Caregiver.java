package cl.felipebarriga.android.smartbandlogger;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class Caregiver extends Activity{
    public String alert;
    public static class Global {
        public static String Number;
        public static String message;
    }

    EditText etMsj,etCel;
    Button btnsend,btnSelec, btnCall;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences mp = PreferenceManager.getDefaultSharedPreferences(this);

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

            }
        }
    }

}
