package com.example.macseh;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Mis_Multisensor extends Fragment {

    public Mis_Multisensor() {
        // Required empty public constructor
    }

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    EditText etBuscador;
    RecyclerView rvLista;
    adaptadorMultisensores adaptador;
    List<Mis_Multisensor_Modelo> listaMultisensor;

    String idUser = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_mis__multisensor, container, false);

        /*-*-*-*-*-*- Preferences -*-*-*-*-*-*/
        preferences = this.getActivity().getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        editor = preferences.edit();
        /* Obtenemos el Id del usuario en sesion */
        idUser = preferences.getString("id", "id");

        etBuscador = vista.findViewById(R.id.etBuscador_Multisensor);
        etBuscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                filtrar(s.toString());
            }
        });

        rvLista = vista.findViewById(R.id.rvLista_Multisensor);
        rvLista.setLayoutManager(new GridLayoutManager(getContext(), 1));

        listaMultisensor = new ArrayList<Mis_Multisensor_Modelo>();

        obtenerMultiSensor();

        adaptador = new adaptadorMultisensores(getContext(), listaMultisensor);
        rvLista.setAdapter(adaptador);

        return vista;
    }

    String URL_Service = "";

    public void obtenerMultiSensor()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        URL_Service =  "https://macseh.ml/Movil/obtener_multiSensor.php?id=" + idUser;
        StringRequest stringRequest = new StringRequest(Request.Method.POST,URL_Service,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("Multisensores");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                listaMultisensor.add(
                                        new Mis_Multisensor_Modelo(
                                                jsonObject1.getString("nombre_lugar"),
                                                jsonObject1.getString("dispositivo"),
                                                jsonObject1.getString("serie_dispositivo")
                                        )
                                );
                            }
                            adaptador = new adaptadorMultisensores(getContext(), listaMultisensor);
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
        ArrayList<Mis_Multisensor_Modelo> filtrarLista = new ArrayList<>();

        for(Mis_Multisensor_Modelo Multisensor : listaMultisensor) {
            if(Multisensor.getNombreDispositivo().toLowerCase().contains(texto.toLowerCase())) {
                filtrarLista.add(Multisensor);
            }
        }

        adaptador.filtrar(filtrarLista);
    }
}
