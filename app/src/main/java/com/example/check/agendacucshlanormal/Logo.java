package com.example.check.agendacucshlanormal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.example.check.agendacucshlanormal.util.DescargarData;
import com.example.check.agendacucshlanormal.util.LlenarListaEventos;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Logo extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Object> {


    private SharedPreferences prefs;
    private String st_update;
    private String st_data;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //DIBUJAR EL LOGO DEBAJO DE LA STATUS BAR
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // CARGAMOS LAS PREFERENCIAS
        prefs = getSharedPreferences(getString(R.string.prefs), Context.MODE_PRIVATE);
        st_update = prefs.getString("UPDATE", "");

        bundle = new Bundle();
        bundle.putString("URL", getString(R.string.url_update));
        getSupportLoaderManager().initLoader(0, bundle, this);
    }

    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case 2:
                return new LlenarListaEventos(this, st_data);
            default:
                return new DescargarData(this, args.getString("URL"));
        }
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {
        switch (loader.getId()) {
            default:
                if (data != null) {
                    if (!data.toString().equals(st_update)) {

                        // La informacion esta desactualizada || descargemos la data actual
                        prefs.edit().putString("UPDATE", data.toString()).apply();
                        bundle.putString("URL", getString(R.string.url_datos));
                        getSupportLoaderManager().initLoader(1, bundle, this);
                    } else {
                        // Guardamos la hora de actualizacion
                        SimpleDateFormat format = new SimpleDateFormat("'Actualizado el 'd 'de' MMMM 'del' yyyy 'a las' h:mm a", Locale.forLanguageTag("es-MX"));
                        prefs.edit().putString("ACTUALIZACION", format.format(Calendar.getInstance().getTime())).apply();
                        // La informacion esta actualizada || Iniciamos la aplicacion
                        Intent i = new Intent(Logo.this, Inicio.class);
                        startActivity(i);
                        finish();
                    }
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(data.toString())) {

                    // MUCHAS VECES LA BASE DE DATOS ES DESCARGADA CON CODIGO HTML QUE NO NECESITOS, POR ESO AQUI LO REEMPLAZAMOS
                    if (data.toString().contains("</form>")) {
                        data = data.toString().split("</form>")[1].trim();
                    }
                    st_data = data.toString();
                    prefs.edit().putString(getString(R.string.prefs_st_eventos_guardados), data.toString()).apply();

                    // Guardamos la hora de actualizacion
                    SimpleDateFormat format = new SimpleDateFormat("'Actualizado el 'd 'de' MMMM 'del' yyyy 'a las' h:mm a", Locale.forLanguageTag("es-MX"));
                    prefs.edit().putString("ACTUALIZACION", format.format(Calendar.getInstance().getTime())).apply();

                    // Una vez descargada la data nueva llenamos la lista de incidentes
                    getSupportLoaderManager().initLoader(2, null, this);
                }
                break;
            case 2:
                // La informacion esta actualizada || Iniciamos la aplicacion
                Intent i = new Intent(Logo.this, Inicio.class);
                startActivity(i);
                finish();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }
}
