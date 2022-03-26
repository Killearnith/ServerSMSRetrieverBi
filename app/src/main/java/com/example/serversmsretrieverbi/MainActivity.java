package com.example.serversmsretrieverbi;

import android.os.Bundle;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ServerA";
    private FirebaseFirestore mDatabase;
    private String rCode, nTel;
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

        //Obtenemos los datos que queremos guardar en la BD para tener un control
        rCode = crearCodigo();

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
                Toast.makeText(getApplicationContext(), "Response: "+response, Toast.LENGTH_LONG).show();

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
                if (task.isSuccessful()){
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
                        textView.setText("Recibida petición de: " + nTel);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        queue.add(jsonObjectRequest);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Error de auth", Toast.LENGTH_LONG);
                }
            }
        });
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
    }
    //Método que crea el código OTP aleatorio.
    private String crearCodigo(){
        String num;
        num = String.valueOf(Math.random()).substring(0,5);
        return num;
    }
}