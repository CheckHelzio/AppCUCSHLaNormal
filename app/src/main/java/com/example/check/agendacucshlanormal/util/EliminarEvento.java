package com.example.check.agendacucshlanormal.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import com.example.check.agendacucshlanormal.R;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class EliminarEvento extends AsyncTaskLoader<Object> {
    private String st_eventos_guardados;

    public EliminarEvento(Context context, String tag) {
        super(context);
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.prefs), Context.MODE_PRIVATE);
        st_eventos_guardados = prefs.getString(context.getString(R.string.prefs_st_eventos_guardados), "").replace(tag, "");
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public String loadInBackground() {
        try {
            URL url = new URL("http://148.202.6.72/aplicacion/datos2.php");
            HttpURLConnection aaaaa = (HttpURLConnection) url.openConnection();
            aaaaa.setReadTimeout(0);
            aaaaa.setConnectTimeout(0);
            aaaaa.setRequestMethod("POST");
            aaaaa.setDoInput(true);
            aaaaa.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("comentarios", st_eventos_guardados);
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
                return st_eventos_guardados;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
