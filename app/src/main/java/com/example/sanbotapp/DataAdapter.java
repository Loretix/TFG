package com.example.sanbotapp;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.qihancloud.opensdk.function.beans.SpeakOption;
import com.qihancloud.opensdk.function.unit.SpeechManager;

import java.util.ArrayList;
import java.util.Collections;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder> implements ItemTouchHelperAdapter {

    private ArrayList<DataModel> dataList;
    private EditActivity editActivity;
    private ItemTouchHelper itemTouchHelper;

    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper;
    }

    // Métodos del ItemTouchHelperAdapter
    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        // Reordena los elementos
        Collections.swap(dataList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public DataAdapter(ArrayList<DataModel> dataList, EditActivity editActivity) {
        this.dataList = dataList;
        this.editActivity = editActivity;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data_rv, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        DataModel data = dataList.get(position);
        holder.bindData(data);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private ImageButton buttonAction;
        private ImageButton buttonDelete;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView_RV);
            buttonAction = itemView.findViewById(R.id.button_action_RV);
            buttonDelete = itemView.findViewById(R.id.button_delete_RV);
        }

        @SuppressLint("SetTextI18n")
        public void bindData(DataModel data) {
            textView.setText(data.getSpinnerOption() + ": " + data.getText() );
            buttonAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (data.getSpinnerOption().equals("Síntesis de voz")) {
                        editActivity.speakOperation(data.getText(), "Normal");

                    } else if (data.getSpinnerOption().equals("Movimiento de brazos")) {

                    } else if (data.getSpinnerOption().equals("Movimiento de cabeza")) {

                    } else if (data.getSpinnerOption().equals("Movimiento de ruedas")) {

                    } else if (data.getSpinnerOption().equals("Encender LEDs")) {

                    } else if (data.getSpinnerOption().equals("Cambio de expresión facial")) {
                        editActivity.changeFaceOperation(data.getText());

                    } else if (data.getSpinnerOption().equals("Insertar imagen")) {

                    } else if (data.getSpinnerOption().equals("Insertar vídeo")) {

                    } else {
                        // No se ha seleccionado ninguna opción
                    }
                }
            });
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Acción para eliminar el elemento
                    int position = getAdapterPosition();
                    dataList.remove(position);
                    notifyItemRemoved(position);
                }
            });
        }
    }
}

