package com.example.macseh.ui.gallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.macseh.Mis_Enchufes_Mini_Modelo;
import com.example.macseh.Mis_Multisensor_Modelo;
import com.example.macseh.R;
import com.example.macseh.adaptadorEnchufes_Mini;
import com.example.macseh.adaptadorMultisensores;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    EditText etBuscador;
    RecyclerView rvLista;
    adaptadorEnchufes_Mini adaptador;
    List<Mis_Enchufes_Mini_Modelo> listaEnchufes_Mini;

    String idUser = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View vista = inflater.inflate(R.layout.fragment_gallery, container, false);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });

        /*-*-*-*-*-*- Preferences -*-*-*-*-*-*/
        preferences = this.getActivity().getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        editor = preferences.edit();
        /* Obtenemos el Id del usuario en sesion */
        idUser = preferences.getString("id", "id");

        etBuscador = vista.findViewById(R.id.etBuscador_Enchufes_Mini);
        etBuscador.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                filtrar(s.toString());
            }
        });

        rvLista = vista.findViewById(R.id.rvLista_Enchufes_Mini);
        rvLista.setLayoutManager(new GridLayoutManager(getContext(), 1));

        listaEnchufes_Mini = new ArrayList<Mis_Enchufes_Mini_Modelo>();

        obtenerEnchufes_Mini();

        adaptador = new adaptadorEnchufes_Mini(getContext(), listaEnchufes_Mini);
        rvLista.setAdapter(adaptador);

        return vista;
    }

    String URL_Service = "";

    public void obtenerEnchufes_Mini()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        URL_Service =  "https://macseh.ml/Movil/obtener_Enchufes_Mini.php?id=" + idUser;
        StringRequest stringRequest = new StringRequest(Request.Method.POST,URL_Service,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("Enchufes");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                listaEnchufes_Mini.add(
                                        new Mis_Enchufes_Mini_Modelo(
                                                jsonObject1.getString("nombre_lugar"),
                                                jsonObject1.getString("dispositivo"),
                                                jsonObject1.getString("serie_dispositivo")
                                        )
                                );
                            }
                            adaptador = new adaptadorEnchufes_Mini(getContext(), listaEnchufes_Mini);
                            rvLista.setAdapter(adaptador);

                            /* Poner linea divisora */
                            LinearLayoutManager llm = new LinearLayoutManager(getContext());
                            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),llm.getOrientation());
                            rvLista.addItemDecoration(dividerItemDecoration);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }
        );

        requestQueue.add(stringRequest);
    }

    public void filtrar(String texto) {
        ArrayList<Mis_Enchufes_Mini_Modelo> filtrarLista = new ArrayList<>();

        for(Mis_Enchufes_Mini_Modelo Enchufe : listaEnchufes_Mini) {
            if(Enchufe.getNombreDispositivo().toLowerCase().contains(texto.toLowerCase())) {
                filtrarLista.add(Enchufe);
            }
        }

        adaptador.filtrar(filtrarLista);
    }


}
