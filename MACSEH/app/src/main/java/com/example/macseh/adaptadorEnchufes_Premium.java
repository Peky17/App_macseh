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

public class adaptadorEnchufes_Premium extends RecyclerView.Adapter<adaptadorEnchufes_Premium.EnchufesViewHolder>
{
    Context context;
    List<Mis_Enchufes_Premium_Modelo> listaEnchufes;

    public adaptadorEnchufes_Premium(Context context, List<Mis_Enchufes_Premium_Modelo> listaEnchufes) {
        this.context = context;
        this.listaEnchufes = listaEnchufes;
    }

    @NonNull
    @Override
    public EnchufesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rv_mis_enchufes_premium, viewGroup, false);
        return new EnchufesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EnchufesViewHolder EnchufesViewHolder, final int i) {
        EnchufesViewHolder.TV_nombreEnchufe.setText(listaEnchufes.get(i).getNombreDispositivo());
        //EnchufesViewHolder.TV_Etiqueta_Enchufe.setText(listaEnchufes.get(i).getSerieDispositivo());
        EnchufesViewHolder.TV_SerialDP_Enchufe.setText(listaEnchufes.get(i).getEtiqueta());
        /* Evento click en el Imageview */
        EnchufesViewHolder.ImgVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, Enchufes_Premium_MQTT.class);
                intent.putExtra("nameDispositivo","Enchufe " + listaEnchufes.get(i).getNombreDispositivo());
                intent.putExtra("serialDispositivo", listaEnchufes.get(i).getEtiqueta());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaEnchufes.size();
    }

    public class EnchufesViewHolder extends RecyclerView.ViewHolder {

        TextView TV_nombreEnchufe, TV_Etiqueta_Enchufe, TV_SerialDP_Enchufe;
        ImageView ImgVer;

        public EnchufesViewHolder(@NonNull View itemView) {
            super(itemView);

            TV_nombreEnchufe = itemView.findViewById(R.id.TV_Nombre_Enchufe_Premium);
            //TV_Etiqueta_Enchufe = itemView.findViewById(R.id.TV_Nombre_Etiqueta_Enchufe_Premium);
            TV_SerialDP_Enchufe = itemView.findViewById(R.id.TV_Serial_Enchufe_Premium);
            /*boton ver*/
            ImgVer = itemView.findViewById(R.id.ImageView_Mostrar_Enchufe_Premium);
        }
    }

    public void filtrar(ArrayList<Mis_Enchufes_Premium_Modelo> filtroEnchufes) {
        this.listaEnchufes = filtroEnchufes;
        notifyDataSetChanged();
    }
}