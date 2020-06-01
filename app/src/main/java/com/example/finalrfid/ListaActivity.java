package com.example.finalrfid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.finalrfid.firebase.Apartado;
import com.example.finalrfid.firebase.ApartadoRecyclerApater;
import com.example.finalrfid.firebase.CanchaFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class ListaActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private ApartadoRecyclerApater apartadoRecyclerApater;
    private RecyclerView mRecyclerView;
    private ArrayList<Apartado> mApartado = new ArrayList<Apartado>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        mRecyclerView = (RecyclerView) findViewById(R.id.list_recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        getApartados();
    }
    public void onClick2(View view){
        finish();
    }

    private void getApartados(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Apartado").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        String nombre = ds.child("nombre").getValue().toString();
                        String matricula = ds.child("matricula").getValue().toString();
                        String cancha = ds.child("cancha").getValue().toString();
                        String horas = ds.child("horas").getValue().toString();
                        int horasPagadas = Integer.parseInt(ds.child("horasPagadas").getValue().toString());
                        mApartado.add(new Apartado(" ",nombre,matricula,cancha,horas,horasPagadas));
                    }
                    apartadoRecyclerApater = new ApartadoRecyclerApater(mApartado,R.layout.renglon_apartado);
                    mRecyclerView.setAdapter(apartadoRecyclerApater);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }
}
