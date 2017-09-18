package com.example.check.agendacucshlanormal;

import android.animation.Animator;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.check.agendacucshlanormal.util.Data;
import com.example.check.agendacucshlanormal.util.RegistrarUpdate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Objects;

public class RegistrarEvento extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Object> {

    private static final String TAG = RegistrarEvento.class.getSimpleName();
    private final static int INICIAL = 333;
    private final static int AGREGAR = 334;
    protected ArrayList<Fecha> listaFechas = new ArrayList<>();
    protected ArrayList<Eventos> listaEventos = new ArrayList<>();
    protected ArrayList<Conflictos> listaConflictos = new ArrayList<>();
    private View color_reveal;
    private AutoCompleteTextView atv_titulo_evento;
    private AutoCompleteTextView atv_tipo_evento;
    private AutoCompleteTextView atv_nombre_org;
    private RelativeLayout full_header;
    private Spinner sp_auditorios;
    private TextView tv_tipo_evento_label;
    private TextView tv_nom_org_label;
    private TextView tv_contraseña_label;
    private EditText et_num_tel;
    private EditText et_contraseña;
    private EditText et_nota;
    private RecyclerView rv_conflictos;
    private RecyclerView rv_fechas;
    private CoordinatorLayout snackposs;
    private RelativeLayout conteConflictos;
    private Eventos evento = null;
    private Handler handler;
    private boolean pin_correcto_eliminar = false;
    private Intent i;
    private String AD;
    private String st_quien;
    private int int_fecha;
    private int unavez = 0;
    private Runnable mRunnable;
    private SharedPreferences prefs;
    private ArrayList<Eventos> listaDeEventosNuevos;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //PONEMOS LAYOT QUE VAMOS A USAR EN ESTA ACTIVITY
        setContentView(R.layout.dialog_editar_evento);
        RelativeLayout sfondo = findViewById(R.id.ly);

        try {
            evento = getIntent().getExtras().getParcelable("EVENTO");
        } catch (Exception ignored) {
        }

        //OCULTAR EL TECLADO PARA QUE NO SE ABRA AL INICIAR LA ACTIVITY
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (getIntent().getStringExtra("DONDE").equals("PRINCIPAL")) {
            Log.d(TAG, "onCreate: DONDE - PRINCIPAL");
            //ANIMACION DE FAB A DIALOG
            FabTransition.setup(this, sfondo);
            getWindow().getSharedElementEnterTransition();
        } else {
            Log.d(TAG, "onCreate: DONDE - NO PRINCIPAL");
            i = getIntent();
            //POSPONEMOS LA ANIMACION DE TRANSICION PARA AGREGAR UNA PERSONALIZADA
            postponeEnterTransition();

            // CREAMOS LA TRANSICION DE ENTRADA Y LA INICIAMOS
            Slide slide = new Slide(Gravity.BOTTOM);
            slide.setInterpolator(AnimUtils.getLinearOutSlowInInterpolator(RegistrarEvento.this));
            slide.excludeTarget(android.R.id.statusBarBackground, true);
            slide.excludeTarget(android.R.id.navigationBarBackground, true);
            getWindow().setEnterTransition(slide);
            full_header = findViewById(R.id.toolbar_dialog);
            full_header.setBackgroundColor(Data.getFondoAuditorio("1", this));
            startPostponedEnterTransition();
        }

