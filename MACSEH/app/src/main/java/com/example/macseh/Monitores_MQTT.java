package com.example.macseh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class Monitores_MQTT extends AppCompatActivity {

    //***********************  METODO ON OPTION ITEM SELECTED ******************************//
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        /* Obtenemos el item del toolbar seleccionado */
        switch (item.getItemId()) {
            case android.R.id.home:
                /* Regresar al fragment */
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    TextView TxtCorriente, TxtConsumo, TxtPotencia;
    ImageView IV_Boton;
    LineChartView lineChartView;

    String[] axisData = {"1", "2", "3", "4", "5", "6", "7", "8", "9",
            "10", "11", "12"};
    int[] yAxisData = {(int) 0.20, (int)0.20, (int)0.20, (int)0.20, (int)0.21, (int)0.22, (int)0.20, (int)0.22, (int)0.26, (int)0.24, (int)0.25, (int)0.23};

    /*-*-*-*-*-*- Variables MQTT -*-*-*-*-*-*/
    static String MQTTHOST = "tcp://macseh.ml:1883";
    static String MQTTUSERNAME = "Kevin";
    static String MQTTPASSWORD = "1234";
    /* Inicializamos topicos MQTT */
    String topicCorriente = "";
    String topicConsumo = "";
    String topicPotencia = "";

    MqttAndroidClient clientCorriente;
    MqttAndroidClient clientConsumo;
    MqttAndroidClient clientPotencia;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    String nombreDispositivo = "";
    String serialDisposotivo = "";
    String idUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitores__m_q_t_t);
        /*-*-*-*-*-*- Preferences -*-*-*-*-*-*/
        preferences = getApplicationContext().getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        editor = preferences.edit();
        /* Obtenemos el Id del usuario en sesion */
        idUser = preferences.getString("id", "id");
        /*-*-*-*-*-*- Recibir informacion del fragment -*-*-*-*-*-*/
        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        if(b!=null)
        {
            nombreDispositivo = (String) b.get("nameDispositivo");
            serialDisposotivo = (String) b.get("serialDispositivo");
        }
        /*-*-*-*-*-*- Elementos del Toolbar -*-*-*-*-*-*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(nombreDispositivo);
        /*-*-*-*-*-*- Elementos del Layout -*-*-*-*-*-*/
        TxtCorriente = (TextView) findViewById(R.id.TV_Corriente_Mqtt);
        TxtConsumo = (TextView) findViewById(R.id.TV_Consumo_Mqtt);
        TxtPotencia = (TextView) findViewById(R.id.TV_Potencia_Mqtt);
        lineChartView = findViewById(R.id.Grafica);
        /*-*-*-*-*-*- MQTT -*-*-*-*-*-*/
        String clientId = MqttClient.generateClientId();
        clientCorriente = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId + "Corriente");
        clientConsumo = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId + "Consumo");
        clientPotencia = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId + "Potencia");

        /* Colocar usuario y contraseña */
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(MQTTUSERNAME);
        options.setPassword(MQTTPASSWORD.toCharArray());

        /*-*-*-*-*-*- Conexion con servidor MQTT -*-*-*-*-*-*/
        try
        {
            IMqttToken token = clientCorriente.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast toast = Toast.makeText(getApplicationContext(), "Conectado correctamente", Toast.LENGTH_SHORT); //name:name
                    toast.show();
                    /* Nos suscribimos al topico */
                    suscribirCorriente();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast toast = Toast.makeText(getApplicationContext(), "Error: " + exception.toString(), Toast.LENGTH_SHORT); //name:name
                    toast.show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        try
        {
            IMqttToken token = clientConsumo.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast toast = Toast.makeText(getApplicationContext(), "Conectado correctamente", Toast.LENGTH_SHORT); //name:name
                    toast.show();
                    /* Nos suscribimos al topico */
                    suscribirConsumo();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast toast = Toast.makeText(getApplicationContext(), "Error: " + exception.toString(), Toast.LENGTH_SHORT); //name:name
                    toast.show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        try
        {
            IMqttToken token = clientPotencia.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast toast = Toast.makeText(getApplicationContext(), "Conectado correctamente", Toast.LENGTH_SHORT); //name:name
                    toast.show();
                    /* Nos suscribimos al topico */
                    suscribirPotencia();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast toast = Toast.makeText(getApplicationContext(), "Error: " + exception.toString(), Toast.LENGTH_SHORT); //name:name
                    toast.show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        /*-*-*-*-*-*- Evento Callback en topico MQTT -*-*-*-*-*-*/
        clientCorriente.setCallback(new MqttCallback()
        {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                TxtCorriente.setText(new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        clientConsumo.setCallback(new MqttCallback()
        {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                TxtConsumo.setText(new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        clientPotencia.setCallback(new MqttCallback()
        {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                TxtPotencia.setText(new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        /*-*-*-*-*-*- Evento del boton encender -*-*-*-*-*-*/


        /*-*-*-*-*-*- Grafica -*-*-*-*-*-*/
        /* Grafica de puntos */
        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();

        Line line = new Line(yAxisValues).setColor(Color.parseColor("#9C27B0"));

        for (int i = 0; i < axisData.length; i++) {
            axisValues.add(i, new AxisValue(i).setLabel(axisData[i]));
        }

        for (int i = 0; i < yAxisData.length; i++) {
            yAxisValues.add(new PointValue(i, yAxisData[i]));
        }

        List lines = new ArrayList();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axis = new Axis();
        axis.setValues(axisValues);
        axis.setTextSize(16);
        axis.setTextColor(Color.parseColor("#03A9F4"));
        data.setAxisXBottom(axis);

        Axis yAxis = new Axis();
        yAxis.setName("Corriente");
        yAxis.setTextColor(Color.parseColor("#03A9F4"));
        yAxis.setTextSize(16);
        data.setAxisYLeft(yAxis);

        lineChartView.setLineChartData(data);
        Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
        viewport.top = 110;
        lineChartView.setMaximumViewport(viewport);
        lineChartView.setCurrentViewport(viewport);
    }

    /*-*-*-*-*-*- Suscribir en topico Corriente MQTT -*-*-*-*-*-*/
    public void suscribirCorriente()
    {
      try
      {
          /* Corriente */
          topicCorriente = serialDisposotivo + "/corriente";
          clientCorriente.subscribe(topicCorriente,0);
      }
      catch (Exception exc)
      {
          Toast toast = Toast.makeText(getApplicationContext(), "Error: " + exc.toString(), Toast.LENGTH_SHORT);
          toast.show();
      }
    }

    /*-*-*-*-*-*- Suscribir en topico Consumo MQTT -*-*-*-*-*-*/
    public void suscribirConsumo()
    {
        try
        {
            /* Consumo */
            topicConsumo = serialDisposotivo + "/consumo";
            clientConsumo.subscribe(topicConsumo,0);
        }
        catch (Exception exc)
        {
            Toast toast = Toast.makeText(getApplicationContext(), "Error: " + exc.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /*-*-*-*-*-*- Suscribir en topico Potencia MQTT -*-*-*-*-*-*/
    public void suscribirPotencia()
    {
        try
        {
            /* Potencia */
            topicPotencia = serialDisposotivo + "/potencia";
            clientPotencia.subscribe(topicPotencia,0);
        }
        catch (Exception exc)
        {
            Toast toast = Toast.makeText(getApplicationContext(), "Error: " + exc.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
