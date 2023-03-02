package com.example.macseh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements Response.Listener<JSONObject>,Response.ErrorListener {

    EditText ET_Correo, ET_Pass;
    Button BtnLogin;

    RequestQueue rq;
    JsonRequest jrq;

    String nombreUsuario = "";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*-*-*-*-*-*- Preferences -*-*-*-*-*-*/
        preferences = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        editor = preferences.edit();
        /*-*-*-*-*-*- Elementos del Layout -*-*-*-*-*-*/
        ET_Correo = (EditText)findViewById(R.id.ET_Email);
        ET_Pass = (EditText)findViewById(R.id.ET_Password);
       BtnLogin = (Button)findViewById(R.id.BtnIniciar_Sesion);

        rq = Volley.newRequestQueue(this);

        /*-*-*-*-*-*- Evento del boton (Iniciar Sesion)  -*-*-*-*-*-*/
        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Llamamos a el metodo */
                IniciarSesion();
            }
        });

        /* Si un usuario ya esta en sesion (Logueado) */
        if (preferences.contains("user"))
        {
            /* Redireccionamos al panel de la app */
            Intent intent = new Intent(MainActivity.this, Panel.class);
            startActivity(intent);
        }
    }

    public void IniciarSesion()
    {
        /* Declarar la URL donde se tiene el script PHP con la consulta pasando el email y contraseña */
        String url = "https://macseh.ml/Movil/sesion.php?email=" + ET_Correo.getText().toString() + "&pwd=" + ET_Pass.getText().toString();
        jrq = new JsonObjectRequest(Request.Method.GET,url, null, this, this);
        rq.add(jrq);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast toast = Toast.makeText(getApplicationContext(),"Correo o contraseña incorrectos", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onResponse(JSONObject response)
    {
        /* Obtenemos el arreglo JSON llamado "datos"  */
        JSONArray jsonArray = response.optJSONArray("datos");

        JSONObject jsonObject = null;

        try
        {
            /* Inicializamos el areglo desde la posicion cero */
            jsonObject = jsonArray.getJSONObject(0);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        /* Bienvenida con toast */
        Toast toast = Toast.makeText(getApplicationContext(), "Bienvenido: " + jsonObject.optString("name"), Toast.LENGTH_SHORT); //name:name
        toast.show();
        /* Preferencias */
        editor.putString("user",jsonObject.optString("name"));
        editor.putString("name",jsonObject.optString("correo"));
        editor.putString("id",jsonObject.optString("id"));
        editor.commit();
        /* Redireccionamos al panel de la app */
        Intent intent = new Intent(MainActivity.this, Panel.class);
        startActivity(intent);
    }

    /* Metodo para redirigir el uauario a registrarse en el sitio web */
    public void Registrarme(View view)
    {
        /* URL */
        String urlRegistro = "https://macseh.ml/Pagina/modulos/tienda/?p=registro";
        /* Direccionamiento a la URL */
        Uri uri = Uri.parse(urlRegistro);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}

