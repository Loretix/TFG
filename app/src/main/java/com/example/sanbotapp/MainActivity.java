package com.example.sanbotapp;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;

import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.unit.HandMotionManager;
import com.qihancloud.opensdk.function.unit.HardWareManager;
import com.qihancloud.opensdk.function.unit.HeadMotionManager;
import com.qihancloud.opensdk.function.unit.ProjectorManager;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.SystemManager;
import com.qihancloud.opensdk.function.unit.WheelMotionManager;

import java.util.ArrayList;


public class MainActivity extends TopBaseActivity {
    private ListView presentaciones;
    private PresentacionesDbAdapter mDbHelper;
    private BloqueAccionesDbAdapter mDbHelperBloqueAcciones;
    private AccionesDbAdapter mDbHelperAcciones;
    private Button addNew;
    private AlertDialog dialog;

    private SpeechManager speechManager;
    private SystemManager systemManager;
    private HandMotionManager handMotionManager;
    private HeadMotionManager headMotionManager;
    private HardWareManager hardWareManager; //leds //touch sensors //voice locate //gyroscope
    private WheelMotionManager wheelMotionManager;
    private ProjectorManager projectorManager;

    private FuncionalidadesActivity funcionalidadesActivity;
    private boolean movnatural, ruido, localizacion, personas, facial;

    private final int PRESENTACIONES_LIMIT = 100;
    private int presentacionesLimit;

