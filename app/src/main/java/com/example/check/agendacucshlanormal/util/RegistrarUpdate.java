package com.example.check.agendacucshlanormal.util;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;


public class RegistrarUpdate extends AsyncTaskLoader<Object> {


    public RegistrarUpdate(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public String loadInBackground() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yy HH:mm:ss", Locale.getDefault());
        String st_update = format.format(c.getTime());

        try {
            URL url = new URL("http://148.202.6.72/aplicacion/update.php");
            HttpURLConnection aaaaa = (HttpURLConnection) url.openConnection();
            aaaaa.setReadTimeout(0);
            aaaaa.setConnectTimeout(0);
            aaaaa.setRequestMethod("POST");
            aaaaa.setDoInput(true);
            aaaaa.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("comentarios", st_update);
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
                return st_update;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
