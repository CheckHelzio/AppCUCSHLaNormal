package com.example.check.agendacucshlanormal.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.example.check.agendacucshlanormal.Eventos;
import com.example.check.agendacucshlanormal.Fecha;
import com.example.check.agendacucshlanormal.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;


public class GuardarEvento extends AsyncTaskLoader<Object> {

    private Bundle bundle;
    private StringBuilder st_eventos_guardados;
    private ArrayList<Eventos> listaDeEventosNuevos;
    private Eventos eventoEditar;

    public GuardarEvento(Context context, Bundle args, Eventos evento) {
        super(context);
        bundle = args;
        eventoEditar = evento;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        Calendar calendarioRegistro = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("d/MM/yyyy'~'h:mm a", Locale.getDefault());

        ArrayList<Fecha> listaFechas = bundle.getParcelableArrayList("LISTA FECHAS");
        listaDeEventosNuevos = new ArrayList<>();
        String st_titulo = bundle.getString("TITULO");
        String st_auditorio = bundle.getString("AUDITORIO");
        String st_tipo_evento = bundle.getString("TIPO DE EVENTO");
        String st_nombre_org = bundle.getString("NOMBRE DEL ORGANIZADOR");
        String st_num_tel = bundle.getString("NUMERO TELEFONICO");
        String st_estatus = bundle.getString("ESTATUS DEL EVENTO");
        String st_quien = bundle.getString("QUIEN REGISTRO");
        String st_nota = bundle.getString("NOTA");
        String st_fondo = bundle.getString("FONDO");
        String folio = Data.getStringFolio(bundle.getInt("FOLIO"));

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Eventos>>() {
        }.getType();
        SharedPreferences prefs = getContext().getSharedPreferences(getContext().getString(R.string.prefs), Context.MODE_PRIVATE);
        ArrayList<Eventos> listaEventos = gson.fromJson(prefs.getString(getContext().getResources().getString(R.string.prefs_lista_eventos), ""), type);

        int int_fecha = bundle.getInt("FECHA DEL DIA");

        if (eventoEditar != null) {
            int x = 0;
            for (Eventos e : listaEventos) {
                if (e.aTag().equals(eventoEditar.aTag())) {
                    break;
                }
                x++;
            }
            listaEventos.remove(x);
        }

        assert listaFechas != null;
        for (Fecha f : listaFechas) {
            Eventos nuevoEvento = new Eventos(
                    // FECHA
                    "" + f.getDia(),
                    // HORA INCIAL
                    "" + f.getHoraInicial(),
                    // HORA FINAL
                    "" + f.getHoraFinal(),
                    // TITULO
                    st_titulo,
                    // AUDITORIO
                    st_auditorio,
                    // TIPO DE EVENTO
                    st_tipo_evento,
                    // NOMBRE DEL ORGANIZADOR
                    st_nombre_org,
                    // NUMERO TELEFONICO DEL ORGANIZADOR
                    st_num_tel,
                    // STATUS DEL EVENTO
                    st_estatus,
                    // QUIEN REGISTRO
                    st_quien,
                    // CUANDO REGISTRO
                    format.format(calendarioRegistro.getTime()),
                    // NOTAS
                    st_nota,
                    // ID
                    folio,
                    // TAG
                    "",
                    // FONDO
                    Data.getFondoAuditorio(st_fondo, getContext())
            );
            if (f.getDia() == int_fecha) {
                listaDeEventosNuevos.add(nuevoEvento);
            }
            listaEventos.add(nuevoEvento);
        }

        Collections.sort(listaEventos, (e1, e2) -> {
            Integer i1 = Integer.parseInt(e1.getFecha().replaceAll("[^0-9]+", ""));
            Integer i2 = Integer.parseInt(e2.getFecha().replaceAll("[^0-9]+", ""));
            if (Objects.equals(i1, i2)) {
                Integer i3 = Integer.parseInt(e1.getHoraInicial());
                Integer i4 = Integer.parseInt(e2.getHoraInicial());
                if (Objects.equals(i3, i4)) {
                    Integer i5 = Integer.parseInt(e1.getHoraFinal());
                    Integer i6 = Integer.parseInt(e2.getHoraFinal());
                    return i5.compareTo(i6);
                } else {
                    return i3.compareTo(i4);
                }
            } else {
                return i1.compareTo(i2);
            }
        });

        st_eventos_guardados = new StringBuilder();
        for (Eventos item : listaEventos) {
            st_eventos_guardados.append(item.aTag()).append("¦");
        }

        forceLoad();
    }

    @Override
    public Object loadInBackground() {

        try {
            URL url = new URL("http://148.202.6.72/aplicacion/datos2.php");
            HttpURLConnection aaaaa = (HttpURLConnection) url.openConnection();
            aaaaa.setReadTimeout(0);
            aaaaa.setConnectTimeout(0);
            aaaaa.setRequestMethod("POST");
            aaaaa.setDoInput(true);
            aaaaa.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("comentarios", st_eventos_guardados.toString());
            String query = builder.build().getEncodedQuery();

            OutputStream os = aaaaa.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            aaaaa.connect();

            int aaaaaaa = aaaaa.getResponseCode();
            if (aaaaaaa == HttpsURLConnection.HTTP_OK) {
                return listaDeEventosNuevos;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
