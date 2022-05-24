package cl.felipebarriga.android.smartbandlogger;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;


public class launch extends Activity{

    private static final String LOG_TAG = "SmartBandLogger";
    private final String CLASS = getClass().getSimpleName();

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        Log.d( LOG_TAG, CLASS + " onCreate: called" );
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getActionBar()).hide();
        setContentView( R.layout.launch );

        //Animations
        Animation animation1= AnimationUtils.loadAnimation(this, R.anim.desplazamiento_arriba);
        Animation animation2= AnimationUtils.loadAnimation(this, R.anim.desplazamiento_abajo);

        TextView fromTextView= (TextView) findViewById(R.id.fromTextView);
        TextView srdesignTextView = (TextView) findViewById(R.id.srdesignTextView);
        ImageView logoImageView= (ImageView) findViewById(R.id.logoImageView);

        fromTextView.setAnimation(animation2);
        srdesignTextView.setAnimation(animation2);
        logoImageView.setAnimation(animation1);

      /*  new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = wmbPreference.edit();
                editor.putBoolean("FIRSTRUN", false);
                editor.commit();
            }

        }, 3000);*/
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(launch.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 3000);

    }

}
