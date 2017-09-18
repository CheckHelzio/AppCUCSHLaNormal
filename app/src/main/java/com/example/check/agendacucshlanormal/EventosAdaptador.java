package com.example.check.agendacucshlanormal;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class EventosAdaptador extends RecyclerView.Adapter<EventosAdaptador.EventosViewHolder> {

    private final int ELIMINAR_EVENTO = 4;
    private List<Eventos> eventos;
    private Context mContext;

    EventosAdaptador(List<Eventos> eventos, Context context) {
        this.eventos = eventos;
        mContext = context;
    }

    @Override
    public EventosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //return null;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_eventos, parent, false);
        return new EventosViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final EventosViewHolder eventosViewHolder, int position) {
        final Eventos evento = eventos.get(position);
        eventosViewHolder.titulo_evento.setText(evento.getTitulo());
        eventosViewHolder.nombre_org.setText(evento.getNombreOrganizador());
        eventosViewHolder.auditorio.setText(nombreAuditorio(evento.getAuditorio()));
        String hora = horasATetxto(Integer.parseInt(evento.getHoraInicial().replaceAll("[^0-9]+", ""))) + " - " + horasATetxto(Integer.parseInt(evento.getHoraFinal().replaceAll("[^0-9]+", "")));
        eventosViewHolder.horario.setText(hora);
        eventosViewHolder.contenedor.setCardBackgroundColor(evento.getFondo());

        eventosViewHolder.contenedor.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, DialogInfoEventosHelzio.class);
            intent.putExtra("EVENTO", evento);
            intent.putExtra("POSITION", eventosViewHolder.getAdapterPosition());
            final Rect startBounds = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            ChangeBoundBackground2.addExtras(intent, getViewBitmap(view), startBounds);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext, view, "fondo");
            ((Activity) mContext).startActivityForResult(intent, ELIMINAR_EVENTO, options.toBundle());
        });
    }

    private String horasATetxto(int numero) {
        String am_pm, st_min, st_hora;

        int hora = (numero / 2) + 7;
        if (hora == 12) {
            am_pm = " PM";
        } else if (hora > 12) {
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

    @Override
    public int getItemCount() {
        return eventos.size();
    }

    void removeItemAtPosition(int position) {
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, eventos.size());

    }

    void addItem(Eventos evento) {
        eventos.add(evento);
        notifyItemInserted(eventos.size() - 1);
    }

    void ordenarItems() {
        ordenar();
        notifyDataSetChanged();
    }

    private void ordenar() {
        Collections.sort(eventos, (e1, e2) -> {
            Integer i1 = Integer.parseInt(e1.getFecha().replaceAll("[^0-9]+", ""));
            Integer i2 = Integer.parseInt(e2.getFecha().replaceAll("[^0-9]+", ""));
            if (i1.equals(i2)) {
                Integer i3 = Integer.parseInt(e1.getHoraInicial());
                Integer i4 = Integer.parseInt(e2.getHoraInicial());
                if (i3.equals(i4)) {
                    Integer i5 = Integer.parseInt(e1.getHoraFinal());
                    Integer i6 = Integer.parseInt(e2.getHoraFinal());
                    return i5.compareTo(i6);
                } else {
                    return i3.compareTo(i4);
                }
            } else {
                return i1.compareTo(i2);
            }
        });
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

    static class EventosViewHolder extends RecyclerView.ViewHolder {
        private TextView titulo_evento, nombre_org, auditorio, horario;
        private CardView contenedor;

        EventosViewHolder(View itemView) {
            super(itemView);
            titulo_evento = itemView.findViewById(R.id.tv_titulo);
            nombre_org = itemView.findViewById(R.id.tv_organizador);
            auditorio = itemView.findViewById(R.id.tv_auditorio);
            horario = itemView.findViewById(R.id.tv_horario);
            contenedor = itemView.findViewById(R.id.boton_eventos);
        }
    }

}
