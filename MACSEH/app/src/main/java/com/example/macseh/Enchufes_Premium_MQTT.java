package com.example.macseh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class Enchufes_Premium_MQTT extends AppCompatActivity {

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
    LineChartView lineChartView;

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
        setContentView(R.layout.activity_enchufes__premium__m_q_t_t);
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
        TxtCorriente = (TextView) findViewById(R.id.TV_Corriente_Mqtt_Enchufe);
        TxtConsumo = (TextView) findViewById(R.id.TV_Consumo_Mqtt_Enchufe);
        TxtPotencia = (TextView) findViewById(R.id.TV_Potencia_Mqtt_Enchufe);
        lineChartView = findViewById(R.id.Grafica_Enchufes_Premium);
        /*-*-*-*-*-*- MQTT -*-*-*-*-*-*/
        String clientId = MqttClient.generateClientId();
        clientCorriente = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId + "Corriente_Enchufe");
        clientConsumo = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId + "Consumo_Enchufe");
        clientPotencia = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId + "Potencia_Enchufe");
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
                yAxisValues.add(new PointValue(new Integer(String.valueOf(message.getPayload())),new Integer(String.valueOf(message.getPayload()))));
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

        /*-*-*-*-*-*- Graficar -*-*-*-*-*-*/
        graficar();

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

    /*-*-*-*-*-*- Evento para encender/apagar el dispositivo -*-*-*-*-*-*/
    //String topicoEncender = serialDisposotivo + "/led79"; <-- Forma correcta
    String topicoEncender = "led79";

    public void Publicar_On()
    {
        try
        {
            String message = "On";
            clientCorriente.publish(topicoEncender, message.getBytes(),0,false);
            Toast toast = Toast.makeText(getApplicationContext(), "Dispositivo Encendido", Toast.LENGTH_SHORT); //name:name
            toast.show();
        }
        catch (MqttException e) {
            Toast toast = Toast.makeText(getApplicationContext(), "Error: " + e.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void Publicar_Off()
    {
        try
        {
            String message = "Off";
            clientCorriente.publish(topicoEncender, message.getBytes(),0,false);
            Toast toast = Toast.makeText(getApplicationContext(), "Dispositivo Apagado", Toast.LENGTH_SHORT); //name:name
            toast.show();
        }
        catch (MqttException e) {
            Toast toast = Toast.makeText(getApplicationContext(), "Error: " + e.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    String[] axisData = {"1", "2", "3", "4", "5", "6", "7", "8", "9",
            "10", "11", "12"};
    int[] yAxisData = {(int) 0.20, (int)0.20, (int)0.20, (int)0.20, (int)0.21, (int)0.22, (int)0.20, (int)0.22, (int)0.26, (int)0.24, (int)0.25, (int)0.23};

    String URL_Service = "";

    final List yAxisValues = new ArrayList();
    final List axisValues = new ArrayList();

    public void graficar()
    {
                            /* Colocamos el color */
                            Line line = new Line(yAxisValues).setColor(Color.parseColor("#9C27B0"));
                            /* rellenamos la lista con un arreglo */
                            for (int i = 0; i < axisData.length; i++)
                            {
                                axisValues.add(i, new AxisValue(i).setLabel(axisData[i]));
                            }
                            /* rellenamo la lista con el arreglo */
                            for (int i = 0; i < yAxisData.length; i++) {
                                yAxisValues.add(new PointValue(i, yAxisData[i]));
                            }

                            /* creamos un objeto linea */
                            List lines = new ArrayList();
                            /* Añadimos el color a la linea */
                            lines.add(line);
                            /* objeto tipo data */
                            LineChartData data = new LineChartData();
                            /* colocamosnuestra linea */
                            data.setLines(lines);
                            /* objeto axis */
                            Axis axis = new Axis();
                            /* colocamos los valores de la lista */
                            axis.setValues(axisValues);
                            axis.setTextSize(16);
                            axis.setTextColor(Color.parseColor("#03A9F4"));
                            data.setAxisXBottom(axis);
                            /* EJE Y */
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

}
