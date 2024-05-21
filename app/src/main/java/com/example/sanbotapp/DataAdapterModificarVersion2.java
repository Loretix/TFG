package com.example.sanbotapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

public class DataAdapterModificarVersion2 extends RecyclerView.Adapter<DataAdapterModificarVersion2.DataViewHolder> implements ItemTouchHelperAdapter {

    private ArrayList<Long> dataList;
    private ModificarActivity modificarActivity;
    private ItemTouchHelper itemTouchHelper;
    private BloqueAccionesDbAdapter mDbHelperBloque;
    private AlertDialog dialog;

    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(dataList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }



    public DataAdapterModificarVersion2(ArrayList<Long> dataList, ModificarActivity modificarActivity, BloqueAccionesDbAdapter mDbHelperBloque) {
        this.dataList = dataList;
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
        Long data = dataList.get(position);
        holder.bindData(String.valueOf(data));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
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
            textView.setText(mDbHelperBloque.getNombreBloque(Long.parseLong(data)));
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
                    long id = dataList.get(position);
                    Intent i = new Intent(modificarActivity, EditActivity.class);
                    i.putExtra("BLOCK_ID", id);
                    modificarActivity.startActivityForResult(i, 1);
                }
            });
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Acción para eliminar el elemento
                    int position = getAdapterPosition();
                    long id = dataList.get(position);
                    mostrarDialogoConfirmacion(textView.getText().toString(), id);

                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    private void mostrarDialogoConfirmacion(String nombrePresentacion, long id) {
        // Inflar el layout del diálogo personalizado
        LayoutInflater inflater = LayoutInflater.from(modificarActivity);
        View dialogView = inflater.inflate(R.layout.popup_delete, null);

        // Obtener referencias a los botones del layout
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnDelete = dialogView.findViewById(R.id.btn_delete);

        TextView textConfirmacion = dialogView.findViewById(R.id.text_confirmacion);
        textConfirmacion.setText("¿Desea eliminar el bloque de acciones '" + nombrePresentacion + "'?");


        // Configurar el comportamiento del botón "Cancelar"
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Si el usuario elige "Cancelar", simplemente cierra el diálogo
                dialog.dismiss();
            }
        });

        // Configurar el comportamiento del botón "Eliminar"
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí puedes colocar el código para eliminar el elemento
                // Por ejemplo, eliminar un elemento de una lista o realizar una acción de eliminación
                mDbHelperBloque.deleteBloqueAcciones(id);
                modificarActivity.fillData();
                // Después de eliminar, cierra el diálogo
                dialog.dismiss();
            }
        });

        // Configurar AlertDialog con el layout personalizado y el contexto adecuado
        AlertDialog.Builder builder = new AlertDialog.Builder(modificarActivity);
        builder.setView(dialogView);

        // Crear y mostrar el diálogo
        dialog = builder.create();
        dialog.show();
    }
}

