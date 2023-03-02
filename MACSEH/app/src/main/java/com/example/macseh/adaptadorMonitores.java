package com.example.macseh;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;

public class adaptadorMonitores extends RecyclerView.Adapter<adaptadorMonitores.MonitoresViewHolder> {

    Context context;
    List<Mis_Monitores_Modelo> listaMonitores;

    private AdapterView.OnItemClickListener onItemClickListener;

    public adaptadorMonitores(Context context, List<Mis_Monitores_Modelo> listaMonitores) {
        this.context = context;
        this.listaMonitores = listaMonitores;
    }

    @NonNull
    @Override
    public MonitoresViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rv_mis_monitores, viewGroup, false);
        return new MonitoresViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull MonitoresViewHolder monitoresViewHolder, final int i) {
        monitoresViewHolder.TV_nombreMedidor.setText(listaMonitores.get(i).getNombreDispositivo());
        //monitoresViewHolder.TV_Etiqueta.setText(listaMonitores.get(i).getSerieDispositivo());
        monitoresViewHolder.TV_SerialDP.setText(listaMonitores.get(i).getEtiqueta());
        /* Evento click en el Imageview */
        monitoresViewHolder.ImgVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, listaMonitores.get(i).getNombreDispositivo(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, Monitores_MQTT.class);
                intent.putExtra("nameDispositivo","Monitor " + listaMonitores.get(i).getNombreDispositivo());
                intent.putExtra("serialDispositivo", listaMonitores.get(i).getEtiqueta());
                context.startActivity(intent);
            }
        });
    }

    public adaptadorMonitores onClickListener;

    @Override
    public int getItemCount() {
        return listaMonitores.size();
    }

    public class MonitoresViewHolder extends RecyclerView.ViewHolder {

        TextView TV_nombreMedidor, TV_Etiqueta, TV_SerialDP;
        ImageView ImgVer;

        public MonitoresViewHolder(@NonNull View itemView) {
            super(itemView);

            TV_nombreMedidor = itemView.findViewById(R.id.TV_Nombre_Medidor);
            //TV_Etiqueta = itemView.findViewById(R.id.TV_Nombre_Etiqueta);
            TV_SerialDP = itemView.findViewById(R.id.TV_Serial);
            /*boton ver*/
            ImgVer = itemView.findViewById(R.id.ImageView_Mostrar);
        }
    }


    public void filtrar(ArrayList<Mis_Monitores_Modelo> filtroMonitores) {
        this.listaMonitores = filtroMonitores;
        notifyDataSetChanged();
    }


}