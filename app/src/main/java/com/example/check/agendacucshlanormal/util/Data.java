package com.example.check.agendacucshlanormal.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.check.agendacucshlanormal.Eventos;
import com.example.check.agendacucshlanormal.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Data {

    public static int getFondoAuditorio(String numero, Context context) {
        switch (numero) {
            case "1":
                return context.getResources().getColor(R.color.ed_a);
            case "2":
                return context.getResources().getColor(R.color.ed_b);
            case "3":
                return context.getResources().getColor(R.color.ed_c);
            case "4":
                return context.getResources().getColor(R.color.ed_d);
            case "5":
                return context.getResources().getColor(R.color.ed_e);
            default:
                return context.getResources().getColor(R.color.ed_a);
        }
    }

    static String getStringFolio(int folio) {
        String stNuevoId = "" + (folio + 1);
        if (stNuevoId.length() == 1) {
            stNuevoId = "000" + stNuevoId;
        } else if (stNuevoId.length() == 2) {
            stNuevoId = "00" + stNuevoId;
        } else if (stNuevoId.length() == 3) {
            stNuevoId = "0" + stNuevoId;
        }
        return stNuevoId;
    }

    public static ArrayList<Eventos> getListaEventos(Context context) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Eventos>>() {
        }.getType();
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.prefs), Context.MODE_PRIVATE);
        return gson.fromJson(prefs.getString(context.getResources().getString(R.string.prefs_lista_eventos), ""), type);
    }


}
