package com.example.serversmsretrieverbi.modelo;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class GuardarEnDB {
    private FirebaseFirestore mDatabase;
    private OTPs clave;
    public GuardarEnDB(){}


    public void guardarDatos(OTPs clave, String documento) {
        mDatabase = FirebaseFirestore.getInstance();
        //Autenticar en la BD
            //Guardamos el codigo en la BD
            DocumentReference docRef = mDatabase.collection("code").document(documento);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    ListCode lCode = documentSnapshot.toObject(ListCode.class);
                    if (lCode != null) {
                        Log.d("DB-Guardar", "Dentro del object listCode, creando la fila");

                        //Vaciamos el contenido
                        Log.d("DB-Guardar", "Se borra nTel despues de escribirse en la BD");
                        clave.setExpedicion(Timestamp.now());
                        mDatabase.collection("code").document(documento)
                                .update("lCode", FieldValue.arrayUnion(clave))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("DB-Guardar", "DocumentSnapshot successfully written!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("DB-Guardar", "Error writing document", e);
                                    }
                                });
                    }
                }
            });
        }

}
