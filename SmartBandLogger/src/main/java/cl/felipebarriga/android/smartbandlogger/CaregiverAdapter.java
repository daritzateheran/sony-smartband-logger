package cl.felipebarriga.android.smartbandlogger;

import android.app.Activity;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CaregiverAdapter extends RecyclerView.Adapter<CaregiverAdapter.CaregiverView> {

    ArrayList<Caregiver> caregiversList = new ArrayList<>();
    CaregiverActivity parent;

    public CaregiverAdapter(ArrayList<Caregiver> caregiversList, CaregiverActivity pParent) {
        this.caregiversList = caregiversList;
        this.parent = pParent;
    }

    @NonNull
    @Override
    public CaregiverView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_add_caregiver,parent,false);

        return new CaregiverView(view).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull CaregiverView holder, int position) {
        Caregiver caregiver = caregiversList.get(position);
        holder.textCaregiverName.setText(caregiver.getname());
        holder.textCaregiverNumber.setText("(+"+caregiver.getExt()+") "+caregiver.getNumber());
    }

    @Override
    public int getItemCount() {
        return caregiversList.size();
    }

    public class CaregiverView extends RecyclerView.ViewHolder{

        private CaregiverAdapter adapter;

        TextView textCaregiverName,textCaregiverNumber;
        public CaregiverView(@NonNull View itemView) {

            super(itemView);

            textCaregiverName = (TextView)itemView.findViewById(R.id.edit_caregiver_name);
            textCaregiverNumber = (TextView)itemView.findViewById(R.id.edit_caregiver_number);
            ImageView imageClose = (ImageView)itemView.findViewById(R.id.image_remove);

            imageClose.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    Caregiver obj = adapter.caregiversList.get(index);
                    adapter.caregiversList.remove(index);
                    adapter.notifyItemRemoved(index);
                    adapter.parent.deleteCaregiverFromPreferences(obj.caregiverToString());
                }
            });

        }

        public CaregiverView linkAdapter(CaregiverAdapter adapter){
            this.adapter = adapter;
            return this;
        }
    }

}
