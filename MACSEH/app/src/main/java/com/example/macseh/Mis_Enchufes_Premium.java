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
public class Mis_Enchufes_Premium extends Fragment {

    public Mis_Enchufes_Premium() {
        // Required empty public constructor
    }

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    EditText etBuscador;
    RecyclerView rvLista;
    adaptadorEnchufes_Premium adaptador;
    List<Mis_Enchufes_Premium_Modelo> listaEnchufes;

    String idUser = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista  = inflater.inflate(R.layout.fragment_mis__enchufes__premium, container, false);

        /*-*-*-*-*-*- Preferences -*-*-*-*-*-*/
        preferences = this.getActivity().getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        editor = preferences.edit();
        /* Obtenemos el Id del usuario en sesion */
        idUser = preferences.getString("id", "id");

        etBuscador = vista.findViewById(R.id.etBuscador_Enchufe_Premium);
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

        rvLista = vista.findViewById(R.id.rvLista_Enchufes_Inteligentes);
        rvLista.setLayoutManager(new GridLayoutManager(getContext(), 1));

        listaEnchufes = new ArrayList<Mis_Enchufes_Premium_Modelo>();

        obtenerMultiSensor();

        adaptador = new adaptadorEnchufes_Premium(getContext(), listaEnchufes);
        rvLista.setAdapter(adaptador);

        return vista;
    }

    String URL_Service = "";

    public void obtenerMultiSensor()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        URL_Service =  "https://macseh.ml/Movil/obtener_Enchufes_Premium.php?id=" + idUser;
        StringRequest stringRequest = new StringRequest(Request.Method.POST,URL_Service,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("Enchufes");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                listaEnchufes.add(
                                        new Mis_Enchufes_Premium_Modelo(
                                                jsonObject1.getString("nombre_lugar"),
                                                jsonObject1.getString("dispositivo"),
                                                jsonObject1.getString("serie_dispositivo")
                                        )
                                );
                            }
                            adaptador = new adaptadorEnchufes_Premium(getContext(), listaEnchufes);
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
        ArrayList<Mis_Enchufes_Premium_Modelo> filtrarLista = new ArrayList<>();

        for(Mis_Enchufes_Premium_Modelo Multisensor : listaEnchufes) {
            if(Multisensor.getNombreDispositivo().toLowerCase().contains(texto.toLowerCase())) {
                filtrarLista.add(Multisensor);
            }
        }

        adaptador.filtrar(filtrarLista);
    }

}
