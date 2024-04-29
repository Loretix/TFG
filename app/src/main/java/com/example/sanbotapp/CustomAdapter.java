package com.example.sanbotapp;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private ArrayList<String> mDataList;

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

        Button editButton = listItemView.findViewById(R.id.button_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí puedes realizar alguna acción cuando se hace clic en el botón
                Intent intent = new Intent(mContext, EditActivity.class);
                mContext.startActivity(intent);
            }
        });

        Button playButton = listItemView.findViewById(R.id.button_play);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reproducir la presentación juju

            }
        });

        return listItemView;
    }
}

