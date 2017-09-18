package com.example.check.agendacucshlanormal;

import android.os.Parcel;
import android.os.Parcelable;

public class Eventos implements Parcelable {
    public static final Creator<Eventos> CREATOR = new Creator<Eventos>() {
        @Override
        public Eventos createFromParcel(Parcel source) {
            return new Eventos(source);
        }

        @Override
        public Eventos[] newArray(int size) {
            return new Eventos[size];
        }
    };
    private String fecha;
    private String horaInicial;
    private String horaFinal;
    private String titulo;
    private String auditorio;
    private String tipoEvento;
    private String nombreOrganizador;
    private String numTelOrganizador;
    private String statusEvento;
    private String quienR;
    private String cuandoR;
    private String notas;
    private String id;
    private String tag;
    private int fondo;

    public Eventos(String fecha, String horaInicial, String horaFinal, String titulo, String auditorio, String tipoEvento, String nombreOrganizador, String numTelOrganizador, String statusEvento, String quienR, String cuandoR, String notas, String id, String tag, int fondo) {
        this.fecha = fecha;
        this.horaInicial = horaInicial;
        this.horaFinal = horaFinal;
        this.titulo = titulo;
        this.auditorio = auditorio;
        this.tipoEvento = tipoEvento;
        this.nombreOrganizador = nombreOrganizador;
        this.numTelOrganizador = numTelOrganizador;
        this.statusEvento = statusEvento;
        this.quienR = quienR;
        this.cuandoR = cuandoR;
        this.notas = notas;
        this.id = id;
        this.tag = tag;
        this.fondo = fondo;
    }

    private Eventos(Parcel in) {
        this.fecha = in.readString();
        this.horaInicial = in.readString();
        this.horaFinal = in.readString();
        this.titulo = in.readString();
        this.auditorio = in.readString();
        this.tipoEvento = in.readString();
        this.nombreOrganizador = in.readString();
        this.numTelOrganizador = in.readString();
        this.statusEvento = in.readString();
        this.quienR = in.readString();
        this.cuandoR = in.readString();
        this.notas = in.readString();
        this.id = in.readString();
        this.tag = in.readString();
        this.fondo = in.readInt();
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHoraInicial() {
        return horaInicial;
    }

    public String getHoraFinal() {
        return horaFinal;
    }

    String getTitulo() {
        return titulo;
    }

    String getAuditorio() {
        return auditorio;
    }

    public void setAuditorio(String auditorio) {
        this.auditorio = auditorio;
    }

    String getTipoEvento() {
        return tipoEvento;
    }

    String getNombreOrganizador() {
        return nombreOrganizador;
    }

    String getNumTelOrganizador() {
        return numTelOrganizador;
    }

    String getStatusEvento() {
        return statusEvento;
    }

    String getQuienR() {
        return quienR;
    }

    String getCuandoR() {
        return cuandoR;
    }

    String getNotas() {
        return notas;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getFondo() {
        return fondo;
    }

    public void setFondo(int fondo) {
        this.fondo = fondo;
    }

    public String aTag() {
        String t = "";

        t += this.fecha + "::";
        t += this.horaInicial + "::";
        t += this.horaFinal + "::";
        t += this.titulo + "::";
        t += this.auditorio + "::";
        t += this.tipoEvento + "::";
        t += this.nombreOrganizador + "::";
        t += this.numTelOrganizador + "::";
        t += this.statusEvento + "::";
        t += this.quienR + "::";
        t += this.cuandoR + "::";
        t += this.notas + "::";
        t += this.id;

        return t;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fecha);
        dest.writeString(this.horaInicial);
        dest.writeString(this.horaFinal);
        dest.writeString(this.titulo);
        dest.writeString(this.auditorio);
        dest.writeString(this.tipoEvento);
        dest.writeString(this.nombreOrganizador);
        dest.writeString(this.numTelOrganizador);
        dest.writeString(this.statusEvento);
        dest.writeString(this.quienR);
        dest.writeString(this.cuandoR);
        dest.writeString(this.notas);
        dest.writeString(this.id);
        dest.writeString(this.tag);
        dest.writeInt(this.fondo);
    }
}
