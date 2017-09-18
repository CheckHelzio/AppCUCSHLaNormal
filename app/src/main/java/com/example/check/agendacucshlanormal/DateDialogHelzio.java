package com.example.check.agendacucshlanormal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;

public class DateDialogHelzio extends Activity {

    DatePicker datePicker;
    ViewGroup conte;
    Button aceptar, cancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_datepicker);
        postponeEnterTransition();

        conte = findViewById(R.id.conte);
        aceptar = findViewById(R.id.bt_dialog_aceptar);
        cancelar = findViewById(R.id.bt_dialog_cancenlar);
        aceptar.setOnClickListener(this::irDia);
        cancelar.setOnClickListener(this::cerrar);
        datePicker = findViewById(R.id.conteDialog);

        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 0, 1);
        datePicker.setMinDate(calendar.getTimeInMillis());
        datePicker.setFirstDayOfWeek(Calendar.MONDAY);

        Slide slide = new Slide(Gravity.BOTTOM);
        slide.setInterpolator(AnimUtils.getLinearOutSlowInInterpolator(DateDialogHelzio.this));
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        slide.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(slide);

        startPostponedEnterTransition();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        cerrar(null);
    }

    public void irDia(View view) {

        int mes;
        if (datePicker.getYear() == 2016) {
            mes = datePicker.getMonth();
        } else {
            mes = datePicker.getMonth();
            for (int x = 2016; x < datePicker.getYear(); x++) {
                mes += 12;
            }
        }

        Intent i = getIntent();
        i.putExtra("NUMERO_DE_MES", mes);
        setResult(RESULT_OK, i);
        cerrar(null);
    }

    public void cerrar(View view) {
        finishAfterTransition();
    }
}
