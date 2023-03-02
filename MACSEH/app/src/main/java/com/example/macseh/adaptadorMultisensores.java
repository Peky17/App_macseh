package com.example.macseh;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class adaptadorMultisensores extends RecyclerView.Adapter<adaptadorMultisensores.MultisensoresViewHolder> {

    Context context;
    List<Mis_Multisensor_Modelo> listaMultisensor;

    public adaptadorMultisensores(Context context, List<Mis_Multisensor_Modelo> listaMultisensor) {
        this.context = context;
        this.listaMultisensor = listaMultisensor;
    }

    @NonNull
    @Override
    public MultisensoresViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rv_mis_multisensor, viewGroup, false);
        return new MultisensoresViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull MultisensoresViewHolder monitoresViewHolder, final int i) {
        monitoresViewHolder.TV_nombreMultisensor.setText(listaMultisensor.get(i).getNombreDispositivo());
        //monitoresViewHolder.TV_Etiqueta_Multisensor.setText(listaMultisensor.get(i).getSerieDispositivo());
        monitoresViewHolder.TV_SerialDP_Multisensor.setText(listaMultisensor.get(i).getEtiqueta());
        /* Evento click en el Imageview */
        monitoresViewHolder.ImgVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, Multisensor_MQTT.class);
                intent.putExtra("nameDispositivo","Multisensor " + listaMultisensor.get(i).getNombreDispositivo());
                intent.putExtra("serialDispositivo", listaMultisensor.get(i).getEtiqueta());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaMultisensor.size();
    }

    public class MultisensoresViewHolder extends RecyclerView.ViewHolder {

        TextView TV_nombreMultisensor, TV_Etiqueta_Multisensor, TV_SerialDP_Multisensor;
        ImageView ImgVer;

        public MultisensoresViewHolder(@NonNull View itemView) {
            super(itemView);

            TV_nombreMultisensor = itemView.findViewById(R.id.TV_Nombre_Multisensor);
            //TV_Etiqueta_Multisensor = itemView.findViewById(R.id.TV_Nombre_Etiqueta_Multosensor);
            TV_SerialDP_Multisensor = itemView.findViewById(R.id.TV_Serial_Multisensor);
            /*boton ver*/
            ImgVer = itemView.findViewById(R.id.ImageView_Mostrar_Multisensor);
        }
    }

    public void filtrar(ArrayList<Mis_Multisensor_Modelo> filtroMonitores) {
        this.listaMultisensor = filtroMonitores;
        notifyDataSetChanged();
    }
}