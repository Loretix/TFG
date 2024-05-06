package com.example.sanbotapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private ArrayList<String> mDataList;
    private AlertDialog dialog;

    public CustomAdapter(Context context, ArrayList<String> dataList) {
        super(context, 0, dataList);
        mContext = context;
        mDataList = dataList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(mContext).inflate(R.layout.list_item_layout, parent, false);
        }

        String currentItem = mDataList.get(position);

        TextView nameTextView = listItemView.findViewById(R.id.textView_item_name);
        nameTextView.setText(currentItem);

        ImageButton editButton = listItemView.findViewById(R.id.button_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí puedes realizar alguna acción cuando se hace clic en el botón
                Intent intent = new Intent(mContext, EditActivity.class);
                mContext.startActivity(intent);
            }
        });

        ImageButton playButton = listItemView.findViewById(R.id.button_play);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reproducir la presentación juju

            }
        });

        ImageButton deleteButton = listItemView.findViewById(R.id.button_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí puedes realizar alguna acción cuando se hace clic en el botón
                mostrarDialogoConfirmacion( currentItem );
            }
        });

        return listItemView;
    }

    @SuppressLint("SetTextI18n")
    private void mostrarDialogoConfirmacion(String nombrePresentacion) {
        // Inflar el layout del diálogo personalizado
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View dialogView = inflater.inflate(R.layout.popup_delete, null);

        // Obtener referencias a los botones del layout
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnDelete = dialogView.findViewById(R.id.btn_delete);

        TextView textConfirmacion = dialogView.findViewById(R.id.text_confirmacion);
        textConfirmacion.setText("¿Desea eliminar la presentación '" + nombrePresentacion + "'?");


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
                // Después de eliminar, cierra el diálogo
                dialog.dismiss();
            }
        });

        // Configurar AlertDialog con el layout personalizado y el contexto adecuado
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(dialogView);

        // Crear y mostrar el diálogo
        dialog = builder.create();
        dialog.show();
    }

}

