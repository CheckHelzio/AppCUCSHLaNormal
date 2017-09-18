package com.example.check.agendacucshlanormal;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.check.agendacucshlanormal.util.Data;
import com.example.check.agendacucshlanormal.util.DescargarData;
import com.example.check.agendacucshlanormal.util.LlenarListaEventos;
import com.example.check.agendacucshlanormal.util.OnSwipeTouchListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class Inicio extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Object> {

    static final int HELZIO_DATE_DIALOG = 13;
    private static final String TAG = Inicio.class.getSimpleName();
    public static boolean esperar = false;
    protected static Calendar calendarioActualizarDiasMes;
    protected static int irHoyMes;
    protected static int irHoyDiaSemana;
    protected static int irHoyNumeroDiaMes;
    protected static int irHoyAño;
    protected static boolean filtro1 = true, filtro2 = true, filtro3 = true, filtro4 = true, filtro5 = true;
    private final String auditorio1 = "Auditorio Salvador Allende";
    private final String auditorio2 = "Auditorio Silvano Barba";
    private final String auditorio3 = "Auditorio Carlos Ramírez";
    private final String auditorio4 = "Auditorio Adalberto Navarro";
    private final String auditorio5 = "Sala de Juicios Orales Mariano Otero";
    private Handler handler;
    private TextView tv_header2;
    private TextView tv_conexion;
    private SharedPreferences prefs;
    private FloatingActionButton fab;
    private GridLayout grid_layout;
    private boolean bclick = false;
    private Integer diasemana;
    private int dia_inicial_del_mes;
    private RelativeLayout conte_mes;
    private String[] eventos = new String[3660];
    private TextView tv_lun, tv_mar, tv_mie, tv_jue, tv_vie, tv_sab, tv_dom;
    private TextView[] lista_numeros_del_mes = new TextView[42];
    private TextView[] lista_nombre_dias_semana = new TextView[7];
    private TextView[] lista_info1 = new TextView[42];
    private TextView[] lista_info2 = new TextView[42];
    private TextView[] lista_info3 = new TextView[42];
    private LinearLayout[] lista_cajas_mes = new LinearLayout[42];
    private TextView tv_hoy;
    private View proteccion;
    private String st_data;
    private String st_update;
    private Bundle bundle;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (isScreenLarge()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        iniciarObjetos();
        iniciarDrawer();
        setListenners();
        actualizarFecha();
        actualizarMarcaAgua();
        iniciarPagina();
        new eventoEnCalendario().execute();
    }

    private void iniciarObjetos() {
        handler = new Handler();

        proteccion = findViewById(R.id.proteccion);
        fab = findViewById(R.id.fab);

        tv_lun = findViewById(R.id.lun);
        tv_mar = findViewById(R.id.mar);
        tv_mie = findViewById(R.id.mie);
        tv_jue = findViewById(R.id.jue);
        tv_vie = findViewById(R.id.vie);
        tv_sab = findViewById(R.id.sab);
        tv_dom = findViewById(R.id.dom);

        Calendar calendarioIrHoy = Calendar.getInstance();
        irHoyNumeroDiaMes = calendarioIrHoy.get(Calendar.DAY_OF_MONTH);
        irHoyDiaSemana = calendarioIrHoy.get(Calendar.DAY_OF_WEEK);
        irHoyAño = calendarioIrHoy.get(Calendar.YEAR);
        irHoyMes = calendarioIrHoy.get(Calendar.MONTH);

        calendarioActualizarDiasMes = Calendar.getInstance();
        calendarioActualizarDiasMes.set(Calendar.DAY_OF_MONTH, 1);

        prefs = getSharedPreferences(getString(R.string.prefs), Context.MODE_PRIVATE);
        st_update = prefs.getString("UPDATE", "");

        tv_header2 = findViewById(R.id.header2);
        tv_conexion = findViewById(R.id.conexion);

        Calendar calendarioMinimo = Calendar.getInstance();
        calendarioMinimo.set(2016, 0, 1);

        grid_layout = findViewById(R.id.grid);
        conte_mes = findViewById(R.id.conte_mes);

        llenarListaNombreDiasSemana();
        llenarListas();

        Gson gson = new Gson();
        eventos = gson.fromJson(prefs.getString(getResources().getString(R.string.prefs_array_eventos), ""), String[].class);
    }

    private void llenarListaNombreDiasSemana() {
        lista_nombre_dias_semana[0] = tv_dom;
        lista_nombre_dias_semana[1] = tv_lun;
        lista_nombre_dias_semana[2] = tv_mar;
        lista_nombre_dias_semana[3] = tv_mie;
        lista_nombre_dias_semana[4] = tv_jue;
        lista_nombre_dias_semana[5] = tv_vie;
        lista_nombre_dias_semana[6] = tv_sab;
    }

    private void llenarListas() {
        for (int x = 0; x <= 41; x++) {
            lista_info1[x] = (TextView) ((ViewGroup) ((ViewGroup) grid_layout.getChildAt(x)).getChildAt(1)).getChildAt(0);
            lista_info2[x] = (TextView) ((ViewGroup) ((ViewGroup) grid_layout.getChildAt(x)).getChildAt(1)).getChildAt(1);
            lista_info3[x] = (TextView) ((ViewGroup) ((ViewGroup) grid_layout.getChildAt(x)).getChildAt(1)).getChildAt(2);
            lista_cajas_mes[x] = (LinearLayout) grid_layout.getChildAt(x);
            lista_numeros_del_mes[x] = (TextView) ((ViewGroup) ((ViewGroup) grid_layout.getChildAt(x)).getChildAt(0)).getChildAt(0);
        }
    }

    private void iniciarDrawer() {
        // Initializing Toolbar and setting it as the actionbar
        Toolbar toolbar = findViewById(R.id.header);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                esperar = false;
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                esperar = true;
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    private void actualizarMarcaAgua() {
        tv_conexion.setText(prefs.getString("ACTUALIZACION", ""));
    }

    private void actualizarFecha() {
        SimpleDateFormat format = new SimpleDateFormat("MMMM 'del' yyyy", Locale.forLanguageTag("es-MX"));
        String f = format.format(calendarioActualizarDiasMes.getTime());
        tv_header2.setText(capitalize(f));
    }

    private Integer obtenerDiaSemana(int i) {
        switch (i) {
            //domingo
            case 1:
                i = 6;
                break;
            //lunes
            case 2:
                i = 0;
                break;
            //martes
            case 3:
                i = 1;
                break;
            //miercoles
            case 4:
                i = 2;
                break;
            //jueves
            case 5:
                i = 3;
                break;
            //viernes
            case 6:
                i = 4;
                break;
            //sabado
            case 7:
                i = 5;
                break;
        }
        return i;
    }

    private void setListenners() {
        for (int x = 0; x < grid_layout.getChildCount() - 1; x++) {
            final int finalX = x;
            grid_layout.getChildAt(x).setOnTouchListener(new OnSwipeTouchListener(Inicio.this) {

                @Override
                public void onClick() {
                    grid_layout.getChildAt(finalX).performClick();
                }

                @Override
                public void onSelected(boolean b) {
                    grid_layout.getChildAt(finalX).setPressed(b);
                }

                @Override
                public void onLongClick() {
                    grid_layout.getChildAt(finalX).performLongClick();
                }

                @Override
                public void onSwipeLeft() {
                    bclick = true;
                    calendarioActualizarDiasMes.add(Calendar.MONTH, 1);
                    cambiarPagina();
                }

                @Override
                public void onSwipeRight() {
                    bclick = true;
                    int x = calendarioActualizarDiasMes.get(Calendar.YEAR);
                    int y = calendarioActualizarDiasMes.get(Calendar.MONTH);
                    if (!(x == 2016 && y == 0)) {
                        calendarioActualizarDiasMes.add(Calendar.MONTH, -1);
                        cambiarPagina();
                    }
                }
            });
        }

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(Inicio.this, RegistrarEvento.class);
            intent.putExtra("DONDE", "PRINCIPAL");
            FabTransition.addExtras(intent, (Integer) getAcentColor(), R.drawable.ic_mas);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Inicio.this, fab,
                    getString(R.string.transition_date_dialog_helzio));
            startActivityForResult(intent, HELZIO_DATE_DIALOG, options.toBundle());
        });
    }

    private void cambiarPagina() {
        final Animation fadeInPagina = AnimationUtils.loadAnimation(Inicio.this, R.anim.fadein_z);
        final Animation fadeOutPagina = AnimationUtils.loadAnimation(Inicio.this, R.anim.fadeout_z);
        fadeInPagina.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                conte_mes.setVisibility(View.VISIBLE);
                bclick = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fadeOutPagina.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                actualizarFecha();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iniciarPagina();
                new eventoEnCalendario().execute();
                conte_mes.startAnimation(fadeInPagina);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        conte_mes.startAnimation(fadeOutPagina);
    }

    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case 0:
                return new DescargarData(this, args.getString("URL"));
            case 1:
                return new DescargarData(this, args.getString("URL"));
            case 2:
                return new LlenarListaEventos(this, st_data);
            default:
                return new DescargarData(this, args.getString("URL"));
        }
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {
        switch (loader.getId()) {
            case 0:
                if (!TextUtils.isEmpty(data.toString())) {
                    if (data.toString().contains("Redirection Error")) {
                        tv_conexion.setText(R.string.problema_conexion);
                        handler.postDelayed(() -> {
                            bundle = new Bundle();
                            bundle.putString("URL", getString(R.string.url_update));
                            getSupportLoaderManager().restartLoader(0, bundle, Inicio.this);
                        }, 1000);
                        break;
                    }

                    st_update = prefs.getString("UPDATE", "");
                    if (!data.toString().equals(st_update)) {

                        tv_conexion.setText(R.string.actualizando);
                        // La informacion esta desactualizada || descargemos la data actual
                        prefs.edit().putString("UPDATE", data.toString()).apply();
                        bundle.putString("URL", getString(R.string.url_datos));
                        getSupportLoaderManager().restartLoader(1, bundle, this);
                    } else {
                        // Guardamos la hora de actualizacion
                        SimpleDateFormat format = new SimpleDateFormat("'Actualizado el 'd 'de' MMMM 'del' yyyy 'a las' h:mm a", Locale.forLanguageTag("es-MX"));
                        prefs.edit().putString("ACTUALIZACION", format.format(Calendar.getInstance().getTime())).apply();
                        // La informacion esta actualizada || Iniciamos la aplicacion
                        actualizarMarcaAgua();

                        handler.postDelayed(() -> {
                            bundle = new Bundle();
                            bundle.putString("URL", getString(R.string.url_update));
                            getSupportLoaderManager().restartLoader(0, bundle, Inicio.this);
                        }, 1000);
                    }
                } else {
                    Log.v(TAG, "PROBLEMA DE CONEXION");
                    tv_conexion.setText(R.string.problema_conexion);
                    handler.postDelayed(() -> {
                        bundle = new Bundle();
                        bundle.putString("URL", getString(R.string.url_update));
                        getSupportLoaderManager().restartLoader(0, bundle, Inicio.this);
                    }, 1000);
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
                    getSupportLoaderManager().restartLoader(2, null, this);
                }
                break;
            case 2:
                Log.v(TAG, "llenar data");
                eventos = (String[]) data;
                new eventoEnCalendario().execute();
                actualizarMarcaAgua();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }

    private void iniciarPagina() {
        diasemana = obtenerDiaSemana(calendarioActualizarDiasMes.get(Calendar.DAY_OF_WEEK));
        int actuDias_Año = calendarioActualizarDiasMes.get(Calendar.YEAR);

        if (actuDias_Año == 2016) {
            dia_inicial_del_mes = calendarioActualizarDiasMes.get(Calendar.DAY_OF_YEAR);
        } else {
            dia_inicial_del_mes = calendarioActualizarDiasMes.get(Calendar.DAY_OF_YEAR);
            for (int x = 2016; x < actuDias_Año; x++) {
                Calendar cr = Calendar.getInstance();
                cr.set(x, 11, 31);
                dia_inicial_del_mes += cr.get(Calendar.DAY_OF_YEAR);
            }
        }

        for (int x = 28; x <= 41; x++) {
            lista_cajas_mes[x].setVisibility(View.VISIBLE);
        }

        if (diasemana + calendarioActualizarDiasMes.getActualMaximum(Calendar.DAY_OF_MONTH) >= 36) {
            lista_cajas_mes[35].setVisibility(View.VISIBLE);
            lista_cajas_mes[36].setVisibility(View.VISIBLE);
            lista_cajas_mes[37].setVisibility(View.VISIBLE);
            lista_cajas_mes[38].setVisibility(View.VISIBLE);
            lista_cajas_mes[39].setVisibility(View.VISIBLE);
            lista_cajas_mes[40].setVisibility(View.VISIBLE);
            lista_cajas_mes[41].setVisibility(View.VISIBLE);
        } else {
            lista_cajas_mes[35].setVisibility(View.GONE);
            lista_cajas_mes[36].setVisibility(View.GONE);
            lista_cajas_mes[37].setVisibility(View.GONE);
            lista_cajas_mes[38].setVisibility(View.GONE);
            lista_cajas_mes[39].setVisibility(View.GONE);
            lista_cajas_mes[40].setVisibility(View.GONE);
            lista_cajas_mes[41].setVisibility(View.GONE);
        }

        for (int y = 0; y < diasemana; y++) {
            lista_cajas_mes[y].setVisibility(View.GONE);
        }

        for (int y = diasemana; y <= 6; y++) {
            lista_cajas_mes[y].setVisibility(View.VISIBLE);
        }

        for (int x = 1; x <= calendarioActualizarDiasMes.getActualMaximum(Calendar.DAY_OF_MONTH); x++) {
            final String st_nd = "" + x;
            lista_numeros_del_mes[x - 1 + diasemana].setText(st_nd);
        }

        if (calendarioActualizarDiasMes.get(Calendar.MONTH) == irHoyMes && irHoyAño == calendarioActualizarDiasMes.get(Calendar.YEAR)) {
            lista_numeros_del_mes[diasemana - 1 + irHoyNumeroDiaMes].setBackgroundResource(R.drawable.fondo_hoy);
            lista_numeros_del_mes[diasemana - 1 + irHoyNumeroDiaMes].setTextColor(Color.WHITE);
            lista_nombre_dias_semana[irHoyDiaSemana - 1].setTextColor((Integer) getAcentColor());
            tv_hoy = lista_numeros_del_mes[diasemana - 1 + irHoyNumeroDiaMes];
        } else {
            tv_hoy.setBackgroundColor(Color.TRANSPARENT);
            tv_hoy.setTextColor(Color.parseColor("#757575"));
            lista_nombre_dias_semana[irHoyDiaSemana - 1].setTextColor(Color.parseColor("#757575"));
        }

        for (int x = calendarioActualizarDiasMes.getActualMaximum(Calendar.DAY_OF_MONTH) + diasemana; x <= 35; x++) {
            lista_cajas_mes[x].setVisibility(View.GONE);
        }

    }

    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    private boolean isScreenLarge() {
        final int screenSize = getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK;
        return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE
                || screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    public Object getAcentColor() {
        int colorAttr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorAttr = android.R.attr.colorAccent;
        } else {
            //Get colorAccent defined for AppCompat
            colorAttr = getResources().getIdentifier("colorAccent", "attr", getPackageName());
        }
        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.data;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_fecha:
                Intent intent = new Intent(this, DateDialogHelzio.class);
                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
                startActivityForResult(intent, HELZIO_DATE_DIALOG, bundle);
                return true;
            case R.id.menu_buscar:
                final AutoCompleteTextView autoCompleteTextView = findViewById(R.id.search_view);
                final ImageView buscar = findViewById(R.id.menu_buscar2);
                final ImageView cancel = findViewById(R.id.menu_cerrar_buscar);
                final RelativeLayout conte_buscar = findViewById(R.id.conte_buscar);
                conte_buscar.setVisibility(View.VISIBLE);

                String conceptos = prefs.getString(getString(R.string.prefs_titulos), "") +
                        prefs.getString(getString(R.string.prefs_nombres_organizador), "") +
                        prefs.getString(getString(R.string.prefs_tipos_evento), "") + "¦" +
                        auditorio1 + "¦" + auditorio2 + "¦" + auditorio3 + "¦" + auditorio4 + "¦" + auditorio5 + "¦";
                ArrayAdapter<String> nombresAdapter = new ArrayAdapter<>(Inicio.this, android.R.layout.simple_list_item_1, conceptos.split("¦"));
                autoCompleteTextView.setAdapter(nombresAdapter);
                autoCompleteTextView.setThreshold(2);
                autoCompleteTextView.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        InputMethodManager inputManager = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (inputManager != null) {
                            inputManager.toggleSoftInput(0, 0);
                        }
                        conte_buscar.setVisibility(View.INVISIBLE);
                        buscar(autoCompleteTextView.getText().toString());
                        autoCompleteTextView.setText("");
                        return true;
                    }
                    return false;
                });

                buscar.setOnClickListener(v -> {
                    if (!autoCompleteTextView.getText().toString().equals("")) {
                        InputMethodManager inputManager = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (inputManager != null) {
                            inputManager.toggleSoftInput(0, 0);
                        }
                        conte_buscar.setVisibility(View.INVISIBLE);
                        buscar(autoCompleteTextView.getText().toString());
                        autoCompleteTextView.setText("");
                    }
                });

                autoCompleteTextView.setOnKeyListener((v, keyCode, event) -> {
                    // If the event is a key-down event on the "enter" button
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                            (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        InputMethodManager inputManager = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (inputManager != null) {
                            inputManager.toggleSoftInput(0, 0);
                        }
                        conte_buscar.setVisibility(View.INVISIBLE);
                        buscar(autoCompleteTextView.getText().toString());
                        autoCompleteTextView.setText("");
                        return true;
                    }
                    return false;
                });
                cancel.setOnClickListener(v -> {
                    autoCompleteTextView.setText("");
                    conte_buscar.setVisibility(View.INVISIBLE);
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private int getFondo(String trim) {
        int a = 0;
        switch (trim) {
            case "1":
                a = (R.drawable.fondo1);
                break;
            case "2":
                a = (R.drawable.fondo2);
                break;
            case "3":
                a = (R.drawable.fondo3);
                break;
            case "4":
                a = (R.drawable.fondo4);
                break;
            case "5":
                a = (R.drawable.fondo5);
                break;
        }
        return a;
    }

    private void buscar(String s) {
        final ArrayList<Eventos> lista_pequeña_eventos = new ArrayList<>();

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Eventos>>() {
        }.getType();
        SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs), Context.MODE_PRIVATE);
        ArrayList<Eventos> lista_eventos = gson.fromJson(prefs.getString(getResources().getString(R.string.prefs_lista_eventos), ""), type);

        switch (s) {
            case auditorio1:
                s = "1";
                for (Eventos ev : lista_eventos) {
                    if (ev.getAuditorio().equals(s))
                        lista_pequeña_eventos.add(ev);
                }
                break;
            case auditorio2:
                s = "2";
                for (Eventos ev : lista_eventos) {
                    if (ev.getAuditorio().equals(s))
                        lista_pequeña_eventos.add(ev);
                }
                break;
            case auditorio3:
                s = "3";
                for (Eventos ev : lista_eventos) {
                    if (ev.getAuditorio().equals(s))
                        lista_pequeña_eventos.add(ev);
                }
                break;
            case auditorio4:
                s = "4";
                for (Eventos ev : lista_eventos) {
                    if (ev.getAuditorio().equals(s))
                        lista_pequeña_eventos.add(ev);
                }
                break;
            case auditorio5:
                s = "5";
                for (Eventos ev : lista_eventos) {
                    if (ev.getAuditorio().equals(s))
                        lista_pequeña_eventos.add(ev);
                }
                break;
            default:
                for (Eventos ev : lista_eventos) {
                    if (ev.aTag().contains(s))
                        lista_pequeña_eventos.add(ev);
                }
                break;
        }

        // INTENT A LA LISTA DE EVENTOS
        Intent intent = new Intent(Inicio.this, DialogResultadoBusqueda.class);

        // PASAMOS LA LISTA DE EVENTOS
        intent.putExtra("LISTA_EVENTOS", lista_pequeña_eventos);

        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        startActivityForResult(intent, HELZIO_DATE_DIALOG, bundle);


    }

    private boolean filtro(String auditorio) {
        boolean f = false;
        switch (auditorio) {
            case "1":
                f = Inicio.filtro1;
                break;
            case "2":
                f = Inicio.filtro2;
                break;
            case "3":
                f = Inicio.filtro3;
                break;
            case "4":
                f = Inicio.filtro4;
                break;
            case "5":
                f = Inicio.filtro5;
                break;
        }
        return f;
    }

    public void clickbotones(View view) {
        Log.v("PRIMERO", "CLICK");
        if (!bclick) {
            bclick = true;
            int HELZIO_ELIMINAR_EVENTO = 4;
            try {
                int da = Integer.parseInt(view.getTag().toString());
                int dm = Integer.parseInt((((TextView) ((ViewGroup) ((ViewGroup) view).getChildAt(0)).getChildAt(0)).getText().toString()));
                final ArrayList<Eventos> lista_pequeña_eventos = new ArrayList<>();

                for (String v : eventos[da].split("¦")) {
                    if (filtro(v.split("::")[4].trim().replaceAll("[^0-9]+", ""))) {
                        lista_pequeña_eventos.add(agregarEvento(v));
                    }
                }

                // INTENT A LA LISTA DE EVENTOS
                Intent intent = new Intent(Inicio.this, DialogListaEventosHelzio.class);
                // PASAMOS EL NUMERO DE DIA
                intent.putExtra("DIA_MES", ((TextView) ((ViewGroup) ((ViewGroup) view).getChildAt(0)).getChildAt(0)).getText());

                // PASAMOS EL NUMERO DE DIA DESDE EL 2016
                intent.putExtra("DIA_AÑO", da);

                // PASAMOS EL NOMBRE DEL DIA
                intent.putExtra("NOMBRE_DIA", view.getResources().getResourceEntryName(view.getId()));

                // PASAMOS LA LISTA DE EVENTOS
                intent.putExtra("LISTA_EVENTOS", lista_pequeña_eventos);

                //BOLEAN PARA SABER SI ESTAMOS CLICKEANDO EL DIA DE HOY Y COLOREAR EL TEXTO EN EL DIALOG
                if (calendarioActualizarDiasMes.get(Calendar.MONTH) == irHoyMes && dm == irHoyNumeroDiaMes) {
                    intent.putExtra("ES_HOY", true);
                } else {
                    intent.putExtra("ES_HOY", false);
                }

                if (calendarioActualizarDiasMes.get(Calendar.YEAR) < irHoyAño) {
                    intent.putExtra("REGISTRAR", false);
                } else if (calendarioActualizarDiasMes.get(Calendar.YEAR) == irHoyAño) { //BOOLEAN PARA SABER SI SE PUEDE REGISTRAR O NO
                    // SI ESTAMOS EN UN MES ANTERIOR AL ACTUAL NO SE PUEDE REGISTRAR
                    if (calendarioActualizarDiasMes.get(Calendar.MONTH) < irHoyMes) {
                        intent.putExtra("REGISTRAR", false);
                    }
                    // SI ESTAMOS EN EL MISMO MES
                    else if (calendarioActualizarDiasMes.get(Calendar.MONTH) == irHoyMes) {
                        //SI EL DIA DEL MES ES ANTERIOR AL DIA DE HOY NO PODEMOS REGISTRAR
                        if (dm < irHoyNumeroDiaMes) {
                            intent.putExtra("REGISTRAR", false);
                        }
                        //SI EL DIA DEL MES ES HOY NO SE PUEDE AGENDAR DESPUES DE LAS 8:30PM
                        else if (dm == irHoyNumeroDiaMes) {
                            Calendar calendarioIrHoy = Calendar.getInstance();
                            if (calendarioIrHoy.get(Calendar.HOUR_OF_DAY) > 20) {
                                int hora = calendarioIrHoy.get(Calendar.HOUR_OF_DAY);
                                int minuto = calendarioIrHoy.get(Calendar.MINUTE);
                                if (hora == 21) {
                                    if (minuto < 30) {
                                        intent.putExtra("REGISTRAR", true);
                                    } else {
                                        intent.putExtra("REGISTRAR", false);
                                    }
                                } else {
                                    intent.putExtra("REGISTRAR", false);
                                }
                            } else {
                                intent.putExtra("REGISTRAR", true);
                            }
                        }
                        //SI ES DESPUES DE HOY SI SE PEUDE REGISTRAR
                        else {
                            intent.putExtra("REGISTRAR", true);
                        }
                    }
                    //SI ES EN UN MES FUTURO SE PUEDE REGISTRAR
                    else {
                        intent.putExtra("REGISTRAR", true);
                    }

                } else if (calendarioActualizarDiasMes.get(Calendar.YEAR) > irHoyAño) {
                    intent.putExtra("REGISTRAR", true);
                }

                final Rect startBounds = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                ChangeBoundBackground.addExtras(intent, getViewBitmap(view), startBounds);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Inicio.this, view, "fondo");
                startActivityForResult(intent, HELZIO_ELIMINAR_EVENTO, options.toBundle());
                view.setEnabled(true);
            } catch (Exception ignored) {

                int da = Integer.parseInt(view.getTag().toString());
                int dm = Integer.parseInt((((TextView) ((ViewGroup) ((ViewGroup) view).getChildAt(0)).getChildAt(0)).getText().toString()));
                final ArrayList<Eventos> lista_pequeña_eventos = new ArrayList<>();

                // INTENT A LA LISTA DE EVENTOS
                Intent intent = new Intent(Inicio.this, DialogListaEventosHelzio.class);
                // PASAMOS EL NUMERO DE DIA
                intent.putExtra("DIA_MES", ((TextView) ((ViewGroup) ((ViewGroup) view).getChildAt(0)).getChildAt(0)).getText());

                // PASAMOS EL NUMERO DE DIA DESDE EL 2016
                intent.putExtra("DIA_AÑO", da);

                // PASAMOS EL NOMBRE DEL DIA
                intent.putExtra("NOMBRE_DIA", view.getResources().getResourceEntryName(view.getId()));

                // PASAMOS LA LISTA DE EVENTOS
                intent.putExtra("LISTA_EVENTOS", lista_pequeña_eventos);

                //BOLEAN PARA SABER SI ESTAMOS CLICKEANDO EL DIA DE HOY Y COLOREAR EL TEXTO EN EL DIALOG
                if (calendarioActualizarDiasMes.get(Calendar.MONTH) == irHoyMes && dm == irHoyNumeroDiaMes) {
                    intent.putExtra("ES_HOY", true);
                } else {
                    intent.putExtra("ES_HOY", false);
                }

                if (calendarioActualizarDiasMes.get(Calendar.YEAR) < irHoyAño) {
                    intent.putExtra("REGISTRAR", false);
                } else if (calendarioActualizarDiasMes.get(Calendar.YEAR) == irHoyAño) {//BOOLEAN PARA SABER SI SE PUEDE REGISTRAR O NO
                    //SI ESTAMOS EN UN MES ANTERIOR AL ACTUAL NO SE PUEDE REGISTRAR
                    if (calendarioActualizarDiasMes.get(Calendar.MONTH) < irHoyMes) {
                        intent.putExtra("REGISTRAR", false);
                    }
                    //SI ESTAMOS EN EL MISMO MES
                    else if (calendarioActualizarDiasMes.get(Calendar.MONTH) == irHoyMes) {
                        //SI EL DIA DEL MES ES ANTERIOR AL DIA DE HOY NO PODEMOS REGISTRAR
                        if (dm < irHoyNumeroDiaMes) {
                            intent.putExtra("REGISTRAR", false);
                        }
                        //SI EL DIA DEL MES ES HOY NO SE PUEDE AGENDAR DESPUES DE LAS 8PM
                        else if (dm == irHoyNumeroDiaMes) {
                            Calendar calendarioIrHoy = Calendar.getInstance();
                            if (calendarioIrHoy.get(Calendar.HOUR_OF_DAY) > 20) {
                                int hora = calendarioIrHoy.get(Calendar.HOUR_OF_DAY);
                                int minuto = calendarioIrHoy.get(Calendar.MINUTE);
                                if (hora == 21) {
                                    if (minuto < 30) {
                                        intent.putExtra("REGISTRAR", true);
                                    } else {
                                        intent.putExtra("REGISTRAR", false);
                                    }
                                } else {
                                    intent.putExtra("REGISTRAR", false);
                                }
                            } else {
                                intent.putExtra("REGISTRAR", true);
                            }
                        }
                        //SI ES DESPUES DE HOY SI SE PEUDE REGISTRAR
                        else {
                            intent.putExtra("REGISTRAR", true);
                        }
                    }
                    //SI ES EN UN MES FUTURO SE PUEDE REGISTRAR
                    else {
                        intent.putExtra("REGISTRAR", true);
                    }

                } else if (calendarioActualizarDiasMes.get(Calendar.YEAR) > irHoyAño) {
                    intent.putExtra("REGISTRAR", true);
                }

                final Rect startBounds = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                ChangeBoundBackground.addExtras(intent, getViewBitmap(view), startBounds);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Inicio.this, view, "fondo");
                startActivityForResult(intent, HELZIO_ELIMINAR_EVENTO, options.toBundle());
                view.setEnabled(true);
            }
            handler.postDelayed(() -> bclick = false, 1000);
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

    private Eventos agregarEvento(String eventos_suelto) {
        return new Eventos(
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
                Data.getFondoAuditorio(eventos_suelto.split("::")[4].trim(), getApplicationContext())
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case HELZIO_DATE_DIALOG:
                if (resultCode == RESULT_OK && data != null) {
                    calendarioActualizarDiasMes.set(2016, 0, 1);
                    calendarioActualizarDiasMes.set(Calendar.MONTH, data.getExtras().getInt("NUMERO_DE_MES"));
                    cambiarPagina();
                }
                break;
        }
    }

    public void checkAuditorios(View v) {
        ((CheckBox) ((ViewGroup) v).getChildAt(0)).setChecked(!((CheckBox) ((ViewGroup) v).getChildAt(0)).isChecked());

        switch (v.getId()) {
            case R.id.filtro1:
                filtro1 = ((CheckBox) ((ViewGroup) v).getChildAt(0)).isChecked();
                break;
            case R.id.filtro2:
                filtro2 = ((CheckBox) ((ViewGroup) v).getChildAt(0)).isChecked();
                break;
            case R.id.filtro3:
                filtro3 = ((CheckBox) ((ViewGroup) v).getChildAt(0)).isChecked();
                break;
            case R.id.filtro4:
                filtro4 = ((CheckBox) ((ViewGroup) v).getChildAt(0)).isChecked();
                break;
            case R.id.filtro5:
                filtro5 = ((CheckBox) ((ViewGroup) v).getChildAt(0)).isChecked();
                break;
        }

        new eventoEnCalendario().execute();
    }

    private class eventoEnCalendario extends AsyncTask<Void, Object, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            for (int x = 1; x <= calendarioActualizarDiasMes.getActualMaximum(Calendar.DAY_OF_MONTH); x++) {
                StringBuilder n = new StringBuilder();
                try {
                    for (String ev : eventos[dia_inicial_del_mes - 1 + x].split("¦")) {
                        if (!ev.equals("") && filtro(ev.split("::")[4].trim().replaceAll("[^0-9]+", ""))) {
                            n.append(ev).append("¦");
                        }
                    }
                    String ev[] = n.toString().split("¦");
                    publishProgress(ev, x);
                } catch (Exception ignored) {
                    String ev[] = n.toString().split("¦");
                    publishProgress(ev, x);
                }

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            String eventos[] = (String[]) values[0];
            int x = (int) values[1];
            lista_cajas_mes[x - 1 + diasemana].setTag((dia_inicial_del_mes - 1 + x));
            try {
                if (eventos.length > 1 || (eventos.length == 1 && !eventos[0].equals(""))) {
                    lista_info1[x - 1 + diasemana].setVisibility(View.VISIBLE);
                    lista_info1[x - 1 + diasemana].setText(eventos[0].split("::")[3].trim());
                    lista_info1[x - 1 + diasemana].setBackgroundResource(getFondo(eventos[0].split("::")[4].trim()));
                    if (eventos.length >= 2) {
                        lista_info2[x - 1 + diasemana].setVisibility(View.VISIBLE);
                        lista_info2[x - 1 + diasemana].setText(eventos[1].split("::")[3].trim());
                        lista_info2[x - 1 + diasemana].setBackgroundResource(getFondo(eventos[1].split("::")[4].trim()));
                        if (eventos.length >= 3) {
                            lista_info3[x - 1 + diasemana].setVisibility(View.VISIBLE);
                            String mas = "" + (eventos.length - 2) + " más";
                            lista_info3[x - 1 + diasemana].setText(mas);
                        } else {
                            lista_info3[x - 1 + diasemana].setVisibility(View.INVISIBLE);
                        }
                    } else {
                        lista_info2[x - 1 + diasemana].setVisibility(View.INVISIBLE);
                        lista_info3[x - 1 + diasemana].setVisibility(View.INVISIBLE);
                    }
                } else {
                    lista_info1[x - 1 + diasemana].setVisibility(View.INVISIBLE);
                    lista_info2[x - 1 + diasemana].setVisibility(View.INVISIBLE);
                    lista_info3[x - 1 + diasemana].setVisibility(View.INVISIBLE);
                }

            } catch (Exception ignored) {
            }

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            bundle = new Bundle();
            bundle.putString("URL", getString(R.string.url_update));
            getSupportLoaderManager().restartLoader(0, bundle, Inicio.this);
            proteccion.setVisibility(View.GONE);
        }
    }
}
