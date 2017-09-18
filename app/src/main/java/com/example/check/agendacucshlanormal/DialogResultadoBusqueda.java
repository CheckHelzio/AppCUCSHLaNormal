package com.example.check.agendacucshlanormal;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class DialogResultadoBusqueda extends Activity {

    RelativeLayout fondo;
    RecyclerView rvEventos;
    TextView tv_mensaje_no_eventos;
    TextView tv_mensaje_con_eventos;
    TextView tv_num_dia;
    private List<Eventos> listaEventos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_lista_eventos_busqueda);
        postponeEnterTransition();

        fondo = findViewById(R.id.fondo);
        rvEventos = findViewById(R.id.recycle);
        tv_mensaje_no_eventos = findViewById(R.id.tv_mensaje_no_evento);
        tv_mensaje_con_eventos = findViewById(R.id.tv_mensaje_con_evento);
        tv_num_dia = findViewById(R.id.tv_num_dia);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvEventos.setLayoutManager(mLayoutManager);

        inicializarDatos();

        Slide slide = new Slide(Gravity.BOTTOM);
        slide.setInterpolator(AnimUtils.getLinearOutSlowInInterpolator(DialogResultadoBusqueda.this));
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        slide.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(slide);

        startPostponedEnterTransition();
    }

    private void inicializarDatos() {
        try {
            listaEventos = getIntent().getParcelableArrayListExtra("LISTA_EVENTOS");

            if (listaEventos.size() > 0) {
                tv_mensaje_no_eventos.setVisibility(View.GONE);
                tv_mensaje_con_eventos.setVisibility(View.GONE);
            } else {
                tv_mensaje_con_eventos.setVisibility(View.GONE);
                tv_mensaje_no_eventos.setText(R.string.no_hay_coincidencias);
                tv_mensaje_no_eventos.setVisibility(View.VISIBLE);
            }
            iniciarAdaptador();

        } catch (Exception ignored) {

        }

    }

    private void iniciarAdaptador() {
        EventosAdaptadorBusqueda adaptador = new EventosAdaptadorBusqueda(listaEventos, DialogResultadoBusqueda.this);
        rvEventos.setAdapter(adaptador);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        dismiss(null);
    }

    public void dismiss(View view) {
        finishAfterTransition();
    }

}
