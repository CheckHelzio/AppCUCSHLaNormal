package com.example.check.agendacucshlanormal;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.check.agendacucshlanormal.util.EliminarEvento;
import com.example.check.agendacucshlanormal.util.RegistrarUpdate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DialogInfoEventosHelzio extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Object> {

    private static final String TAG = DialogInfoEventosHelzio.class.getSimpleName();
    private final int EDITAR_EVENTO = 33;
    private RelativeLayout fondo;
    private EditText et_pin;
    private CoordinatorLayout snackposs;
    private ImageButton fab_edit;
    private Boolean pin_correcto_eliminar = false;
    private String alerta = "";
    private Eventos evento;
    private TextView tv_titulo;
    private TextView tv_auditorios;
    private TextView tv_tipoActividad;
    private TextView tv_fecha;
    private TextView tv_horario;
    private TextView tv_organizador;
    private TextView tv_num_tel;
    private TextView tv_nota;
    private TextView tv_id;
    private TextView tv_marca_agua;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_info_evento);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        postponeEnterTransition();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        snackposs = findViewById(R.id.snackposs);
        fab_edit = findViewById(R.id.fab_edit);

        fab_edit.setOnClickListener(v -> EditarEnvento());

        tv_titulo = findViewById(R.id.titulo);
        tv_auditorios = findViewById(R.id.tv_auditorios);
        tv_tipoActividad = findViewById(R.id.tv_tipo_actividad);
        tv_fecha = findViewById(R.id.tv_fecha);
        tv_horario = findViewById(R.id.tv_horario);
        tv_organizador = findViewById(R.id.tv_nombre_organizador);
        tv_num_tel = findViewById(R.id.tv_num_tel);
        tv_nota = findViewById(R.id.tv_notas);
        tv_id = findViewById(R.id.tv_id);
        tv_marca_agua = findViewById(R.id.marca_agua);
        fondo = findViewById(R.id.fondo);
        et_pin = findViewById(R.id.et_pin);
        ImageView iv_delete = findViewById(R.id.iv_delete);

        evento = getIntent().getParcelableExtra("EVENTO");
        tv_titulo.setText(evento.getTitulo());
        tv_auditorios.setText(nombreAuditorio(evento.getAuditorio()));
        tv_tipoActividad.setText(evento.getTipoEvento());
        tv_fecha.setText(fecha(evento.getFecha()));
        String hora = horasATetxto(Integer.parseInt(evento.getHoraInicial().replaceAll("[^0-9]+", ""))) + " - " + horasATetxto(Integer.parseInt(evento.getHoraFinal().replaceAll("[^0-9]+", "")));
        tv_horario.setText(hora);
        tv_organizador.setText(evento.getNombreOrganizador());
        tv_num_tel.setText(evento.getNumTelOrganizador().equals("") ? "Sin número telefónico" : evento.getNumTelOrganizador());
        tv_nota.setText(evento.getNotas());
        tv_id.setText(evento.getId());
        String marca_agua = status(evento.getStatusEvento()) + " por " + evento.getQuienR() + "  -  " + evento.getCuandoR().split("~")[0] + " a las " + evento.getCuandoR().split("~")[1];
        tv_marca_agua.setText(marca_agua);
        fondo.setBackgroundColor(evento.getFondo());

        et_pin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                switch (et_pin.getText().toString()) {
                    case "1308":
                        pin_correcto_eliminar = true;
                        et_pin.setTextColor(Color.WHITE);
                        break;
                    case "2886":
                        pin_correcto_eliminar = true;
                        et_pin.setTextColor(Color.WHITE);
                        break;
                    case "4343":
                        pin_correcto_eliminar = true;
                        et_pin.setTextColor(Color.WHITE);
                        break;
                    default:
                        pin_correcto_eliminar = false;
                        et_pin.setTextColor(Color.RED);
                        break;
                }
            }
        });

        iv_delete.setOnClickListener(view -> {
            if (pin_correcto_eliminar) {
                if (Eliminar(evento.getFecha(), evento.getHoraInicial(), evento.getHoraFinal())) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DialogInfoEventosHelzio.this);
                    alertDialogBuilder.setMessage("\n" + (alerta.equals("") ? "¿Deseas eliminar este evento?" : alerta));
                    alertDialogBuilder.setPositiveButton("ACEPTAR",
                            (arg0, arg1) -> getSupportLoaderManager().initLoader(0, null, DialogInfoEventosHelzio.this));
                    alertDialogBuilder.setNegativeButton("CANCELAR",
                            null);
                    alertDialogBuilder.create().show();
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DialogInfoEventosHelzio.this);
                    alertDialogBuilder.setMessage("\n" + (alerta.equals("") ? "Este evento ya ha finalizado, no se puede eliminar" : alerta));
                    alertDialogBuilder.setPositiveButton("ACEPTAR", null);
                    alertDialogBuilder.create().show();
                }
            } else {
                Snackbar snack = Snackbar.make(snackposs, "La contraseña que ingresaste es incorrecta", Snackbar.LENGTH_LONG);
                snack.show();
            }
        });

        fondo.postDelayed(() -> {
                    final Rect endBounds = new Rect(fondo.getLeft(), fondo.getTop(), fondo.getRight(), fondo.getBottom());
                    ChangeBoundBackground2.setup(DialogInfoEventosHelzio.this, fondo, endBounds, getViewBitmap(fondo));
                    getWindow().getSharedElementEnterTransition();
                    startPostponedEnterTransition();
                }

                , 30);
    }

    private String status(String statusEvento) {
        switch (statusEvento) {
            case "R":
                statusEvento = "Registrado";
                break;
            case "E":
                statusEvento = "Editado";
                break;
        }
        return statusEvento;
    }

    private boolean Eliminar(String fecha, String hora_inicial, String hora_final) {
        Boolean eliminar = false;
        //CALENDARIO CON LA FECHA DE HOY
        Calendar c = Calendar.getInstance();

        //CALENDARIO CON LA FECHA FINAL Y HORA FINAL DEL EVENTO
        Calendar e = Calendar.getInstance();
        e.set(2016, 0, 1);
        e.set(Calendar.DAY_OF_YEAR, Integer.parseInt(fecha));
        horasaCalendario(e, hora_final);

        //SI LA FECHA FINAL Y HORA FINAL DEL EVENTO EN ANTERIOR A LA FECHA DE HOY EL EVENTO NO SE PUEDE ELIMINAR PORQUE YA TERMINO
        if (e.getTimeInMillis() < c.getTimeInMillis()) {
            eliminar = false;
        } else if (e.getTimeInMillis() > c.getTimeInMillis()) {
            //LA HORA FINAL ES MAYOR, HAY QUE CHECAR LA INICIAL PARA SABER SI EL EVENTO ESTA AHORITA O NO
            horasaCalendario(e, hora_inicial);

            //SI LA HORA INICIAL ES MAYOR TAMBIEN QUIERE DECIR QUE EL EVENTO AUN NO COMIENZA
            if (e.getTimeInMillis() > c.getTimeInMillis()) {
                eliminar = true;
            } else {
                //SI LA HORA INICIAL ES MENOR ENTONCES EL EVENTO YA COMENZO
                eliminar = false;
                alerta = "Una sesión de este evento esta ocurriendo ahora mismo, no se puede eliminar";
            }
        }
        return eliminar;
    }

    private void horasaCalendario(Calendar e, String hora_inicial) {

        int hora = (Integer.parseInt(hora_inicial) / 2) + 7;
        e.set(Calendar.HOUR_OF_DAY, hora);

        if (Integer.parseInt(hora_inicial) % 2 != 0) {
            e.set(Calendar.MINUTE, 30);
        }
    }

    public void EditarEnvento() {
        if (Eliminar(evento.getFecha(), evento.getHoraInicial(), evento.getHoraFinal())) {
            Intent intent = new Intent(this, RegistrarEvento.class);
            intent.putExtra("DONDE", "PRINCIPAL");
            intent.putExtra("EVENTO", evento);
            FabTransition.addExtras(intent, (Integer) getAcentColor(), R.drawable.ic_edit);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, fab_edit,
                    getString(R.string.transition_date_dialog_helzio));
            startActivityForResult(intent, EDITAR_EVENTO, options.toBundle());
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DialogInfoEventosHelzio.this);
            alertDialogBuilder.setMessage("\n" + (alerta.equals("") ? "Este evento ya ha finalizado, no se puede editar" : alerta.replace("eliminar", "editar")));
            alertDialogBuilder.setPositiveButton("ACEPTAR", null);

            alertDialogBuilder.create().show();
        }
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

    private String fecha(String fecha_inicial) {
        Calendar fecha = Calendar.getInstance();
        fecha.set(2016, 0, 1);
        fecha.add(Calendar.DAY_OF_YEAR, Integer.valueOf(fecha_inicial.replaceAll("[^0-9]+", "")) - 1);
        SimpleDateFormat format = new SimpleDateFormat("cccc',' d 'de' MMMM 'del' yyyy", Locale.getDefault());
        fecha_inicial = (format.format(fecha.getTime()));
        return fecha_inicial;
    }

    private String nombreAuditorio(String numero) {
        String st = "";
        switch (numero) {
            case "1":
                st = "Auditorio Salvador Allende";
                break;
            case "2":
                st = "Auditorio Silvano Barba";
                break;
            case "3":
                st = "Auditorio Carlos Ramírez";
                break;
            case "4":
                st = "Auditorio Adalberto Navarro";
                break;
            case "5":
                st = "Sala de Juicios Orales Mariano Otero";
                break;
        }
        return st;
    }

    @Override
    public void onBackPressed() {
        dismiss(null);
    }

    public void dismiss(View view) {
        finishAfterTransition();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String s_fecha = evento.getFecha();

            Log.d(TAG, "onActivityResult: EDITAR EVENTO - Result ok");
            ArrayList<Eventos> listaEventosNuevos = data.getParcelableArrayListExtra("LISTA");
            evento = listaEventosNuevos.get(0);
            tv_titulo.setText(evento.getTitulo());
            Log.d(TAG, "onActivityResult: " + evento.getTitulo());
            tv_auditorios.setText(nombreAuditorio(evento.getAuditorio()));
            tv_tipoActividad.setText(evento.getTipoEvento());
            tv_fecha.setText(fecha(evento.getFecha()));
            String hora = horasATetxto(Integer.parseInt(evento.getHoraInicial().replaceAll("[^0-9]+", ""))) + " - " + horasATetxto(Integer.parseInt(evento.getHoraFinal().replaceAll("[^0-9]+", "")));
            tv_horario.setText(hora);
            tv_organizador.setText(evento.getNombreOrganizador());
            tv_num_tel.setText(evento.getNumTelOrganizador().equals("") ? "Sin número telefónico" : evento.getNumTelOrganizador());
            tv_nota.setText(evento.getNotas());
            tv_id.setText(evento.getId());
            String marca_agua = status(evento.getStatusEvento()) + " por " + evento.getQuienR() + "  -  " + evento.getCuandoR().split("~")[0] + " a las " + evento.getCuandoR().split("~")[1];
            tv_marca_agua.setText(marca_agua);
            fondo.setBackgroundColor(evento.getFondo());

            ArrayList<Eventos> listaDeRetorno = new ArrayList<>();
            for (Eventos e : listaEventosNuevos) {
                if (e.getFecha().equals(s_fecha)) {
                    listaDeRetorno.add(e);
                }
            }
            Intent i = getIntent();
            i.putParcelableArrayListExtra("LISTA", listaDeRetorno);
            setResult(RESULT_FIRST_USER, i);
        }
    }

    private String horasATetxto(int numero) {
        String am_pm, st_min, st_hora;

        int hora = (numero / 2) + 7;
        if (hora > 12) {
            hora = hora - 12;
            am_pm = " PM";
        } else {
            am_pm = " AM";
        }

        if (hora < 10) {
            st_hora = "0" + hora;
        } else {
            st_hora = "" + hora;
        }

        if (numero % 2 == 0) {
            st_min = "00";
        } else {
            st_min = "30";
        }

        return st_hora + ":" + st_min + am_pm;
    }

    @Override
    public android.support.v4.content.Loader<Object> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case 0:
                return new EliminarEvento(this, evento.aTag());
            case 1:
                return new RegistrarUpdate(this);
            default:
                return new EliminarEvento(this, evento.aTag());
        }
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Object> loader, Object data) {
        switch (loader.getId()) {
            case 0:
                if (data == null) {
                    Snackbar.make(snackposs, "Hay un problema con la conexión a la base de datos. Verifica tu conexión a internet.", Snackbar.LENGTH_LONG).show();
                } else {
                    getSupportLoaderManager().initLoader(1, null, this);
                }
                break;
            case 1:
                if (data != null) {
                    SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs), Context.MODE_PRIVATE);
                    prefs.edit().putString(getString(R.string.prefs_st_eventos_guardados), data.toString()).apply();
                    Intent i = getIntent();
                    i.putExtra("POSITION", i.getIntExtra("POSITION", 0));
                    setResult(RESULT_OK, i);
                    finishAfterTransition();
                } else {
                    Snackbar.make(snackposs, "Hay un problema con la conexión a la base de datos. Verifica tu conexión a internet.", Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Object> loader) {

    }
}
