package com.example.sanbotapp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 * <p>
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class BloqueAccionesDbAdapter {

    public static final String KEY_NOMBRE = "nombre";
    public static final String KEY_ORDENACION = "ordenacion";
    public static final String KEY_ID_PRESENTACIONES = "idPresentaciones";
    public static final String KEY_ROWID = "_id";

    private static final String DATABASE_TABLE = "bloques_acciones";
    private final Context mCtx;

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    public BloqueAccionesDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     * initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public BloqueAccionesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new presentation using the name, presentation id and order provided. If the presentation is
     * successfully created return the new rowId for that presentation, otherwise return
     * a -1 to indicate failure.
     *
     * @param nombre      the name of the action block
     * @param idPresentaciones the id of the presentation
     * @return rowId or -1 if failed
     */

    public long createBloqueAcciones(String nombre, long idPresentaciones) {
        long result = -1;
        try {
            if (nombre == null || nombre.length() == 0) {
                return result;
            }

            // Obtener el número de ordenación más alto actualmente en la base de datos para esta presentación
            long maxOrdenacion = getMaxOrdenacion(idPresentaciones);

            // Incrementar el número de ordenación en uno para el nuevo bloque
            long ordenacion = maxOrdenacion + 1;

            // Crear el nuevo bloque de acciones
            result = createBloqueAccionesAux(nombre, idPresentaciones, ordenacion);
        } catch (Exception e) {
            Log.e(DATABASE_TABLE, "Error al crear el bloque de acciones", e);
        }
        return result;
    }

    public long createBloqueAccionesAux(String nombre, long idPresentaciones, long ordenacion) {
        long result = 0;
        try {
            if (nombre == null || nombre.length() <= 0 ) { result =  -1; }
            else if (idPresentaciones <= -1) { return -1; }
            else if (ordenacion <= 0) { return -1; }
        } catch (Exception e) {
            Log.w(DATABASE_TABLE, e.getStackTrace().toString());
        }

        if (result != -1) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_NOMBRE, nombre);
            initialValues.put(KEY_ID_PRESENTACIONES, idPresentaciones);
            initialValues.put(KEY_ORDENACION, ordenacion);
            result = mDb.insert(DATABASE_TABLE, null, initialValues);
        }

        return result;
    }

    private long getMaxOrdenacion(long idPresentaciones) {
        long maxOrdenacion = 0;
        Cursor cursor = null;
        try {
            String query = "SELECT MAX(" + KEY_ORDENACION + ") FROM " + DATABASE_TABLE +
                    " WHERE " + KEY_ID_PRESENTACIONES + " = " + idPresentaciones;
            cursor = mDb.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                maxOrdenacion = cursor.getLong(0);
            }
        } catch (Exception e) {
            Log.e(DATABASE_TABLE, "Error al obtener el máximo de ordenación", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return maxOrdenacion;
    }

    /**
     * Delete the action block with the given rowId
     *
     * @param rowId id of action block to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteBloqueAcciones(long rowId) {
        try {
            if (rowId < 1) {
                return false;
            }

            // Obtener el número de ordenación del bloque que se va a eliminar
            long ordenacionAEliminar = getOrdenacion(rowId);

            // Eliminar el bloque de acciones de la base de datos
            boolean deleteResult = mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;

            // Si se eliminó correctamente, actualizar los números de ordenación de los bloques restantes
            if (deleteResult) {
                updateOrdenacionAfterDelete(ordenacionAEliminar);
            }

            return deleteResult;
        } catch (Exception e) {
            Log.e(DATABASE_TABLE, "Error al eliminar el bloque de acciones", e);
            return false;
        }
    }

    private long getOrdenacion(long rowId) {
        long ordenacion = 0;
        Cursor cursor = null;
        try {
            String query = "SELECT " + KEY_ORDENACION + " FROM " + DATABASE_TABLE +
                    " WHERE " + KEY_ROWID + " = " + rowId;
            cursor = mDb.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                ordenacion = cursor.getLong(0);
            }
        } catch (Exception e) {
            Log.e(DATABASE_TABLE, "Error al obtener la ordenación del bloque de acciones", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return ordenacion;
    }

    private void updateOrdenacionAfterDelete(long deletedOrdenacion) {
        try {
            // Actualizar los números de ordenación de los bloques restantes
            ContentValues values = new ContentValues();
            values.put(KEY_ORDENACION, KEY_ORDENACION + " - 1");
            mDb.update(DATABASE_TABLE, values, KEY_ORDENACION + " > ?",
                    new String[] { String.valueOf(deletedOrdenacion) });
        } catch (Exception e) {
            Log.e(DATABASE_TABLE, "Error al actualizar los números de ordenación", e);
        }
    }

    public boolean deleteAllBloqueAcciones() {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + KEY_ROWID, null) > 0;
    }

    /**
     * Return a Cursor over the list of all presentations in the database
     *
     * @return Cursor over all notes
     */
    public Cursor fetchAllBloqueAcciones(long idPresentaciones) {
        return mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_NOMBRE, KEY_ID_PRESENTACIONES, KEY_ORDENACION}, KEY_ID_PRESENTACIONES + "=" + idPresentaciones, null, null, null, KEY_ORDENACION);
    }


    /**
     * Return a Cursor positioned at the presentation that matches the given rowId
     *
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchBloqueAcciones(long rowId) throws SQLException {
        Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[]{KEY_ROWID, KEY_NOMBRE, KEY_ID_PRESENTACIONES, KEY_ORDENACION}, KEY_ROWID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Update the action block using the details provided. The action block to be updated is
     * specified using the rowId, and it is altered to use the name
     * value passed in
     *
     * @param rowId       id of block action to update
     * @param nombre      value to set block action title to update
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateBloqueAcciones(long rowId, String nombre) {
        boolean result = true;
        try {
            if (rowId <= -1) { result = false; }
            else if (nombre == null || nombre.length() <= 0 ) { result = false; }
        } catch (Exception e) {
            Log.w(DATABASE_TABLE, e.getStackTrace().toString());
        }

        if (result) {
            ContentValues args = new ContentValues();
            args.put(KEY_NOMBRE, nombre);

            result = mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
        }

        return result;
    }

    public boolean updateOrdenacion(long rowId, long nuevaOrdenacion) {
        boolean result = true;
        mDb.beginTransaction();
        try {
            // Obtener el bloque de acciones a reordenar
            Cursor cursor = fetchBloqueAcciones(rowId);
            if (cursor == null || !cursor.moveToFirst()) {
                return false;
            }

            long idPresentaciones = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID_PRESENTACIONES));
            long ordenacionActual = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ORDENACION));
            cursor.close();

            // Actualizar la ordenación de los demás bloques
            if (ordenacionActual < nuevaOrdenacion) {
                mDb.execSQL("UPDATE " + DATABASE_TABLE + " SET " + KEY_ORDENACION + " = " + KEY_ORDENACION + " - 1" +
                        " WHERE " + KEY_ID_PRESENTACIONES + " = " + idPresentaciones +
                        " AND " + KEY_ORDENACION + " > " + ordenacionActual +
                        " AND " + KEY_ORDENACION + " <= " + nuevaOrdenacion);
            } else if (ordenacionActual > nuevaOrdenacion) {
                mDb.execSQL("UPDATE " + DATABASE_TABLE + " SET " + KEY_ORDENACION + " = " + KEY_ORDENACION + " + 1" +
                        " WHERE " + KEY_ID_PRESENTACIONES + " = " + idPresentaciones +
                        " AND " + KEY_ORDENACION + " < " + ordenacionActual +
                        " AND " + KEY_ORDENACION + " >= " + nuevaOrdenacion);
            }

            // Actualizar la ordenación del bloque de acciones seleccionado
            ContentValues values = new ContentValues();
            values.put(KEY_ORDENACION, nuevaOrdenacion);
            result = mDb.update(DATABASE_TABLE, values, KEY_ROWID + " = ?", new String[]{String.valueOf(rowId)}) > 0;

            mDb.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(DATABASE_TABLE, "Error al actualizar la ordenación", e);
            result = false;
        } finally {
            mDb.endTransaction();
        }

        return result;
    }

    // Dado un id de bloque devuelve el nombe del bloque
    public String getNombreBloque(long idBloque) {
        String nombre = "";
        Cursor cursor = null;
        try {
            String query = "SELECT " + KEY_NOMBRE + " FROM " + DATABASE_TABLE +
                    " WHERE " + KEY_ROWID + " = " + idBloque;
            cursor = mDb.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                nombre = cursor.getString(0);
            }
        } catch (Exception e) {
            Log.e(DATABASE_TABLE, "Error al obtener el nombre del bloque de acciones", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return nombre;
    }

    // Dado una lista de ids de bloques actualiza la ordenación de los bloques
    public boolean updateOrdenacionBloques(ArrayList<Long> idsBloques, long idPresentaciones) {
        boolean result = true;
        mDb.beginTransaction();
        try {
            for (int i = 0; i < idsBloques.size(); i++) {
                long idBloque = idsBloques.get(i);
                ContentValues values = new ContentValues();
                values.put(KEY_ORDENACION, i + 1);
                result = mDb.update(DATABASE_TABLE, values, KEY_ROWID + " = ? AND " + KEY_ID_PRESENTACIONES + " = ?",
                        new String[]{String.valueOf(idBloque), String.valueOf(idPresentaciones)}) > 0;
                if (!result) {
                    break;
                }
            }
            mDb.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(DATABASE_TABLE, "Error al actualizar la ordenación de los bloques", e);
            result = false;
        } finally {
            mDb.endTransaction();
        }
        return result;
    }

    // Dato el id de una presentacion busca los bloques de esa presentación y de cada bloque busca las acciones y devuelve un arraylist tipo datamodel con los datos
    public ArrayList<DataModel> getDatosPresentacion(long idPresentacion, AccionesDbAdapter accionesDbAdapter) {
        ArrayList<DataModel> datos = new ArrayList<>();
        Cursor cursor = null;
        try {
            String query = "SELECT " + KEY_ROWID + " FROM " + DATABASE_TABLE +
                    " WHERE " + KEY_ID_PRESENTACIONES + " = " + idPresentacion +
                    " ORDER BY " + KEY_ORDENACION;
            cursor = mDb.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    long idBloque = cursor.getLong(0);
                    ArrayList<DataModel> listbloque = accionesDbAdapter.getAccionesBloque(idBloque);
                    if (listbloque.size() > 0) {
                        // Añadimos a la lista de datos las acciones del listblque
                        datos.addAll(listbloque);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(DATABASE_TABLE, "Error al obtener los datos de la presentación", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return datos;
    }





}