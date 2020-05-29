package com.example.finalrfid.firebase;

public class Apartado {
    public String id;
    public String nombre;
    public String cancha;
    public int horas;

    public Apartado(){

    }

    public Apartado(String id, String nombre, String cancha, int horas){
        this.id = id;
        this.nombre = nombre;
        this.cancha = cancha;
        this.horas = horas;
    }
}

