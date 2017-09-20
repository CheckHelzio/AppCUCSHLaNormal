package com.example.check.agendacucshlanormal.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Log;

import com.example.check.agendacucshlanormal.Eventos;
import com.example.check.agendacucshlanormal.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;


public class LlenarListaEventos extends AsyncTaskLoader<Object> {

    private static final String TAG = LlenarListaEventos.class.getSimpleName();
    private String st_data;
    private String st_titulos;
    private String st_tiposDeEvento;
    private String st_nombresOrganizador;
    private int id_prox;
    private ArrayList<Eventos> lista_eventos = new ArrayList<>();
    private SharedPreferences prefs;
    private String[] eventos = new String[3660];

    public LlenarListaEventos(Context context, String data) {
        super(context);
        st_data = data;
        prefs = context.getSharedPreferences(context.getString(R.string.prefs), Context.MODE_PRIVATE);
        st_titulos = prefs.getString(context.getString(R.string.prefs_titulos), "");
        st_tiposDeEvento = prefs.getString(context.getString(R.string.prefs_tipos_evento), "");
        st_nombresOrganizador = prefs.getString(context.getString(R.string.prefs_nombres_organizador), "");
        id_prox = prefs.getInt(context.getString(R.string.prefs_id_prox), 0);
        Arrays.fill(eventos, "");
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public Object loadInBackground() {

        StringBuilder stb_titulos = new StringBuilder();
        stb_titulos.append(st_titulos);
        StringBuilder stb_tiposDeEvento = new StringBuilder();
        stb_tiposDeEvento.append(st_tiposDeEvento);
        StringBuilder stb_nombresOrganizador = new StringBuilder();
        stb_nombresOrganizador.append(st_nombresOrganizador);

        try {
            for (String eventos_suelto : st_data.trim().split("¦")) {
                if (!TextUtils.isEmpty(eventos_suelto)) {

                    st_titulos = stb_titulos.toString();
                    st_tiposDeEvento = stb_tiposDeEvento.toString();
                    st_nombresOrganizador = stb_nombresOrganizador.toString();

                    if (!st_titulos.contains(eventos_suelto.trim().split("::")[3].trim())) {
                        stb_titulos.append(eventos_suelto.trim().split("::")[3].trim()).append("¦");
                    }
                    if (!st_tiposDeEvento.contains(eventos_suelto.trim().split("::")[5].trim())) {
                        stb_tiposDeEvento.append(eventos_suelto.trim().split("::")[5].trim()).append("¦");
                    }
                    if (!st_nombresOrganizador.contains(eventos_suelto.trim().split("::")[6].trim())) {
                        stb_nombresOrganizador.append(eventos_suelto.trim().split("::")[6].trim()).append("¦");
                    }

                    Eventos nuevo_evento = new Eventos(
                            // FECHA
                            eventos_suelto.split("::")[0].trim().replaceAll("[^0-9]+", ""),
                            // HORA INCIAL
                            eventos_suelto.split("::")[1].trim().replaceAll("[^0-9]+", ""),
                            // HORA FINAL
                            eventos_suelto.split("::")[2].trim().replaceAll("[^0-9]+", ""),
                            // TITULO
                            eventos_suelto.split("::")[3].trim(),
                            // AUDITORIO
                            eventos_suelto.split("::")[4].trim().replaceAll("[^0-9]+", ""),
                            // TIPO DE EVENTO
                            eventos_suelto.split("::")[5].trim(),
                            // NOMBRE DEL ORGANIZADOR
                            eventos_suelto.split("::")[6].trim(),
                            // NUMERO TELEFONICO DEL ORGANIZADOR
                            eventos_suelto.split("::")[7].trim(),
                            // STATUS DEL EVENTO
                            eventos_suelto.split("::")[8].trim(),
                            // QUIEN REGISTRO
                            eventos_suelto.split("::")[9].trim(),
                            // CUANDO REGISTRO
                            eventos_suelto.split("::")[10].trim(),
                            // NOTAS
                            eventos_suelto.split("::")[11].trim(),
                            // ID
                            eventos_suelto.split("::")[12].trim().replaceAll("[^0-9]+", ""),
                            // TAG
                            eventos_suelto.trim(),
                            // FONDO
                            Data.getFondoAuditorio(eventos_suelto.split("::")[4].trim(), getContext())
                    );

                    if (!eventos[Integer.parseInt(eventos_suelto.split("::")[0].trim().replaceAll("[^0-9]+", ""))].contains(eventos_suelto)) {
                        lista_eventos.add(nuevo_evento);
                        eventos[Integer.parseInt(eventos_suelto.split("::")[0].trim().replaceAll("[^0-9]+", ""))] += eventos_suelto + "¦";
                    }

                    // COMPROBAMOS EL ID DE CADA EVENTO PARA DETERMINAR SI ES MAYOR AL ANTERIOR Y AL FINAL OBTENER EL ID MAS ALTO
                    if (Integer.parseInt(eventos_suelto.split("::")[12].trim()) > id_prox) {
                        id_prox = Integer.parseInt(eventos_suelto.split("::")[12].trim());
                    }

                }
            }

            prefs.edit().putInt(getContext().getString(R.string.prefs_id_prox), id_prox).apply();
            prefs.edit().putString(getContext().getString(R.string.prefs_nombres_organizador), stb_nombresOrganizador.toString()).apply();
            prefs.edit().putString(getContext().getString(R.string.prefs_tipos_evento), stb_tiposDeEvento.toString()).apply();
            prefs.edit().putString(getContext().getString(R.string.prefs_titulos), stb_titulos.toString()).apply();

            Gson gson = new Gson();
            String lista_json = gson.toJson(lista_eventos);
            prefs.edit().putString(getContext().getString(R.string.prefs_lista_eventos), lista_json).apply();

            String array_json = gson.toJson(eventos);
            prefs.edit().putString(getContext().getString(R.string.prefs_array_eventos), array_json).apply();

            return eventos;
        } catch (Exception e) {
            Log.e(TAG, "loadInBackground: ERROR AL LLENAR LA LISTA DE EVENTOS" + e);
            return null;
        }

    }

}
