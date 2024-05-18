package com.example.sanbotapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

public class DataAdapterModificar extends RecyclerView.Adapter<DataAdapterModificar.DataViewHolder> implements ItemTouchHelperAdapter {

    private Cursor cursor;
    private ModificarActivity modificarActivity;
    private ItemTouchHelper itemTouchHelper;
    private BloqueAccionesDbAdapter mDbHelperBloque;

    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper;
    }

    // Métodos del ItemTouchHelperAdapter
    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        cursor.moveToPosition(fromPosition);
        long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(BloqueAccionesDbAdapter.KEY_ROWID));

        cursor.moveToPosition(toPosition);
        int ordenationTo = cursor.getInt(cursor.getColumnIndexOrThrow(BloqueAccionesDbAdapter.KEY_ORDENACION));

        // Actualizar la ordenación del elemento movido en la base de datos
        mDbHelperBloque.updateOrdenacion(itemId, ordenationTo);

        // Notificar al RecyclerView del cambio
        notifyItemMoved(fromPosition, toPosition);
    }


    public DataAdapterModificar(Cursor cursor, ModificarActivity modificarActivity, BloqueAccionesDbAdapter mDbHelperBloque) {
        this.cursor = cursor;
        this.modificarActivity = modificarActivity;
        this.mDbHelperBloque = mDbHelperBloque;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout_modificar, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        if (cursor != null && cursor.moveToFirst()) {
            cursor.moveToPosition(position);
            String data = cursor.getString(cursor.getColumnIndexOrThrow(BloqueAccionesDbAdapter.KEY_NOMBRE));
            holder.bindData(data);
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private ImageButton buttonAction;
        private ImageButton buttonDelete;
        private ImageButton buttonModificar;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView_item_name);
            buttonAction = itemView.findViewById(R.id.button_play);
            buttonDelete = itemView.findViewById(R.id.button_delete);
            buttonModificar = itemView.findViewById(R.id.button_edit);
        }

        @SuppressLint("SetTextI18n")
        public void bindData(String data) {
            textView.setText(data);
            buttonAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            buttonModificar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Acción para modificar un elemento bloque de acciones
                    int position = getAdapterPosition();
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(BloqueAccionesDbAdapter.KEY_ROWID));
                    Intent i = new Intent(modificarActivity, EditActivity.class);
                    i.putExtra(BloqueAccionesDbAdapter.KEY_ROWID, id);
                    modificarActivity.startActivityForResult(i, 1);
                }
            });
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Acción para eliminar el elemento

                }
            });
        }
    }
}

