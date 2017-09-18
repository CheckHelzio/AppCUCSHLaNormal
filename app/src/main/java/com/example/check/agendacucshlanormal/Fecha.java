package com.example.check.agendacucshlanormal;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;

public class Fecha implements Parcelable {
    public static final Parcelable.Creator<Fecha> CREATOR = new Parcelable.Creator<Fecha>() {
        @Override
        public Fecha createFromParcel(Parcel source) {
            return new Fecha(source);
        }

        @Override
        public Fecha[] newArray(int size) {
            return new Fecha[size];
        }
    };
    private int dia;
    private int horaInicial;
    private int horaFinal;
    private TextView label_inicial;
    private TextView label_final;

    Fecha(int dia, int horaInicial, int horaFinal) {
        this.dia = dia;
        this.horaInicial = horaInicial;
        this.horaFinal = horaFinal;
    }

    private Fecha(Parcel in) {
        this.dia = in.readInt();
        this.horaInicial = in.readInt();
        this.horaFinal = in.readInt();
        this.label_inicial = in.readParcelable(TextView.class.getClassLoader());
        this.label_final = in.readParcelable(TextView.class.getClassLoader());
    }

    public int getDia() {
        return dia;
    }

    public int getHoraInicial() {
        return horaInicial;
    }

    void setHoraInicial(int horaInicial) {
        this.horaInicial = horaInicial;
    }

    public int getHoraFinal() {
        return horaFinal;
    }

    void setHoraFinal(int horaFinal) {
        this.horaFinal = horaFinal;
    }

    TextView getLabel_inicial() {
        return label_inicial;
    }

    void setLabel_inicial(TextView label_inicial) {
        this.label_inicial = label_inicial;
    }

    TextView getLabel_final() {
        return label_final;
    }

    void setLabel_final(TextView label_final) {
        this.label_final = label_final;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.dia);
        dest.writeInt(this.horaInicial);
        dest.writeInt(this.horaFinal);
        dest.writeParcelable((Parcelable) this.label_inicial, flags);
        dest.writeParcelable((Parcelable) this.label_final, flags);
    }
}
