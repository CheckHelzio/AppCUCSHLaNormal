package com.example.check.agendacucshlanormal;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ConflictosAdaptador extends RecyclerView.Adapter<ConflictosAdaptador.ConflictosViewHolder> {

    private List<Conflictos> listaConflictos;
    private Context mContext;

    ConflictosAdaptador(List<Conflictos> conflictos, Context context) {
        this.listaConflictos = conflictos;
        mContext = context;
    }

    @Override
    public ConflictosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //return null;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_eventos, parent, false);
        return new ConflictosViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ConflictosViewHolder ConflictosViewHolder, int position) {

        final Conflictos conflicto = listaConflictos.get(position);
        try {
            final Eventos evento = conflicto.getQueEvento();
            String error = "No hay cupo disponible para la fecha No. " + (conflicto.getNum_fecha() + 1) + ":";
            ConflictosViewHolder.titulo_evento.setText(error);
            ConflictosViewHolder.titulo_evento.setTypeface(Typeface.DEFAULT_BOLD);
            String auditorio = "Hay un evento registrado de " + horasATetxto(Integer.parseInt(evento.getHoraInicial().replaceAll("[^0-9]+", ""))) + " - " + horasATetxto(Integer.parseInt(evento.getHoraFinal().replaceAll("[^0-9]+", ""))) + ".";
            ConflictosViewHolder.auditorio.setText(auditorio);
            ConflictosViewHolder.horario.setText(R.string.presiona);
            ConflictosViewHolder.contenedor.setCardBackgroundColor(fondoAuditorio(conflicto.getAuditorio()));

            ConflictosViewHolder.contenedor.setOnClickListener(view -> {
                Intent intent = new Intent(mContext, DialogInfoEventosHelzio.class);
                intent.putExtra("EVENTO", evento);
                intent.putExtra("POSITION", ConflictosViewHolder.getAdapterPosition());
                final Rect startBounds = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                ChangeBoundBackground2.addExtras(intent, getViewBitmap(view), startBounds);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext, view, "fondo");
                ((Activity) mContext).startActivityForResult(intent, 44, options.toBundle());
            });
        } catch (Exception ignored) {
            switch (conflicto.getTipo()) {
                case "F": {
                    String error = "No hay cupo disponible para la fecha No. " + (conflicto.getNum_fecha() + 1) + ":";
                    String error2 = "El horario de la fecha No. " + (conflicto.getNum_fecha_2() + 1) + " tiene preferencia sobre el horario de la fecha No. " + (conflicto.getNum_fecha() + 1);
                    ConflictosViewHolder.titulo_evento.setText(error);
                    ConflictosViewHolder.titulo_evento.setTypeface(Typeface.DEFAULT_BOLD);
                    ConflictosViewHolder.auditorio.setText(error2);
                    ConflictosViewHolder.horario.setText(R.string.mod);
                    ConflictosViewHolder.contenedor.setCardBackgroundColor(Color.parseColor("#121212"));
                    ConflictosViewHolder.contenedor.setOnClickListener(null);
                    break;
                }
                case "V": {
                    String error = "Hay un problema con la fecha No. " + (conflicto.getNum_fecha() + 1) + ":";
                    String error2 = "Los eventos no pueden iniciar después de las 8:30 PM.";
                    ConflictosViewHolder.titulo_evento.setText(error);
                    ConflictosViewHolder.titulo_evento.setTypeface(Typeface.DEFAULT_BOLD);
                    ConflictosViewHolder.auditorio.setText(error2);
                    ConflictosViewHolder.horario.setText(R.string.mod2);
                    ConflictosViewHolder.contenedor.setCardBackgroundColor(Color.parseColor("#121212"));
                    ConflictosViewHolder.contenedor.setOnClickListener(null);
                    break;
                }
                case "V1": {
                    String error = "Hay un problema con la fecha No. " + (conflicto.getNum_fecha() + 1) + ":";
                    String error2 = "La de inicio del evento ya pasó o no tiene por lo menos 60 minutos de diferencia con la hora actual.";
                    ConflictosViewHolder.titulo_evento.setText(error);
                    ConflictosViewHolder.titulo_evento.setTypeface(Typeface.DEFAULT_BOLD);
                    ConflictosViewHolder.auditorio.setText(error2);
                    ConflictosViewHolder.horario.setText(R.string.mod3);
                    ConflictosViewHolder.contenedor.setCardBackgroundColor(Color.parseColor("#121212"));
                    ConflictosViewHolder.contenedor.setOnClickListener(null);
                    break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return listaConflictos.size();
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

    private int fondoAuditorio(String numero) {
        int st = 0;
        switch (numero) {
            case "1":
                st = mContext.getResources().getColor(R.color.ed_a);
                break;
            case "2":
                st = mContext.getResources().getColor(R.color.ed_b);
                break;
            case "3":
                st = mContext.getResources().getColor(R.color.ed_c);
                break;
            case "4":
                st = mContext.getResources().getColor(R.color.ed_d);
                break;
            case "5":
                st = mContext.getResources().getColor(R.color.ed_e);
                break;
        }
        return st;
    }

    static class ConflictosViewHolder extends RecyclerView.ViewHolder {
        private TextView titulo_evento, auditorio, horario;
        private CardView contenedor;

        ConflictosViewHolder(View itemView) {
            super(itemView);
            titulo_evento = itemView.findViewById(R.id.tv_titulo);
            auditorio = itemView.findViewById(R.id.tv_auditorio);
            horario = itemView.findViewById(R.id.tv_horario);
            contenedor = itemView.findViewById(R.id.boton_eventos);
        }
    }
}
