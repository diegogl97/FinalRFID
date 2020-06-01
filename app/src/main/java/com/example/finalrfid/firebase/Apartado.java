package com.example.finalrfid.firebase;

public class Apartado {
    public String id;
    public String nombre;
    public String matricula;
    public String cancha;
    public String horas;
    public int horasPagadas;

    public Apartado(){

    }

    public Apartado(String id, String nombre, String matricula,String cancha, String horas,int horasPagadas){
        this.id = id;
        this.nombre = nombre;
        this.matricula = matricula;
        this.cancha = cancha;
        this.horas = horas;
        this.horasPagadas = horasPagadas;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getCancha() {
        return cancha;
    }

    public void setCancha(String cancha) {
        this.cancha = cancha;
    }

    public String getHoras() {
        return horas;
    }

    public void setHoras(String horas) {
        this.horas = horas;
    }

    public int getHorasPagadas() {
        return horasPagadas;
    }

    public void setHorasPagadas(int horasPagadas) {
        this.horasPagadas = horasPagadas;
    }
}

