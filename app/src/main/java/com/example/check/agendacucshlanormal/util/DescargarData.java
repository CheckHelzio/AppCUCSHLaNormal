package com.example.check.agendacucshlanormal.util;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class DescargarData extends AsyncTaskLoader<Object> {
    private static final String TAG = DescargarData.class.getSimpleName();
    private String url;

    public DescargarData(Context context, String Url) {
        super(context);
        url = Url;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public String loadInBackground() {
        try {
            return loadFromNetwork(url);
        } catch (IOException e) {
            Log.e(TAG, "loadInBackground: " + e);
            return null;
        }
    }

    private String loadFromNetwork(String urlString) throws IOException {
        InputStream stream;
        String str = "";
        try {
            stream = downloadUrl(urlString);
            str = readIt(stream);
            assert stream != null;
            stream.close();
        } catch (Exception e) {
            Log.e(TAG, "loadFromNetwork: " + e);
        }

        return str;
    }

    private InputStream downloadUrl(String urlString) throws IOException {
        Log.v("ULR 3", urlString);
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(0);
        conn.setConnectTimeout(0);
        return conn.getInputStream();
    }

    private String readIt(InputStream stream) throws IOException {
        StringBuilder a = new StringBuilder();
        String linea;
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        while ((linea = br.readLine()) != null) {
            a.append(linea);
        }
        return a.toString();
    }
}
