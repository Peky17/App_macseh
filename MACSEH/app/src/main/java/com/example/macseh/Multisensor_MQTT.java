package com.example.macseh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class Multisensor_MQTT extends AppCompatActivity {

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

    TextView TxtTemperatura, TxtLuminosidad;
    LineChartView lineChartView;

    String[] axisData = {"1", "2", "3", "4", "5", "6", "7", "8", "9",
            "10", "11", "12"};
    int[] yAxisData = {16, 20, 15, 30, 20, 20, 15, 40, 45, 10, 20, 18};

    /*-*-*-*-*-*- Variables MQTT -*-*-*-*-*-*/
    static String MQTTHOST = "tcp://macseh.ml:1883";
    static String MQTTUSERNAME = "Kevin";
    static String MQTTPASSWORD = "1234";
    /* Inicializamos topicos MQTT */
    String topicTemperatura = "";
    String topicLuminosidad = "";
    String topicPotencia = "";

    MqttAndroidClient clientTemperatura;
    MqttAndroidClient clientLuminosidad;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    String nombreDispositivo = "";
    String serialDisposotivo = "";
    String idUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multisensor__m_q_t_t);
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
        TxtTemperatura = (TextView) findViewById(R.id.TV_Temperatura_Mqtt);
        TxtLuminosidad = (TextView) findViewById(R.id.TV_Luminosidad_Mqtt);
        lineChartView = findViewById(R.id.Grafica_Multisensor);
        /*-*-*-*-*-*- MQTT -*-*-*-*-*-*/
        String clientId = MqttClient.generateClientId();
        clientTemperatura = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId + "Temperatura");
        clientLuminosidad = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId + "Luminosidad");
        /* Colocar usuario y contraseña */
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(MQTTUSERNAME);
        options.setPassword(MQTTPASSWORD.toCharArray());

        /*-*-*-*-*-*- Conexion con servidor MQTT -*-*-*-*-*-*/
        try
        {
            IMqttToken token = clientTemperatura.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast toast = Toast.makeText(getApplicationContext(), "Conectado correctamente", Toast.LENGTH_SHORT); //name:name
                    toast.show();
                    /* Nos suscribimos al topico */
                    suscribirLuminosidad();
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
            IMqttToken token = clientLuminosidad.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast toast = Toast.makeText(getApplicationContext(), "Conectado correctamente", Toast.LENGTH_SHORT); //name:name
                    toast.show();
                    /* Nos suscribimos al topico */
                    suscribirTemperatura();
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
        clientTemperatura.setCallback(new MqttCallback()
        {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                TxtTemperatura.setText(new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        clientLuminosidad.setCallback(new MqttCallback()
        {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                TxtLuminosidad.setText(new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

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
        yAxis.setName("Temperatura");
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
    public void suscribirTemperatura()
    {
        try
        {
            /* Temperatura */
            topicTemperatura = serialDisposotivo + "/temp";
            clientTemperatura.subscribe(topicTemperatura,0);
        }
        catch (Exception exc)
        {
            Toast toast = Toast.makeText(getApplicationContext(), "Error: " + exc.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /*-*-*-*-*-*- Suscribir en topico Consumo MQTT -*-*-*-*-*-*/
    public void suscribirLuminosidad()
    {
        try
        {
            /* Luminosidad */
            topicLuminosidad = serialDisposotivo + "/luz";
            clientLuminosidad.subscribe(topicLuminosidad,0);
        }
        catch (Exception exc)
        {
            Toast toast = Toast.makeText(getApplicationContext(), "Error: " + exc.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
