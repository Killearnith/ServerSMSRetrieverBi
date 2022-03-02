package com.example.serversmsretrieverbi;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.serversmsretrieverbi.Modelo.Clave;
import com.example.serversmsretrieverbi.Modelo.ListCode;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ServerA";
    //Obtenido de el repositorio de Android: https://github.com/android/identity-samples/tree/main/SmsVerification/app/src/main/java/com/google/samples/smartlock/sms_verify/ui
    private FirebaseFirestore mDatabase;
    private String rCode, nTel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Instanciar la base de datos
        mDatabase = FirebaseFirestore.getInstance();


        //Obtenemos los datos que queremos guardar en la BD para tener un control
        rCode = crearCodigo();
        //Obtener tel ---->

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
    }

    //Método que crea el código OTP aleatorio.
    private String crearCodigo(){
        String num;
        num = String.valueOf(Math.random()).substring(0,5);
        return num;
    }
}