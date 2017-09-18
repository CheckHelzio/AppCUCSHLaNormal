package com.example.check.agendacucshlanormal;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DialogListaEventosHelzio extends Activity {

    private static int REGISTRAR = 1313;
    private RelativeLayout fondo;
    private RecyclerView rvEventos;
    private TextView tv_mensaje_no_eventos;
    private TextView tv_mensaje_con_eventos;
    private TextView tv_num_dia;
    private TextView tv_nom_dia;
    private List<Eventos> listaEventos;
    private EventosAdaptador adaptador;
    private Boolean animando = false;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_lista_eventos);
        postponeEnterTransition();

        tv_num_dia = findViewById(R.id.tv_num_dia);
        tv_nom_dia = findViewById(R.id.tv_nom_dia);
        tv_mensaje_con_eventos = findViewById(R.id.tv_mensaje_con_evento);
        tv_mensaje_no_eventos = findViewById(R.id.tv_mensaje_no_evento);
        rvEventos = findViewById(R.id.recycle);
        fondo = findViewById(R.id.fondo);

        handler = new Handler();
        tv_num_dia.setText(getIntent().getStringExtra("DIA_MES"));
        String nom = getIntent().getStringExtra("NOMBRE_DIA").substring(0, 3) + ".";
        tv_nom_dia.setText(nom);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvEventos.setLayoutManager(mLayoutManager);

        inicializarDatos();

        fondo.postDelayed(() -> {
            final Rect endBounds = new Rect(fondo.getLeft(), fondo.getTop(), fondo.getRight(), fondo.getBottom());
            ChangeBoundBackground.setup(DialogListaEventosHelzio.this, fondo, endBounds, getViewBitmap(fondo));
            getWindow().getSharedElementEnterTransition();
            startPostponedEnterTransition();
        }, 30);
    }

    private void inicializarDatos() {
        try {
            listaEventos = getIntent().getParcelableArrayListExtra("LISTA_EVENTOS");

            if (listaEventos.size() > 0) {
                tv_mensaje_no_eventos.setVisibility(View.GONE);
                if (getIntent().getBooleanExtra("REGISTRAR", false)) {
                    tv_mensaje_con_eventos.setText(R.string.toca_resgitrar_evento);
                    tv_mensaje_con_eventos.setTextColor(Color.BLACK);
                    tv_num_dia.setTextColor(Color.BLACK);
                    tv_nom_dia.setTextColor(Color.BLACK);

                } else {
                    tv_mensaje_con_eventos.setVisibility(View.GONE);
                }
            } else {
                tv_mensaje_con_eventos.setVisibility(View.GONE);
                tv_mensaje_no_eventos.setVisibility(View.VISIBLE);
                if (getIntent().getBooleanExtra("REGISTRAR", false)) {
                    tv_mensaje_no_eventos.setText(R.string.no_eventos_registra);
                    tv_mensaje_no_eventos.setTextColor(Color.BLACK);
                    tv_num_dia.setTextColor(Color.BLACK);
                    tv_nom_dia.setTextColor(Color.BLACK);
                } else {
                    tv_mensaje_no_eventos.setText(R.string.no_hay_eventos);
                }
            }
            iniciarAdaptador();

        } catch (Exception ignored) {

        }

        if (getIntent().getBooleanExtra("ES_HOY", false)) {
            tv_num_dia.setTextColor(getResources().getColor(R.color.colorAcento));
            tv_nom_dia.setTextColor(getResources().getColor(R.color.colorAcento));
            tv_mensaje_con_eventos.setTextColor(getResources().getColor(R.color.colorAcento));
            tv_mensaje_no_eventos.setTextColor(getResources().getColor(R.color.colorAcento));
        }

        tv_mensaje_no_eventos.setOnClickListener(view -> RegistrarEvento());
        tv_mensaje_con_eventos.setOnClickListener(view -> RegistrarEvento());
    }

    private void RegistrarEvento() {
        if (getIntent().getBooleanExtra("REGISTRAR", false)) {
            Intent intent = new Intent(this, RegistrarEvento.class);
            intent.putExtra("DIA_AÑO", getIntent().getIntExtra("DIA_AÑO", 0));
            intent.putExtra("DONDE", "LISTA");
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
            startActivityForResult(intent, REGISTRAR, bundle);
        }
    }


    private void iniciarAdaptador() {
        Log.v("LISTA EVENTOS", "Numero de eventos: " + listaEventos.size());
        adaptador = new EventosAdaptador(listaEventos, DialogListaEventosHelzio.this);
        rvEventos.setAdapter(adaptador);
    }

    @Override
    public void onBackPressed() {
        dismiss(null);
    }

    public void dismiss(View view) {
        if (animando) {
            Log.v("ANIMACION", "SIN ANIMACION");
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            finishAfterTransition();
            Log.v("ANIMACION", "CON ANIMACION");
        }
    }

    private Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handler.postDelayed(() -> {
            if (requestCode == REGISTRAR) {
                if (resultCode == RESULT_OK) {
                    animando = true;
                    ArrayList<Eventos> listaDeEventos = data.getParcelableArrayListExtra("LISTA");
                    for (Eventos e : listaDeEventos) {
                        adaptador.addItem(e);
                    }

                    adaptador.ordenarItems();
                }
                Inicio.esperar = false;
            } else {
                animando = true;
                if (resultCode == RESULT_OK) {
                    Log.v("ELIMINAR", "REGRESANDOA  LISTA... RESULTADO OK");
                    listaEventos.remove(data.getIntExtra("POSITION", 0));
                    rvEventos.removeViewAt(data.getIntExtra("POSITION", 0));
                    adaptador.removeItemAtPosition(data.getIntExtra("POSITION", 0));
                } else if (resultCode == RESULT_FIRST_USER) {
                    listaEventos = data.getParcelableArrayListExtra("LISTA");
                    adaptador = new EventosAdaptador(listaEventos, DialogListaEventosHelzio.this);
                    rvEventos.setAdapter(adaptador);
                    rvEventos.invalidate();
                }

                Log.v("ELIMINAR", "ESPERANDO = FALSE");
                Inicio.esperar = false;

            }
        }, 150);
    }
}
