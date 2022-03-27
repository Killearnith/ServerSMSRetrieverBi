package com.example.serversmsretrieverbi;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.example.serversmsretrieverbi.modelo.Clave;
import com.example.serversmsretrieverbi.modelo.ListCode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ServerA";
    private FirebaseFirestore mDatabase;
    private String nTel, msg;
    private int rCode;
    private TextView textView;
    private FirebaseApp app;
    private FirebaseAuth auten;
    private String auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Esconder la barra superior de la APP
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        //Enlazar views
        textView = (TextView) findViewById(R.id.text);

        //Instanciar la base de datos
        //Autenticar en la BD

        app = FirebaseApp.initializeApp(this);
        auten = FirebaseAuth.getInstance();



        //Obtener token de Auth
        String url ="https://www.googleapis.com/identitytoolkit/v3/relyingparty/verifyPassword?key=AIzaSyCO0wQa_fia6ojLkFCzLG-sft5XUWF2Skw";
        Log.d("Test","Aqui llego");
        // Request a string response from the provided URL.
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject postData = new JSONObject();
        try {
            postData.put("email","a@a.com");
            postData.put("password", "123456");
            postData.put("returnSecureToken", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectPost = new JsonObjectRequest(Request.Method.POST, url, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    auth = response.getString("idToken");
                    //textView.setText(response.getString("idToken"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Toast.makeText(getApplicationContext(), "Response: "+response, Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(jsonObjectPost);
        //Obtener tel ---->
        Log.d("Test","Aqui tmb");
        auten.signInWithEmailAndPassword("a@a.com","123456").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

            }
        });
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("numeros").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                String url2 ="https://smsretrieverservera-default-rtdb.europe-west1.firebasedatabase.app/numeros.json?auth="+auth;
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    nTel=response.getString("tel");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                textView.setText("Recibida petición de: "+nTel);
                                if(nTel!=null){
                                    //Creamos el nuevo codigo OTP.
                                    rCode = crearCodigo();
                                    msg = crearMensaje();
                                    sendSMS(nTel,msg);
                                    //SmsManager sms = SmsManager.getDefault();
                                    //PendingIntent sentSMS;
                                    //String SENT = "SMS_SENT";
                                    //sentSMS = PendingIntent.getBroadcast(MainActivity.this, 0,new Intent(SENT), FLAG_IMMUTABLE);
                                    //sms.sendTextMessage(nTel, null, msg, null, null);
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "No hay mensajes", Toast.LENGTH_LONG).show();
                            }
                        });
                queue.add(jsonObjectRequest);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        
    }
    //Obtenemos los datos que queremos guardar en la BD para tener un control

        /*
        //Guardamos el codigo en la BD
        DocumentReference docRef = mDatabase.collection("code").document("listcode");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ListCode lCode = documentSnapshot.toObject(ListCode.class);
                if (lCode != null) {
                    Log.d(TAG, "Dentro del object listCode, creando la fila");
                    Clave clave = new Clave();
                    clave.setCode(rCode);
                    clave.setNumtel(nTel);
                    clave.setExpiracion(Timestamp.now());
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Fallo al añadir nueva fila al registro", e);
                    }
                });

*/

    //Método que crea el código OTP aleatorio.
    private int crearCodigo(){
        int num;
        num = new Random().nextInt(900000) + 100000;
        return num;
    }

    private String crearMensaje(){
        String msg;
        msg = "Tú código OTP es: "+rCode+"\n"+"g3Mji1k3j7Q";
        return msg;
    }
    //https://localcoder.org/how-to-monitor-each-of-sent-sms-status
    private void sendSMS(String phoneNumber, String message)
    {
        String enviado = "SMS_SENT";
        String recibido = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(enviado), FLAG_IMMUTABLE);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(recibido), FLAG_IMMUTABLE);

        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS enviado",Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Fallo genérico",Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "Sin servicio error",Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio apagada",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(enviado));

        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS mandado correctamente",Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "Ha fallado el envío de SMS",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(recibido));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }
}