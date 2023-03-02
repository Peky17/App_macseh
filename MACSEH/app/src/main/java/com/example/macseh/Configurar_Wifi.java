package com.example.macseh;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class Configurar_Wifi extends AppCompatActivity {

    Button BtnScan;

    //***********************  METODO ON OPTION ITEM SELECTED ******************************//
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        /* Obtenemos el item del toolbar seleccionado */
        switch (item.getItemId()) {
            case android.R.id.home:
                /* Regresar al Panel de la App MACSEH */
                Intent intent = new Intent(this, Panel.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    WifiManager wifiManager;
    WifiReceiver wifiReceiver;
    ListAdapter listAdapter;
    ListView wifiList;
    List mywifiList;
    String SSID_Red = "";
    String ipAddress_Privada = "";

    //***********************  METODO ONCREATE ******************************//
    @SuppressLint("WifiManagerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configurar__wifi);
        /*-*-*-*-*-*- Elementos del Toolbar -*-*-*-*-*-*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        /*-*-*-*-*-*- Elementos del Layout -*-*-*-*-*-*/
        BtnScan = (Button)findViewById(R.id.scanBtn);
        wifiList = (ListView)findViewById(R.id.wifiList);
        wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiReceiver();
        registerReceiver(wifiReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        /* Alerta del permiso de Ubicacion */
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);

            View view = LayoutInflater.from(this).inflate(R.layout.custom_alert_permisos, null);
            builder.setView(view);
            builder.setPositiveButton("IR A ACTIVAR", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });

            builder.setNegativeButton("CERRAR", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    try
                    {}
                    catch (Exception exc)
                    {
                        Toast.makeText(getApplicationContext(),
                                exc.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.show();
        }
        catch (Exception exc)
        {
            Toast toast = Toast.makeText(getApplicationContext(),exc.toString(), Toast.LENGTH_LONG);
            toast.show();
        }

        /* Verificar si tenemos los permisos necesarios (No funciona de Android 6.0 en adelante) */
        if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);
            Toast toast = Toast.makeText(getApplicationContext(), "Active su wifi y su ubicacion", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
           scanWifiList();
        }

        /* Evento click en el boton */
        BtnScan.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                scanWifiList();
                Toast toast = Toast.makeText(getApplicationContext(), "Redes WiFi Escaneadas", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        /* Evento click en un item del list view */
        wifiList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l)
            {
                TextView textView = (TextView) view.findViewById(R.id.txtWifiName);

                String textItemList = textView.getText().toString();

                final AlertDialog.Builder builder = new AlertDialog.Builder(Configurar_Wifi.this);
                view = LayoutInflater.from(Configurar_Wifi.this).inflate(R.layout.custom_alert_permisos, null);
                TextView title = (TextView) view.findViewById(R.id.title);
                ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                TextView descripcion = (TextView) view.findViewById(R.id.TV_Descripcion_producto);
                title.setText("¿DESEA CONECTARSE A ESTA RED WIFI?");
                imageButton.setImageResource(R.drawable.ic_wifi_black);
                descripcion.setText(textItemList);
                descripcion.setTextSize(22);
                SSID_Red = textItemList;

                builder.setPositiveButton("Conectarme", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        ConnectToNetworkWEP(SSID_Red,"");
                        Toast.makeText(getApplicationContext(),SSID_Red, Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        Toast.makeText(getApplicationContext(), "Operacion cancelada", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setView(view);
                builder.show();
            }
        });

    }

    //***********************  ESCANEAR REDES WIFI ******************************//
    private void scanWifiList()
    {
        wifiManager.startScan();
        mywifiList = wifiManager.getScanResults();
        setAdapter();
    }

    //***********************  COLOCAR ADAPTADOR AL LISTVIEW CON LAS REDES WIFI  ******************************//
    private void setAdapter()
    {
      listAdapter = new ListAdapter_Wifi(getApplicationContext(),mywifiList);
      wifiList.setAdapter(listAdapter);
    }

    //***********************  CONECTAR TELEFONO A ACCES POINT DEL DISPOSITIVO MACSEH ******************************//
    public boolean ConnectToNetworkWEP(String networkSSID, String password)
    {
        try
        {
            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";   // String con el SSID del A.P. de la red
            conf.wepKeys[0] = "\"" + password + "\""; //String con la contraseña del A.P. de la red

            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            conf.allowedGroupCiphers.set(WifiConfiguration.AuthAlgorithm.OPEN);
            conf.allowedGroupCiphers.set(WifiConfiguration.AuthAlgorithm.SHARED);

            WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            int networkId = wifiManager.addNetwork(conf);

            if (networkId == -1)
            {
                conf.wepKeys[0] = password;
                networkId = wifiManager.addNetwork(conf);
            }

            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for( WifiConfiguration i : list )
            {
                if(i.SSID != null && i.SSID.equals("\"" + networkSSID + "\""))
                {
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(i.networkId, true);
                    wifiManager.reconnect();
                    break;
                }
            }

            Toast toast = Toast.makeText(getApplicationContext(), "Conectado al AP con exito", Toast.LENGTH_SHORT);
            toast.show();
            return true;
        }
        catch (Exception ex)
        {
            Toast toast = Toast.makeText(getApplicationContext(),ex.toString(), Toast.LENGTH_SHORT);
            toast.show();
            System.out.println(Arrays.toString(ex.getStackTrace()));
            return false;
        }
    }

    //***********************  RECIBIDOR BROADCAST PARA DETECTAR LA CONEXION AL ACCES POINT ******************************//
    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = manager.getActiveNetworkInfo();
            onNetworkChange(ni);
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
   private void onNetworkChange(NetworkInfo networkInfo)
    {
        if (networkInfo != null)
        {
            if (networkInfo.getState() == NetworkInfo.State.CONNECTED)
            {
                WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                int ip = wifiInfo.getIpAddress();
                String ipAddress = Formatter.formatIpAddress(ip);
                ipAddress_Privada = ipAddress;
                Toast toast = Toast.makeText(getApplicationContext(),"Conectado a la red", Toast.LENGTH_SHORT);
                toast.show();
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
