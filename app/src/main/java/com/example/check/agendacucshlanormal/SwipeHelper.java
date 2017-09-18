package com.example.check.agendacucshlanormal;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class SwipeHelper extends ItemTouchHelper.SimpleCallback {

    private FechasAdaptador adaptador;

    SwipeHelper(FechasAdaptador adaptador) {
        super(ItemTouchHelper.LEFT, ItemTouchHelper.RIGHT);
        this.adaptador = adaptador;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        adaptador.removeItemAtPosition(viewHolder.getAdapterPosition());
    }
}