    @Override
    protected void onMainServiceConnected() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        onMainServiceConnected();
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_title);

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);
        handMotionManager = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);
        headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
        hardWareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);
        wheelMotionManager = (WheelMotionManager) getUnitManager(FuncConstant.WHEELMOTION_MANAGER);
        projectorManager = (ProjectorManager) getUnitManager(FuncConstant.PROJECTOR_MANAGER);

        funcionalidadesActivity = new FuncionalidadesActivity(speechManager, systemManager, handMotionManager,
                headMotionManager, hardWareManager, wheelMotionManager, MainActivity.this);

        presentaciones = findViewById(R.id.listView);
        addNew = findViewById(R.id.button_add_presentation);

        // Database Connection
        mDbHelper = new PresentacionesDbAdapter(this);
        mDbHelper.open();
        fillData();

        mDbHelperAcciones = new AccionesDbAdapter(this);
        mDbHelperAcciones.open();

        mDbHelperBloqueAcciones = new BloqueAccionesDbAdapter(this);
        mDbHelperBloqueAcciones.open();

        Intent toCreatePresentacion = new Intent(MainActivity.this, ModificarActivity.class);
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (presentacionesLimit < PRESENTACIONES_LIMIT) {
                    startActivityForResult(toCreatePresentacion, 0);
                } else {
                    Toast.makeText(MainActivity.this, "Limite de presentaciones alcanzado", Toast.LENGTH_LONG).show();
                }
            }
        });

        // TODO: no se porque no funciona
       // funcionalidadesActivity.speakOperation("¡Bienvenido! ¿Estás listo para crear una nueva presentación?", "Normal");

    }

    private void fillData() {
        Cursor notesCursor = mDbHelper.fetchAllPresentaciones();
        presentacionesLimit = notesCursor.getCount();
        String[] from = new String[]{PresentacionesDbAdapter.KEY_NOMBRE, PresentacionesDbAdapter.KEY_ROWID};
        int[] to = new int[]{R.id.textView_item_name};

        CustomCursorAdapter adapter = new CustomCursorAdapter(this, R.layout.list_item_layout, notesCursor, from, to);
        presentaciones.setAdapter(adapter);
    }

    private class CustomCursorAdapter extends SimpleCursorAdapter {
        public CustomCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
            super(context, layout, c, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_layout, parent, false);
            }

            // Mover el cursor a la posición correcta
            Cursor cursor = getCursor();
            cursor.moveToPosition(position);

            // Obtener referencias a los botones de editar y borrar
            ImageButton actionButton = convertView.findViewById(R.id.button_play);
            ImageButton editButton = convertView.findViewById(R.id.button_edit);
            ImageButton deleteButton = convertView.findViewById(R.id.button_delete);

            // Configurar el texto de la vista
            TextView textView = convertView.findViewById(R.id.textView_item_name);
            textView.setText(cursor.getString(cursor.getColumnIndexOrThrow(PresentacionesDbAdapter.KEY_NOMBRE)));

            // Obtener el ID de la presentación
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(PresentacionesDbAdapter.KEY_ROWID));

            // Obtener el nombre de la presentación
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow(PresentacionesDbAdapter.KEY_NOMBRE));

            // Configurar los OnClickListener para los botones
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, ModificarActivity.class);
                    intent.putExtra(PresentacionesDbAdapter.KEY_ROWID, id);
                    startActivityForResult(intent, 1);
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mostrarDialogoConfirmacion(nombre, id);
                }
            });

            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<DataModel> list = mDbHelperBloqueAcciones.getDatosPresentacion(id, mDbHelperAcciones);
                    mostrarDialogoPresentar(id, list);

                }
            });

            return convertView;
        }
    }



    @SuppressLint("SetTextI18n")
    private void mostrarDialogoConfirmacion(String nombrePresentacion, long id) {
        // Inflar el layout del diálogo personalizado
        LayoutInflater inflater = LayoutInflater.from(this);
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
                mDbHelper.deletePresentacion(id);
                fillData();
                // Después de eliminar, cierra el diálogo
                dialog.dismiss();
            }
        });

        // Configurar AlertDialog con el layout personalizado y el contexto adecuado
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Crear y mostrar el diálogo
        dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void mostrarDialogoPresentar(long id, ArrayList<DataModel> list) {
        // Inflar el layout del diálogo personalizado
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.popup_proyectar, null);

        // Obtener referencias a los botones del layout
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnDelete = dialogView.findViewById(R.id.btn_delete);

        TextView textConfirmacion = dialogView.findViewById(R.id.text_confirmacion);
        textConfirmacion.setText("¿Desea proyectar la presentación?");


        // Configurar el comportamiento del botón "Cancelar"
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

                Cursor notesCursor = mDbHelper.fetchPresentacion(id);

                movnatural = (notesCursor.getInt(notesCursor.getColumnIndexOrThrow(PresentacionesDbAdapter.KEY_MOVNATURAL)) == 1);
                ruido = (notesCursor.getInt(notesCursor.getColumnIndexOrThrow(PresentacionesDbAdapter.KEY_RUIDO)) == 1);
                localizacion = (notesCursor.getInt(notesCursor.getColumnIndexOrThrow(PresentacionesDbAdapter.KEY_LOCALIZACION)) == 1);
                personas = (notesCursor.getInt(notesCursor.getColumnIndexOrThrow(PresentacionesDbAdapter.KEY_PERSONAS)) == 1);
                facial = (notesCursor.getInt(notesCursor.getColumnIndexOrThrow(PresentacionesDbAdapter.KEY_FACIAL)) == 1);


                Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                intent.putExtra("dataList", list);
                //pasar los booleanos del modulo reactivo
                intent.putExtra("movnatural", movnatural);
                intent.putExtra("ruido", ruido);
                intent.putExtra("localizacion", localizacion);
                intent.putExtra("personas", personas);
                intent.putExtra("facial", facial);
                startActivity(intent);

            }
        });

        // Configurar el comportamiento del botón "Eliminar"
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

                // TODO: Encender el proyector

                projectorManager.switchProjector(true);

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                projectorManager.setMode(ProjectorManager.MODE_WALL);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                projectorManager.setBright(31);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                projectorManager.setTrapezoidV(30);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                intent.putExtra("dataList", list);
                intent.putExtra("movnatural", movnatural);
                intent.putExtra("ruido", ruido);
                intent.putExtra("localizacion", localizacion);
                intent.putExtra("personas", personas);
                intent.putExtra("facial", facial);
                // Cuando se vuelva de la actividad de proyección, se apaga el proyector
                startActivityForResult(intent, 1);
            }
        });

        // Configurar AlertDialog con el layout personalizado y el contexto adecuado
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Crear y mostrar el diálogo
        dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
        // Apagar el proyector
        projectorManager.switchProjector(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
