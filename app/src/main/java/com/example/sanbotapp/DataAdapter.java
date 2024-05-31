package com.example.sanbotapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.qihancloud.opensdk.function.beans.SpeakOption;
import com.qihancloud.opensdk.function.unit.SpeechManager;

import java.util.ArrayList;
import java.util.Collections;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder> implements ItemTouchHelperAdapter {

    private ArrayList<DataModel> dataList;
    private FuncionalidadesActivity funcionalidadesActivity;
    private EditActivity editActivity;
    private ItemTouchHelper itemTouchHelper;
    private AlertDialog dialog;
    private ImageView IVPreviewImage;
    private Uri selectedImageUriEx;
    int SELECT_PICTURE_ACTION = 202;

    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper;
    }

    public void updateImagePreview(Uri uri) {
        selectedImageUriEx = uri;
        IVPreviewImage.setImageURI(uri);
    }

    // Métodos del ItemTouchHelperAdapter
    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        // Reordena los elementos
        Collections.swap(dataList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public DataAdapter(ArrayList<DataModel> dataList, FuncionalidadesActivity funcionalidadesActivity, EditActivity editActivity) {
        this.dataList = dataList;
        this.funcionalidadesActivity = funcionalidadesActivity;
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
        private ImageButton buttonAddImagen;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView_RV);
            buttonAction = itemView.findViewById(R.id.button_action_RV);
            buttonDelete = itemView.findViewById(R.id.button_delete_RV);
            buttonAddImagen = itemView.findViewById(R.id.button_imagen_RV);
        }

        @SuppressLint("SetTextI18n")
        public void bindData(DataModel data) {
            textView.setText(data.getSpinnerOption() + ": " + data.getText() );
            buttonAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (data.getSpinnerOption().equals("Síntesis de voz")) {
                        funcionalidadesActivity.speakOperation(data.getText(), "Normal");

                    } else if (data.getSpinnerOption().equals("Movimiento de brazos")) {
                        funcionalidadesActivity.moveBrazosOperation(data.getText());

                    } else if (data.getSpinnerOption().equals("Movimiento de cabeza")) {
                        funcionalidadesActivity.moveCabezaOperation(data.getText());

                    } else if (data.getSpinnerOption().equals("Movimiento de ruedas")) {
                        funcionalidadesActivity.moveRuedasOperation(data.getText());

                    } else if (data.getSpinnerOption().equals("Encender LEDs")) {
                        funcionalidadesActivity.encenderLedsOperation(data.getText());

                    } else if (data.getSpinnerOption().equals("Cambio de expresión facial")) {
                        funcionalidadesActivity.changeFaceOperation(data.getText());

                    } else if (data.getSpinnerOption().equals("Insertar imagen")) {
                        funcionalidadesActivity.mostrarImagen(data.getText());
                    } else if (data.getSpinnerOption().equals("Insertar vídeo")) {
                        funcionalidadesActivity.mostrarVideo(data.getText());

                    } else if (data.getSpinnerOption().equals("Pregunta verdadero o falso")) {
                        funcionalidadesActivity.trueFalseOperation(data.getText());
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

                    // Ver elementos restantes
                    for (DataModel data : dataList) {
                        System.out.println("DELETE" + data.getSpinnerOption() + ": " + data.getText());
                    }


                    notifyItemRemoved(position);


                }
            });

            buttonAddImagen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Abre el dialgo para añadir una imagen pasando la posición del elemento
                    int position = getAdapterPosition();
                    mostrarDialogoSubirImagen(position);

                }
            });
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(editActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(editActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }

    @SuppressLint("SetTextI18n")
    private void mostrarDialogoSubirImagen(int position) {


        // Muevete al elemento
        DataModel data = dataList.get(position);


        // Inflar el layout del diálogo personalizado
        LayoutInflater inflater = LayoutInflater.from(editActivity);
        View dialogView = inflater.inflate(R.layout.popup_addimage, null);

        // Obtener referencias a los botones del layout
        Button btnSelectImage = dialogView.findViewById(R.id.btn_select_image);
        Button btnSelectUrl = dialogView.findViewById(R.id.btn_select_url);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnAceptar = dialogView.findViewById(R.id.btn_aceptar);

        // Obtener referencias a los elementos del layout url
        LinearLayout LLUrl = dialogView.findViewById(R.id.layoutEditText);
        EditText ETUrl = dialogView.findViewById(R.id.editTextOption);
        Button btnAnadir = dialogView.findViewById(R.id.buttonAdd);
        // One Preview Image
        IVPreviewImage = dialogView.findViewById(R.id.IVPreviewImage);

        // Poner imagen en el data
        System.out.println("Imagen: " + data.getImagen());
        if (data.getImagen() != null) {
            IVPreviewImage.setVisibility(View.VISIBLE);
            if (data.getImagen().startsWith("http")) {
                Glide.with(editActivity).load(data.getImagen()).into(IVPreviewImage);
            } else {
                IVPreviewImage.setImageURI(Uri.parse(data.getImagen()));
            }

        }


        // Configurar el comportamiento del botón "Cancelar"
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Si el usuario elige "Cancelar", simplemente cierra el diálogo
                dialog.dismiss();
            }
        });

        // Configurar el comportamiento del botón "Seleccionar imagen"
        btnSelectUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImageUriEx = null;
                LLUrl.setVisibility(View.VISIBLE);
                IVPreviewImage.setVisibility(View.GONE);
            }
        });

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Si el usuario elige "Aceptar", se añade la imagen al elemento
                System.out.println("URL: " + ETUrl.getText().toString());
                System.out.println("URI: " + selectedImageUriEx);
                if (selectedImageUriEx != null) {
                    String uri = selectedImageUriEx.toString();
                    // Verificar si el texto y la opción no están vacíos
                    if (!TextUtils.isEmpty(uri)) {
                        System.out.println("ENTRAMOS: " + selectedImageUriEx);
                        // Añadir la imagen al elemento
                        data.setImagen(selectedImageUriEx.toString());
                    }

                } else if ( !ETUrl.toString().isEmpty() ) {
                    System.out.println("AQUI NO ENTRAMOS: " + ETUrl.getText().toString());
                    data.setImagen(ETUrl.getText().toString());

                }
                // Actualizar el dataList
                dataList.set(position, data);
                // Si no hay selected image
                dialog.dismiss();
            }
        });

        // Configurar el comportamiento del botón "Seleccionar imagen"
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();
                IVPreviewImage.setVisibility(View.VISIBLE);
                LLUrl.setVisibility(View.GONE);
                // Si el usuario elige "Seleccionar imagen", se abre la galería
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                editActivity.startActivityForResult(intent, SELECT_PICTURE_ACTION);
            }
        });

        btnAnadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Coger url del edit y mostrar la imagen en el layout de imagen
                if( ETUrl.getText().toString().isEmpty()){
                    Toast.makeText(editActivity, "Introduce una URL", Toast.LENGTH_SHORT).show();
                }else {
                    IVPreviewImage.setVisibility(View.VISIBLE);
                    String url = ETUrl.getText().toString();

                    Glide.with(editActivity)
                            .load(url)
                            .into(IVPreviewImage);

                }

            }
        });

        // Configurar AlertDialog con el layout personalizado y el contexto adecuado
        AlertDialog.Builder builder = new AlertDialog.Builder(editActivity);
        builder.setView(dialogView);

        // Crear y mostrar el diálogo
        dialog = builder.create();
        dialog.show();
    }
}