        //INICIAMOS TODOS LOS VIEWS QUE VAMOS A UTILIZAR
        iniciarObjetos();
        iniciarDatos();
    }

    private void iniciarObjetos() {
        et_nota = findViewById(R.id.et_nota);
        conteConflictos = findViewById(R.id.conteConflictos);
        full_header = findViewById(R.id.toolbar_dialog);
        atv_titulo_evento = findViewById(R.id.atv_tituto_evento);
        atv_tipo_evento = findViewById(R.id.atv_tipo_evento);
        atv_nombre_org = findViewById(R.id.atv_nombre_org);
        sp_auditorios = findViewById(R.id.sp_auditorios);
        tv_tipo_evento_label = findViewById(R.id.tv_tipo_evento_label);
        tv_contraseña_label = findViewById(R.id.tv_contraseña_label);
        et_contraseña = findViewById(R.id.et_contraseña);
        rv_fechas = findViewById(R.id.rv_fechas);
        rv_conflictos = findViewById(R.id.rv_conflictos);
        color_reveal = findViewById(R.id.color_reveal);
        snackposs = findViewById(R.id.snackposs);
        tv_nom_org_label = findViewById(R.id.tv_nom_org_label);
        TextView tv_guardar_evento = findViewById(R.id.tv_guardar_evento);
        TextView tv_repeticion = findViewById(R.id.tv_repeticion);
        et_num_tel = findViewById(R.id.et_numero_tel);

        tv_guardar_evento.setOnClickListener(view -> registrarEvento());

        tv_repeticion.setOnClickListener(v -> abrirDialogAgregarFechas());


        // CREAMOS UN HANDLER PARA TAREAS CON TIEMPO DE RETRASO
        handler = new Handler();
        mRunnable = this::loopComprobarhoras;
    }

    private void iniciarDatos() {
        prefs = getSharedPreferences(getString(R.string.prefs), Context.MODE_PRIVATE);

        // CONFIGURAR TEXT WATCHERS
        atv_titulo_evento.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceAll("::", "").replace("¦", "");
                if (!s.toString().equals(result)) {
                    atv_titulo_evento.setText(result);
                    atv_titulo_evento.setSelection(result.length());
                }
            }
        });
        atv_tipo_evento.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceAll("::", "").replace("¦", "");
                if (!s.toString().equals(result)) {
                    atv_tipo_evento.setText(result);
                    atv_tipo_evento.setSelection(result.length());
                }

                if (atv_tipo_evento.getText().toString().trim().length() == 0) {
                    tv_tipo_evento_label.setTextColor(Color.RED);
                } else {
                    tv_tipo_evento_label.setTextColor(Color.parseColor("#121212"));
                }
            }
        });
        atv_nombre_org.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceAll("::", "").replace("¦", "");
                if (!s.toString().equals(result)) {
                    atv_nombre_org.setText(result);
                    atv_nombre_org.setSelection(result.length());
                }

                if (atv_nombre_org.getText().toString().trim().length() == 0) {
                    tv_nom_org_label.setTextColor(Color.RED);
                } else {
                    tv_nom_org_label.setTextColor(Color.parseColor("#121212"));
                }
            }
        });
        et_contraseña.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_contraseña.getText().toString().trim().length() == 0) {
                    tv_contraseña_label.setTextColor(Color.RED);
                } else {
                    tv_contraseña_label.setTextColor(Color.parseColor("#121212"));
                }

                switch (et_contraseña.getText().toString()) {
                    case "1308":
                        pin_correcto_eliminar = true;
                        et_contraseña.setTextColor(Color.parseColor("#121212"));
                        st_quien = "Susy";
                        break;
                    case "2886":
                        pin_correcto_eliminar = true;
                        et_contraseña.setTextColor(Color.parseColor("#121212"));
                        st_quien = "Tere";
                        break;
                    case "4343":
                        pin_correcto_eliminar = true;
                        et_contraseña.setTextColor(Color.parseColor("#121212"));
                        st_quien = "CTA";
                        break;
                    default:
                        pin_correcto_eliminar = false;
                        et_contraseña.setTextColor(Color.RED);
                        st_quien = "";
                        break;
                }
            }
        });

        // CONFIGURAR SPINER PARA SELECCIONAR EL AUDITORIO
        String auditorio1 = "Auditorio Salvador Allende";
        String auditorio2 = "Auditorio Silvano Barba";
        String auditorio3 = "Auditorio Carlos Ramírez";
        String auditorio4 = "Auditorio Adalberto Navarro";
        String auditorio5 = "Sala de Juicios Orales Mariano Otero";
        String[] items = new String[]{
                auditorio1, auditorio2, auditorio3, auditorio4, auditorio5};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        sp_auditorios.setAdapter(adapter);
        sp_auditorios.setSelection(0);
        sp_auditorios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                //CONFIGURAR EL COLOR DEL HEADER SEGUN EL AUDITORIO SELECCIOANDO
                colorReveal(Data.getFondoAuditorio((i + 1) + "", RegistrarEvento.this));
                AD = "" + (i + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //TIPO DE EVENTO
        tv_tipo_evento_label.setTextColor(Color.RED);

        // COLOCAMOS COMO FECHA INICIAL LA FECHA DEL DIA DEL EVENTO
        int_fecha = getIntent().getIntExtra("DIA_AÑO", -1);

        //CONFIGUAR NOMBRE DEL ORGANIZADOR
        tv_nom_org_label.setTextColor(Color.RED);

        // CONFIGURAMOS EL EDIT TEXT DE LA CONTRASEÑA
        tv_contraseña_label.setTextColor(Color.RED);

        // CONFIGURAR LA PRIMER FECHA EN LA LISTA DE FECHAS
        if (int_fecha != -1) {
            listaFechas.add(new Fecha(int_fecha, 0, 2));
        }

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv_fechas.setLayoutManager(mLayoutManager);
        FechasAdaptador adaptador = new FechasAdaptador(listaFechas, RegistrarEvento.this);
        rv_fechas.setAdapter(adaptador);

        ItemTouchHelper.Callback callback = new SwipeHelper(adaptador);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(rv_fechas);

        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(this);
        rv_conflictos.setLayoutManager(mLayoutManager2);
        ConflictosAdaptador conflictosAdaptador = new ConflictosAdaptador(listaConflictos, RegistrarEvento.this);
        rv_conflictos.setAdapter(conflictosAdaptador);

        if (evento != null) {
            et_nota.setText(evento.getNotas());
            atv_titulo_evento.setText(evento.getTitulo());
            atv_tipo_evento.setText(evento.getTipoEvento());
            atv_nombre_org.setText(evento.getNombreOrganizador());
            et_num_tel.setText(evento.getNumTelOrganizador());
            sp_auditorios.setSelection(Integer.parseInt(evento.getAuditorio()) - 1);
            int_fecha = Integer.parseInt(evento.getFecha());
            listaFechas.add(new Fecha(int_fecha, Integer.parseInt(evento.getHoraInicial()), Integer.parseInt(evento.getHoraFinal())));

        }

        rv_fechas.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (unavez == 0) {
                String titulos = prefs.getString(getString(R.string.prefs_titulos), "");
                String tipos_de_evento = prefs.getString(getString(R.string.prefs_tipos_evento), "");
                String nombresOrganizador = prefs.getString(getString(R.string.prefs_nombres_organizador), "");

                // DESPUES DE PONER EL TITULO INICIAMOS EL AUTOCOMPLETAR PARA EL NOMBRE DEL EVENTO PARA QUE NO SALGA LA LISTA DE EVENTOS
                ArrayAdapter<String> nombresEAdapter =
                        new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titulos.split("¦"));
                atv_titulo_evento.setAdapter(nombresEAdapter);

                // INICIAMOS EL AUTOCOMPLETAR DE TIPOS DE EVENTO Y COLOCAMOS EL TIPO DE EVENTO CORRESPONDIENTE
                ArrayAdapter<String> tiposEventoAdapter =
                        new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tipos_de_evento.split("¦"));
                atv_tipo_evento.setAdapter(tiposEventoAdapter);

                //CONFIGURAR AUTOCOMPLETAR PARA EL NOMBRE DEL ORGANIZADOR
                ArrayAdapter<String> nombresOrganizadorAdapter =
                        new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nombresOrganizador.split("¦"));
                atv_nombre_org.setAdapter(nombresOrganizadorAdapter);

                listaEventos = Data.getListaEventos(RegistrarEvento.this);
                if (evento != null) {
                    int x = 0;
                    for (Eventos e : listaEventos) {
                        if (e.aTag().equals(evento.aTag())) {
                            listaEventos.remove(x);
                            break;
                        }
                        x++;
                    }
                }
                loopComprobarhoras();
                unavez++;
            }
        });
    }

    private void loopComprobarhoras() {

        Log.v("ERRORES", "NUMERO ERRORES: " + listaConflictos.size());
        int x = 0;
        // SI HAY MAS DE UNA FECHA DE REGISTRO
        if (listaFechas.size() > 1) {

            // HAY QUE COMPROBAR QUE LAS DISTINTAS FECHAS NO TENGAN CONFLICTO ENTRE ELLAS JBH
            comprobarConFechas();

            // POR CADA FECHA ES UN EVENTO DIFERENTE, COMPROBAR CADA UNO DE ESOS EVENTOS
            for (Fecha f : listaFechas) {
                comprobarConBaseDatos(f, x);
                x++;
            }
        }
        // SI SOLO HAY UNA FECHA DE REGISTRO
        else if (listaFechas.size() == 1) {
            // QUITAR TODOS LOS PROBLEMAS DE FECHAS PORQUE NO ABRA YA QUE SOLO HAY UNA FECHA
            quitarConflictosF(true, 0, 0);

            // COMPROBAR EL UNICO EVENTO CON LA BASE DE DATOS
            comprobarConBaseDatos(listaFechas.get(0), 0);
        }

        // SI HAY MAS DE UN CONFLICTO EN LA LISTA
        if (listaConflictos.size() > 0) {
            // MOSTRAMOS LA LISTA EN LA PANTALLA
            rv_conflictos.setVisibility(View.VISIBLE);
            conteConflictos.setVisibility(View.VISIBLE);

            int y = 0;
            try {
                for (Fecha f : listaFechas) {
                    for (Conflictos c : listaConflictos) {
                        if (c.getNum_fecha() == y) {
                            f.getLabel_inicial().setTextColor(Color.RED);
                            f.getLabel_final().setTextColor(Color.RED);
                            break;
                        } else {
                            f.getLabel_inicial().setTextColor(Color.parseColor("#121212"));
                            f.getLabel_final().setTextColor(Color.parseColor("#121212"));
                        }
                    }
                    y++;
                }
            } catch (Exception ignored) {
            }
        }
        // SI NO HAY CONFLICTOS
        else {

            try {
                // DESAPARECEMOS LA LISTA DE LA PANTALLA
                rv_conflictos.setVisibility(View.INVISIBLE);
                conteConflictos.setVisibility(View.INVISIBLE);

                // COLOREAMOS DE NEGRO TODAS LAS HORAS YA QUE NO HAY ERRORES
                for (Fecha f : listaFechas) {
                    f.getLabel_inicial().setTextColor(Color.parseColor("#121212"));
                    f.getLabel_final().setTextColor(Color.parseColor("#121212"));
                }
            } catch (Exception ignored) {
            }

        }

        handler.postDelayed(mRunnable, 1200);
    }

    private void comprobarConFechas() {
        for (int x = 1; x < listaFechas.size(); x++) {

            // LA FECHA 1 QUE VAMOS A COMPARAR.
            Fecha f1 = listaFechas.get(x);

            // SEPARAMOS EN EVENTOS INDIVIDUALES
            for (int y = 0; y < x; y++) {

                // LA FECHA 2 CON LA QUE VAMOS A COMPRAR LA PRIMERA.
                Fecha f2 = listaFechas.get(y);

                // SI LAS DOS FECHAS SON DEL MISMO DIA...
                if (f1.getDia() == f2.getDia()) {

                    // EL EVENTO NO COMIENZA POR LO MENOS CON UNA HORA DE DIFERENCIA CON EL PROXIMO
                    // LA HORA INICIAL DEL EVENTO ESTA JUSTO EN MEDIO DEL HORARIO DE OTRO
                    if (f1.getHoraInicial() > (f2.getHoraInicial() - 2) && f1.getHoraInicial() < f2.getHoraFinal()) {
                        agregarConflictoF(new Conflictos(x, y, "F"));
                        break;
                    }

                    // LA HORA INICIAL DEL EVENTO ES POR LO MENOS UNA HORA ANTES QUE EL PROX EVENTO
                    else if (f1.getHoraInicial() < (f2.getHoraInicial() - 1)) {

                        // EL EVENTO FINALIZA DENTRO DEL HORARIO DEL PROXIMO O EL HORARIO ES TAN EXTENSO QUE CABE UN EVENTO DENTRO
                        if (f1.getHoraFinal() > f2.getHoraInicial()) {
                            agregarConflictoF(new Conflictos(x, y, "F"));
                            break;
                        } else {
                            quitarConflictosF(false, x, y);
                        }
                    } else {
                        quitarConflictosF(false, x, y);
                    }
                } else {
                    quitarConflictosF(false, x, y);
                }
            }
        }
    }

    private void comprobarConBaseDatos(Fecha f, int n) {

        // HORA Y FECHA DE LA FECHA
        Calendar c = Calendar.getInstance();
        c.set(2016, 0, 1);
        c.set(Calendar.DAY_OF_YEAR, f.getDia());

        // HORA Y FECHA ACTUAL
        Calendar c2 = Calendar.getInstance();

        // COMPROBAR PRIMERO SI SE ESTA INTENTANDO REGISTRAR UN EVENTO A LAS 9:00 PM O MAS TARDE
        if (f.getHoraInicial() > 27) {
            agregarConflictoV(new Conflictos(n, 0, "V"));
        }

        // VERFICIAR SI ES EL DIA DE HOY
        else if (c.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c.get(Calendar.MONTH) == c2.get(Calendar.MONTH) && c.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH)) {
            Log.v("ERROR", "ES LA MISMA FECHA");
            quitarConflictosV(n);
            if (f.getHoraInicial() <= horaASpinner(c2.get(Calendar.HOUR_OF_DAY), c2.get(Calendar.MINUTE))) {
                agregarConflictoV1(new Conflictos(n, 0, "V1"));
            } else {
                quitarConflictosV1(n);
            }
        }
        // SI EL EVENTO SE ESTA INTENTANDO REGISTRAR EN UNA HORA VALIDA COMPROBAMOS EL CUPO CON LA BASE DE DATOS
        else {
            quitarConflictosV(n);
            // SEPARAMOS EN EVENTOS INDIVIDUALES
            try {
                for (Eventos e : listaEventos) {
                    if (Integer.parseInt(e.getFecha()) == f.getDia() && e.getAuditorio().equals(AD)) {

                        // EL EVENTO NO COMIENZA POR LO MENOS CON UNA HORA DE DIFERENCIA CON EL PROXIMO
                        // LA HORA INICIAL DEL EVENTO ESTA JUSTO EN MEDIO DEL HORARIO DE OTRO
                        if (f.getHoraInicial() > (Integer.valueOf(e.getHoraInicial()) - 2) && f.getHoraInicial() < Integer.valueOf(e.getHoraFinal())) {
                            agregarConflictoE(new Conflictos(n, e, AD));
                            break;
                        }

                        // LA HORA INICIAL DEL EVENTO ES POR LO MENOS UNA HORA ANTES QUE EL PROX EVENTO
                        else if (f.getHoraInicial() < (Integer.valueOf(e.getHoraInicial()) - 1)) {

                            // EL EVENTO FINALIZA DENTRO DEL HORARIO DEL PROXIMO O EL HORARIO ES TAN EXTENSO QUE CABE UN EVENTO DENTRO
                            if (f.getHoraFinal() > Integer.valueOf(e.getHoraInicial())) {
                                agregarConflictoE(new Conflictos(n, e));
                                break;
                            } else {
                                quitarConflictosE(n, e);
                            }
                        } else {
                            quitarConflictosE(n, e);
                        }
                    } else {
                        quitarConflictosE(n, e);
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    private int horaASpinner(int i, int i1) {
        i = i - 7;
        i = i * 2;
        if (i1 >= 30) {
            i++;
        }
        return i + 2;
    }

    private void agregarConflictoV(Conflictos conflicto) {
        try {
            boolean agregar = true;
            for (Conflictos c : listaConflictos) {
                if (conflicto.getNum_fecha() == c.getNum_fecha() && conflicto.getTipo().equals("V")) {
                    agregar = false;
                    break;
                }
            }

            if (agregar) {
                listaFechas.get(conflicto.getNum_fecha()).getLabel_inicial().setTextColor(Color.RED);
                listaFechas.get(conflicto.getNum_fecha()).getLabel_final().setTextColor(Color.RED);
                listaConflictos.add(conflicto);
                rv_conflictos.getAdapter().notifyDataSetChanged();
            }
        } catch (Exception ignored) {
        }
    }

    private void quitarConflictosV(int xx) {
        try {
            int x = 0;
            for (Conflictos c : listaConflictos) {
                if (c.getNum_fecha() == xx && c.getTipo().equals("V")) {
                    listaConflictos.remove(x);
                    rv_conflictos.getAdapter().notifyDataSetChanged();
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void agregarConflictoV1(Conflictos conflicto) {
        try {
            boolean agregar = true;
            for (Conflictos c : listaConflictos) {
                if (conflicto.getNum_fecha() == c.getNum_fecha() && conflicto.getTipo().equals("V1")) {
                    agregar = false;
                    break;
                }
            }

            if (agregar) {
                listaFechas.get(conflicto.getNum_fecha()).getLabel_inicial().setTextColor(Color.RED);
                listaFechas.get(conflicto.getNum_fecha()).getLabel_final().setTextColor(Color.RED);
                listaConflictos.add(conflicto);
                rv_conflictos.getAdapter().notifyDataSetChanged();
            }
        } catch (Exception ignored) {
        }
    }

    private void quitarConflictosV1(int xx) {
        try {
            int x = 0;
            for (Conflictos c : listaConflictos) {
                if (c.getNum_fecha() == xx && c.getTipo().equals("V1")) {
                    listaConflictos.remove(x);
                    rv_conflictos.getAdapter().notifyDataSetChanged();
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void quitarConflictosE(int xx, Eventos ee) {
        try {
            int x = 0;
            for (Conflictos c : listaConflictos) {
                if (c.getNum_fecha() == xx && c.getQueEvento() == ee) {
                    listaConflictos.remove(x);
                    rv_conflictos.getAdapter().notifyDataSetChanged();
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void agregarConflictoE(Conflictos conflicto) {

        Log.v("CONFLICTOS", "AGREGAR CONFLICO EDE EVENTO");
        try {
            boolean agregar = true;
            for (Conflictos c : listaConflictos) {
                Log.v("CONFLICTOS", "SEPARAR LISTA DE CONFLICTOS EN IND");
                if (conflicto.getNum_fecha() == c.getNum_fecha() && conflicto.getQueEvento() == c.getQueEvento()) {
                    Log.v("CONFLICTOS", "UNO IGUAL");
                    agregar = false;
                    break;
                }
            }

            Log.v("CONFLICTOS", "AGREGAR: " + agregar);
            if (agregar) {

                Log.v("CONFLICTOS", "AGREGAREGANDO");
                Log.v("CONFLICTOS", "NO. DE FECHA: " + conflicto.getNum_fecha());
                listaFechas.get(conflicto.getNum_fecha()).getLabel_inicial().setTextColor(Color.RED);
                listaFechas.get(conflicto.getNum_fecha()).getLabel_final().setTextColor(Color.RED);

                Log.v("CONFLICTOS", "COLOREANDO FECHAS");
                listaConflictos.add(conflicto);
                Log.v("CONFLICTOS", "AGREGANDO CONFLICTOS LA LISTA");
                rv_conflictos.getAdapter().notifyDataSetChanged();
                Log.v("CONFLICTOS", "NOTIFICANDO CAMBIOS");
            }
        } catch (Exception ignored) {
        }
    }

    private void agregarConflictoF(Conflictos conflicto) {
        try {
            boolean agregar = true;
            for (Conflictos c : listaConflictos) {
                if (conflicto.getNum_fecha() == c.getNum_fecha() && conflicto.getNum_fecha_2() == c.getNum_fecha_2()) {
                    agregar = false;
                    break;
                }
            }

            if (agregar) {
                listaFechas.get(conflicto.getNum_fecha()).getLabel_inicial().setTextColor(Color.RED);
                listaFechas.get(conflicto.getNum_fecha()).getLabel_final().setTextColor(Color.RED);
                listaConflictos.add(conflicto);
                rv_conflictos.getAdapter().notifyDataSetChanged();
            }
        } catch (Exception ignored) {
        }
    }

    private void quitarConflictosF(boolean b, int xx, int yy) {
        try {
            if (b) { //  QUITA TODOS LOS CONFLICTOS DE FECHA
                int x = 0;
                for (Conflictos c : listaConflictos) {
                    if (c.getTipo().equals("F")) {
                        listaConflictos.remove(x);
                        rv_conflictos.getAdapter().notifyDataSetChanged();
                    }
                    x++;
                }
            } else {
                int x = 0;
                for (Conflictos c : listaConflictos) {
                    if (c.getTipo().equals("F") && c.getNum_fecha() == xx && c.getNum_fecha_2() == yy) {
                        listaConflictos.remove(x);
                        rv_conflictos.getAdapter().notifyDataSetChanged();
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void colorReveal(final int fondo) {

        //COLOREAMOS DEL COLOR DEL AUDITORIO EL FONDO INVISIBLE
        color_reveal.setBackgroundColor(fondo);

        //OBTENEMOS EL CENTRO DEL FONDO INVISIBLE, SERA EL ORIGEN DEL EFECTO REVELAR
        int cx = (color_reveal.getLeft() + color_reveal.getRight()) / 2;
        int cy = (color_reveal.getTop());

        //OBTENEMOS EL RADIO FINAL PARA EL CIRCULO DEL EFECTO REVELAR
        int finalRadius = Math.max(color_reveal.getWidth(), color_reveal.getHeight());

        //Creamos un animator para la view, el radio del efecto comienza en cero;
        Animator anim = ViewAnimationUtils.createCircularReveal(color_reveal, cx, cy, 0, finalRadius);
        anim.setDuration(500L);

        //AGREGAMOS UN LISTENER PARA CUANDO TERMINE LA ANIMACION ESCONDER DE NUEVO EL VIEW
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //CUANDO LA ANIMACION PONEMOS DEL MISMO COLOR EL FONDO INICIAL
                full_header.setBackgroundColor(fondo);

                //DESPUES OCULTAMOS NUEVAMENTE EL VIEW PARA EL EFECTO REVELAR
                color_reveal.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        //HACEMOS VISIBLE EL FONDO INVISIBLE Y COMENZAMOS LA ANIMACION
        color_reveal.setVisibility(View.VISIBLE);
        anim.start();

    }

    public void cerrar(View view) {
        handler.removeCallbacks(mRunnable);
        finishAfterTransition();
    }

    public void abrirDialogAgregarFechas() {
        if (listaConflictos.size() > 0) {
            Snackbar.make(snackposs, "Antes de agregar más fechas para el evento soluciona los problemas de cupo.", Snackbar.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, DialogAgregarFechas.class);
            intent.putExtra("DIA", int_fecha);
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
            startActivityForResult(intent, AGREGAR, bundle);
        }
    }

    public void registrarEvento() {
        if (listaConflictos.size() > 0) {
            Snackbar.make(snackposs, "Antes de registrar el evento soluciona los problemas de cupo.", Snackbar.LENGTH_LONG).show();
        } else if (atv_titulo_evento.getText().toString().trim().equals("")) {
            Snackbar.make(snackposs, "Ingresa el titulo del evento.", Snackbar.LENGTH_LONG).show();
        } else if (atv_tipo_evento.getText().toString().trim().equals("")) {
            Snackbar.make(snackposs, "Ingresa el tipo evento.", Snackbar.LENGTH_LONG).show();
        } else if (listaFechas.size() < 1) {
            Snackbar.make(snackposs, "Elige una fecha para el evento.", Snackbar.LENGTH_LONG).show();
        } else if (atv_nombre_org.getText().toString().trim().equals("")) {
            Snackbar.make(snackposs, "Ingresa el nombre del organizador del evento.", Snackbar.LENGTH_LONG).show();
        } else if (!pin_correcto_eliminar) {
            Snackbar.make(snackposs, "Ingresa una contraseña valida para registrar el evento.", Snackbar.LENGTH_LONG).show();
        } else {
            // Todos los datos son correctos... registrar el evento
            Bundle bundle = new Bundle();
            bundle.putString("TITULO", atv_titulo_evento.getText().toString().trim());
            bundle.putString("AUDITORIO", "" + (sp_auditorios.getSelectedItemPosition() + 1));
            bundle.putString("TIPO DE EVENTO", atv_tipo_evento.getText().toString().trim());
            bundle.putString("NOMBRE DEL ORGANIZADOR", atv_nombre_org.getText().toString().trim());
            bundle.putString("NUMERO TELEFONICO", TextUtils.isEmpty(et_num_tel.getText().toString()) ? "Sin número" : et_num_tel.getText().toString());
            bundle.putString("ESTATUS DEL EVENTO", evento == null ? "R" : "E");
            bundle.putString("QUIEN REGISTRO", st_quien);
            bundle.putString("NOTA", TextUtils.isEmpty(et_nota.getText().toString()) ? "Sin notas" : et_nota.getText().toString());
            bundle.putString("FONDO", "" + (sp_auditorios.getSelectedItemPosition() + 1));
            bundle.putParcelableArrayList("LISTA FECHAS", listaFechas);
            int folio = prefs.getInt(getString(R.string.prefs_id_prox), 0);
            bundle.putInt("FOLIO", evento == null ? folio : Integer.parseInt(evento.getId()));
            bundle.putInt("FECHA DEL DIA", int_fecha);
            getSupportLoaderManager().initLoader(0, bundle, this);
            Snackbar.make(snackposs, "Registrando evento, por favor espere...", Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case INICIAL:
                if (resultCode == RESULT_OK) {
                    int_fecha = data.getIntExtra("DIA_DEL_AÑO", 0);
                    String st_fecha = "" + data.getIntExtra("DIA_DEL_AÑO", 0);
                }
                break;
            case AGREGAR:
                if (resultCode == RESULT_OK) {

                    // TRAEMOS LA LISTA DE LAS FECHAS SELECCIONADAS
                    ArrayList<Integer> listaFechas2 = data.getIntegerArrayListExtra("LISTA_FECHAS");

                    for (Integer f : listaFechas2) {
                        try {
                            listaFechas.add(new Fecha(f, listaFechas.get(0).getHoraInicial(), listaFechas.get(0).getHoraFinal()));
                        } catch (Exception ignored0) {
                            listaFechas.add(new Fecha(f, 0, 0));
                        }
                    }

                    // LA ORDENAMOS EN ORDEN ASCENDENTE PARA NO TENER DIAS SALTEADOS
                    Collections.sort(listaFechas, (f1, f2) -> {
                        Integer i1 = f1.getDia();
                        Integer i2 = f2.getDia();

                        if (Objects.equals(i1, i2)) {

                            Integer i3 = f1.getHoraInicial();
                            Integer i4 = f2.getHoraInicial();
                            if (Objects.equals(i3, i4)) {
                                Integer i5 = f1.getHoraFinal();
                                Integer i6 = f2.getHoraFinal();
                                return i5.compareTo(i6);
                            } else {
                                return i3.compareTo(i4);
                            }
                        } else {
                            return i1.compareTo(i2);
                        }

                    });

                    rv_fechas.getAdapter().notifyDataSetChanged();
                }
                break;
            case 44:
                if (resultCode == RESULT_OK) {
                    listaConflictos.clear();
                }
                break;

        }
    }

    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case 0:
                return new com.example.check.agendacucshlanormal.util.GuardarEvento(this, args, evento);
            case 1:
                return new RegistrarUpdate(this);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {
        switch (loader.getId()) {
            case 0:
                if (data == null) {
                    Snackbar.make(snackposs, "Hay un problema con la conexión a la base de datos. Verifica tu conexión a internet e intentalo nuevamente", Snackbar.LENGTH_LONG).show();
                } else {
                    listaDeEventosNuevos = (ArrayList<Eventos>) data;
                    getSupportLoaderManager().initLoader(1, null, this);
                }
                break;
            case 1:
                if (data != null) {
                    rv_conflictos.setVisibility(View.GONE);
                    i = getIntent();
                    i.putParcelableArrayListExtra("LISTA", listaDeEventosNuevos);
                    setResult(RESULT_OK, i);
                    cerrar(null);
                } else {
                    Snackbar.make(snackposs, "Hay un problema con la conexión a la base de datos. Verifica tu conexión a internet e intentalo nuevamente.", Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }
}

