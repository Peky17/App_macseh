package com.example.macseh.ui.slideshow;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.macseh.MainActivity;
import com.example.macseh.Panel;
import com.example.macseh.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SlideshowFragment extends Fragment implements Response.Listener<JSONObject>,Response.ErrorListener {

    private SlideshowViewModel slideshowViewModel;

    Button btnScanner;
    TextView ETCode;

    String idUser = "";

    RequestQueue rq;
    JsonRequest jrq;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        rq = Volley.newRequestQueue(getContext());
        /*-*-*-*-*-*- Preferences -*-*-*-*-*-*/
        preferences = this.getActivity().getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        editor = preferences.edit();
        /* Obtenemos el Id del usuario en sesion */
        idUser = preferences.getString("id", "id");
        btnScanner = root.findViewById(R.id.BtnScan);
        ETCode = root.findViewById(R.id.ET_Codigo);

        /*-*-*-*-*-*- Evento del boton (Escanear)  -*-*-*-*-*-*/
        btnScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Iniciar escaneo */
                IntentIntegrator integrator = IntentIntegrator.forSupportFragment(SlideshowFragment.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Porfavor enfoque la camara en el codigo QR");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

        /* Llamamos al metodo para obtener el estatus de la sesion web */
        get_Status_Sesion();

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null)
        {
            if (result.getContents() != null)
            {
                String Info_Usuario = result.getContents();
                /* Llamo al metodo validar */
                ValidarQR();
            }
            else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    public void ValidarQR()
    {
        /* Declarar la URL donde se tiene el script PHP con la consulta pasando el usuario */
        String url = "https://macseh.ml/Movil/validarQR.php?id=" + idUser;
        jrq = new JsonObjectRequest(Request.Method.GET,url, null, this, this);
        rq.add(jrq);
    }

    public void get_Status_Sesion()
    {
        /* Declarar la URL donde se tiene el script PHP con la consulta pasando el email y contraseña */
        String url = "https://macseh.ml/Movil/obtenerInfo_Sesion.php?id=" + idUser;
        jrq = new JsonObjectRequest(Request.Method.GET,url, null, this, this);
        rq.add(jrq);
    }

    @Override
    public void onErrorResponse(VolleyError error) {

        /* Mostrar alerta donde se autentico el usuario correctamente */
        try
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_alert_permisos, null);
            /**/
            ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
            TextView TVTitulo = (TextView) view.findViewById(R.id.title);
            TextView TVMesaage = (TextView) view.findViewById(R.id.TV_Descripcion_producto);
            TextView TVQueestion = (TextView) view.findViewById(R.id.TV_question);
            TVTitulo.setText("OPERACION REALIZADA");
            TVMesaage.setText("Identidad verificada con exito");
            TVQueestion.setText("¡Compruebelo desde la App Web!");
            imageButton.setImageResource(R.drawable.ic_verified_user_black_24dp);
            /* Colocamos la vista inflada */
            builder.setView(view);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    try
                    {
                       /* Cerramos el dialogo */
                    }
                    catch (Exception exc)
                    {
                        Toast.makeText(getContext(),
                                exc.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            builder.show();
        }
        catch (Exception exc)
        {
            Toast toast = Toast.makeText(getContext(),exc.toString(), Toast.LENGTH_LONG);
            toast.show();
        }

    }

    @Override
    public void onResponse(JSONObject response)
    {
       /* Obtener el estado de la sesion web */
        JSONArray jsonArray = response.optJSONArray("status");
        JSONObject jsonObject = null;

        try
        {
            /* Inicializamos el areglo desde la posicion cero */
            jsonObject = jsonArray.getJSONObject(0);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        String result =  jsonObject.optString("valor");

        if(result.contains("1"))
        {
            ETCode.setText("SESION WEB INICIADA");
        }
        else if(result.contains("0"))
        {
            ETCode.setText("SESION WEB NO INICIADA");
        }
    }

    //***********************  METODO ON RESUME ******************************//
    @Override
    public void onResume()
    {
        super.onResume();
        get_Status_Sesion();
    }

}
