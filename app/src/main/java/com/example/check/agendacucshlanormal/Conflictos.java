package com.example.check.agendacucshlanormal;

public class Conflictos {
    private int num_fecha;
    private int num_fecha_2;
    private String tipo;
    private Eventos queEvento;
    private String auditorio;

    Conflictos(int n, Eventos e, String ad) {
        this.num_fecha = n;
        this.queEvento = e;
        this.auditorio = ad;
    }

    Conflictos(int num_fecha, Eventos queEvento) {
        this.num_fecha = num_fecha;
        this.queEvento = queEvento;
    }

    Conflictos(int num_fecha, int num_fecha_2, String tipo) {
        this.num_fecha = num_fecha;
        this.num_fecha_2 = num_fecha_2;
        this.tipo = tipo;
    }

    public String getAuditorio() {
        return auditorio;
    }

    public void setAuditorio(String auditorio) {
        this.auditorio = auditorio;
    }

    String getTipo() {
        return tipo;
    }

    int getNum_fecha() {
        return num_fecha;
    }

    Eventos getQueEvento() {
        return queEvento;
    }

    int getNum_fecha_2() {
        return num_fecha_2;
    }
}
