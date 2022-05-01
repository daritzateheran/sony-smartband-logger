package cl.felipebarriga.android.smartbandlogger;


import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import android.widget.Toast;

import java.util.ArrayList;

public class CaregiverActivity extends Activity implements View.OnClickListener {

    Button buttonAdd;

    RecyclerView recyclerCaregivers;
    ArrayList<Caregiver> caregiversList = new ArrayList<>();
    CaregiverAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.caregiver_activity);

        recyclerCaregivers = findViewById(R.id.recycler_caregivers);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerCaregivers.setLayoutManager(layoutManager);

        String listaActual = PreferenceManager.getDefaultSharedPreferences(this).getString("caregivers", "");
        String[] listaActualEditada = listaActual.split(";");

        for(String item:listaActualEditada){
            if(item != ""){
                caregiversList.add(new Caregiver(item));
            }
        }

        adapter = new CaregiverAdapter(caregiversList,this);
        recyclerCaregivers.setAdapter(adapter);

        buttonAdd = findViewById(R.id.button_add);

        buttonAdd.setOnClickListener(this);

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void deleteCaregiverFromPreferences(String objToRemove){
        String listaActual = PreferenceManager.getDefaultSharedPreferences(this).getString("caregivers", "");
        String listaFinal = listaActual.replaceFirst(objToRemove,"");
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("caregivers", listaFinal).apply();
        Toast.makeText(this, "Cuidador eliminado exitosamente.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.button_add){
            String number = "3009876595";
            String ext = "57";
            Caregiver contacto = new Caregiver("Darit7",number,ext);
            String contactoAgregado = contacto.caregiverToString();

            String existe = PreferenceManager.getDefaultSharedPreferences(this).getString("caregivers", "");
            if (existe == ""){
                //WRITE IF IT DOESN'T EXIST
                PreferenceManager.getDefaultSharedPreferences(this).edit().putString("caregivers", contactoAgregado).apply();
            }
            else {
                //READ
                String listaActual = PreferenceManager.getDefaultSharedPreferences(this).getString("caregivers", "");
                //Checks if the number exists
                Boolean existeNumero = listaActual.contains(contacto.getNumber()+","+contacto.getExt());
                if(!existeNumero) {
                    String listaActualizada = listaActual + contactoAgregado;
                    //WRITE
                    PreferenceManager.getDefaultSharedPreferences(this).edit().putString("caregivers", listaActualizada).apply();
                    adapter.caregiversList.add(contacto);
                    adapter.notifyItemInserted(adapter.caregiversList.size()-1);
                    //SHOW MESSAGE
                    Toast.makeText(this, "Cuidador creado exitosamente", Toast.LENGTH_SHORT).show();
                }
                else{
                    //SHOW ERROR MESSAGE
                    Toast.makeText(this, "El cuidador ya existe.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
