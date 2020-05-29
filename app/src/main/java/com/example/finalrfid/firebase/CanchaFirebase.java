package com.example.finalrfid.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.Key;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.ContentValues.TAG;

public class CanchaFirebase {
    private  DatabaseReference mDatabase;
    ArrayList<Apartado> apartadoArrayList= new ArrayList<Apartado>();
    public CanchaFirebase(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Apartado").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                apartadoArrayList.clear();
                Iterable data = dataSnapshot.getChildren();
                for (int i = 0; i < apartadoArrayList.size(); i++){
                    Apartado apartado = dataSnapshot.getValue(Apartado.class);
                    apartadoArrayList.add(apartado);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

    }

    public void cargaDato() {
        String key = mDatabase.child("Apartado").push().getKey();
        Apartado entrada = new Apartado(key,"Diego","Fútbol",1);
        mDatabase.child("Apartado").child(key).setValue(entrada);
    }
}
