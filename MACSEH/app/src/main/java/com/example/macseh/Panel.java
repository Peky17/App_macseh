package com.example.macseh;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Panel extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    TextView TvCorreo;
    TextView TvNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        /*-*-*-*-*-*- Elementos del Navigation activity -*-*-*-*-*-*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        /* Evento click para el Boton flotante */
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Registrar Producto", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        //con esto generamos el usuario en el header del menu-------------------------------
        View hView = navigationView.getHeaderView(0);
        TvCorreo = (TextView) hView.findViewById(R.id.tv_Correo);
        TvNombre = (TextView) hView.findViewById(R.id.textView_Name);
        /* App Bar Configuration */
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,R.id.mis_monitores,R.id.mis_Enchufes_Premium,R.id.mis_Multisensor)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        /*-*-*-*-*-*- Preferences -*-*-*-*-*-*/
        preferences = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        editor = preferences.edit();
        /* Obtenemos el usuario en sesion */
        String userSesion = preferences.getString("user", "Correo");
        String userName = preferences.getString("name", "Nombre");
        /* Mostramos la informacion del usuario en sesion */
        TvCorreo.setText(userSesion);
        TvNombre.setText(userName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.panel, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /* Metodo para gestionar los item seleccionados en el MenuItem: activity_menu_principal.xml  --> item*/
    @SuppressLint("ResourceType")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        /* Obtener el id del item seleccionado */
        int id= item.getItemId();
        /* Si el item seleccionado es 'action_settings' entonces saldremos del sistema. */
        if(id == R.id.action_settings)
        {
            final AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("EXIT");
            dialogo1.setMessage("¿Desea salir del sistema?");
            /* Si el usuario desea cerrar l app */
            dialogo1.setPositiveButton("SI", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialogo1, int id)
                {
                    /* Cerrar la app */
                    finishAffinity();
                }
            });
            /* Si el usuario no desea cerrar la app */
            dialogo1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id)
                {
                    /* Ocualtar el alertDialog */
                    dialogo1.dismiss();
                }
            });

            dialogo1.show();
        }
        /* Si el item seleccionado es 'action_Wifi' entonces redirigimos al fragment correspondiente. */
        else if (id == R.id.action_WiFi)
        {
          /* Direccionar al activity indicado */
            startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));

            /*Intent intent = new Intent(this, Configurar_Wifi.class);
            startActivity(intent);*/
        }

        /* Si el item seleccionado es 'action_Wifi' entonces redirigimos al fragment correspondiente. */
        else if (id == R.id.action_Logout)
        {
            final AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("CERRAR SESION");
            dialogo1.setMessage("¿Desea cerrar la sesion?");
            /* Si el usuario desea cerrar l app */
            dialogo1.setPositiveButton("SI", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialogo1, int id)
                {
                    /* Eliminar el usuario de las preferencias */
                    editor.clear();
                    editor.commit();
                    /* redirigir al login */
                    Intent intent = new Intent(Panel.this, MainActivity.class);
                    startActivity(intent);
                }
            });
            /* Si el usuario no desea cerrar sesion */
            dialogo1.setNegativeButton("NO", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialogo1, int id)
                {
                    /* Ocualtar el alertDialog */
                    dialogo1.dismiss();
                }
            });

            dialogo1.show();
        }

        return super.onOptionsItemSelected(item);
    }

    //***********************  RECIBIDOR BROADCAST PARA DETECTAR LA CONEXION AL ACCES POINT ******************************//
    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = manager.getActiveNetworkInfo();
            try {
                onNetworkChange(ni);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    };

    //***********************  METODO ON RESUME ******************************//
    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    //***********************  METODO ON PAUSE ******************************//
    @Override
    public void onPause() {
        unregisterReceiver(networkStateReceiver);
        super.onPause();
    }

    //***********************  METODO PARA CONOCER SI ESTAMOS CONECTADOS A UNA RED WiFi ******************************//
    private void onNetworkChange(NetworkInfo networkInfo) throws UnknownHostException {
        if (networkInfo != null)
        {
            if (networkInfo.getState() == NetworkInfo.State.CONNECTED)
            {
                /* Usamos el metofo WifiManager */
                final WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                /* Obtener SSID */
                final DhcpInfo dhcp = wifiMgr.getDhcpInfo();
                /* Obtenemos la direccion ip del gateway */
                int ipInt = dhcp.gateway;
                String ip = InetAddress.getByAddress(
                        ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array())
                        .getHostAddress();
                /* Si el Gateway obtenido es igual al establecido por defecto en el A.P. del ESP32 */
                if(ip.contains("192.168.4.1"))
                {
                    /* Mostrar alerta para direccionar al navegador */
                    try {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

                        View view = LayoutInflater.from(this).inflate(R.layout.custom_alert_permisos, null);
                        builder.setView(view);
                        builder.setPositiveButton("IR A CONECTARLO", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                try {

                                    /* URL del Gateway */
                                    String urlRegistro = "http://192.168.4.1/";
                                    /* Direccionamiento a la URL */
                                    Uri uri = Uri.parse(urlRegistro);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                }
                                catch (Exception exc)
                                {
                                    Toast.makeText(getApplicationContext(),
                                            exc.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                        builder.show();
                    }
                    catch (Exception exc)
                    {
                        Toast toast = Toast.makeText(getApplicationContext(),exc.toString(), Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            }
            else
            {
                Toast toast = Toast.makeText(getApplicationContext(),"Desconectado de la red", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }


    //***********************  RECIBIDOR BROADCAST PARA EL ESCANEO WIFI ******************************//
    class WifiReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

}
