package com.example.macseh;

import android.content.Context;
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

public class adaptadorEnchufes_Mini extends RecyclerView.Adapter<adaptadorEnchufes_Mini.EnchufesMiniViewHolder> {

    Context context;
    List<Mis_Enchufes_Mini_Modelo> listaEnchufesMini;

    public adaptadorEnchufes_Mini(Context context, List<Mis_Enchufes_Mini_Modelo> listaEnchufesMini) {
        this.context = context;
        this.listaEnchufesMini = listaEnchufesMini;
    }

    @NonNull
    @Override
    public EnchufesMiniViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rv_mis_enchufes_mini, viewGroup, false);
        return new EnchufesMiniViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull EnchufesMiniViewHolder monitoresViewHolder, final int i) {
        monitoresViewHolder.TV_nombreMedidor.setText(listaEnchufesMini.get(i).getNombreDispositivo());
        //monitoresViewHolder.TV_Etiqueta.setText(listaEnchufesMini.get(i).getSerieDispositivo());
        monitoresViewHolder.TV_SerialDP.setText(listaEnchufesMini.get(i).getEtiqueta());
        /* Evento click en el Imageview */
        monitoresViewHolder.ImgVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, listaEnchufesMini.get(i).getNombreDispositivo(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaEnchufesMini.size();
    }

    public class EnchufesMiniViewHolder extends RecyclerView.ViewHolder {

        TextView TV_nombreMedidor, TV_Etiqueta, TV_SerialDP;
        ImageView ImgVer;

        public EnchufesMiniViewHolder(@NonNull View itemView) {
            super(itemView);

            TV_nombreMedidor = itemView.findViewById(R.id.TV_Nombre_Enchufe_Mini);
            //TV_Etiqueta = itemView.findViewById(R.id.TV_Nombre_Etiqueta_Enchufe_Mini);
            TV_SerialDP = itemView.findViewById(R.id.TV_Serial_Enchufe_Mini);
            /*boton ver*/
            ImgVer = itemView.findViewById(R.id.ImageView_Mostrar_Enchufe_Mini);
        }
    }

    public void filtrar(ArrayList<Mis_Enchufes_Mini_Modelo> filtroMonitores) {
        this.listaEnchufesMini = filtroMonitores;
        notifyDataSetChanged();
    }
}