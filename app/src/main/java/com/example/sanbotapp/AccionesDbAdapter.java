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
public class AccionesDbAdapter {

    public static final String KEY_FUNCIONALIDAD = "tipoFuncionalidad";
    public static final String KEY_CONFIGURACION = "tipoConfiguracion";
    public static final String KEY_ORDENACION = "ordenacion";
    public static final String KEY_ID_BLOQUES= "idBloquesAcciones";
    public static final String KEY_ROWID = "_id";

    private static final String DATABASE_TABLE = "acciones";
    private final Context mCtx;

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    public AccionesDbAdapter(Context ctx) {
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
    public AccionesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new action using the name, presentation id and order provided. If the action is
     * successfully created return the new rowId for that action, otherwise return
     * a -1 to indicate failure.
     *
     * @param tipoFuncionalidad      the type of functionality
     * @param tipoConfiguracion      the type of configuration
     * @param idBloquesAcciones      the id of the action block
     * @return rowId or -1 if failed
     */

    public long createAcciones(String tipoFuncionalidad, String tipoConfiguracion, long idBloquesAcciones) {
        long result = -1;
        try {
            if(tipoFuncionalidad == null || tipoFuncionalidad.length() <= 0) { return -1; }
            if(tipoConfiguracion == null || tipoConfiguracion.length() <= 0) { return -1; }
            if(idBloquesAcciones <= -1) { return -1; }

            // Obtener el número de ordenación más alto actualmente en la base de datos para esta acción
            long maxOrdenacion = getMaxOrdenacion(idBloquesAcciones);

            // Incrementar el número de ordenación en uno para el nuevo bloque
            long ordenacion = maxOrdenacion + 1;

            // Crear la nueva accion
            result = createAccionesAux(tipoFuncionalidad, tipoConfiguracion, idBloquesAcciones, ordenacion);
        } catch (Exception e) {
            Log.e(DATABASE_TABLE, "Error al crear el bloque de acciones", e);
        }
        return result;
    }

    public long createAccionesAux(String tipoFuncionalidad, String tipoConfiguracion, long idBloquesAcciones, long ordenacion) {
        long result = 0;
        try {
            if (tipoFuncionalidad == null || tipoFuncionalidad.length() <= 0 ) { result =  -1; }
            else if (tipoConfiguracion == null || tipoConfiguracion.length() <= 0) { return -1; }
            else if (idBloquesAcciones <= -1) { return -1; }
            else if (ordenacion <= 0) { return -1; }
        } catch (Exception e) {
            Log.w(DATABASE_TABLE, e.getStackTrace().toString());
        }

        if (result != -1) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_FUNCIONALIDAD, tipoFuncionalidad);
            initialValues.put(KEY_CONFIGURACION, tipoConfiguracion);
            initialValues.put(KEY_ID_BLOQUES, idBloquesAcciones);
            initialValues.put(KEY_ORDENACION, ordenacion);
            result = mDb.insert(DATABASE_TABLE, null, initialValues);
        }

        return result;
    }

    private long getMaxOrdenacion(long idBloquesAcciones) {
        long maxOrdenacion = 0;
        Cursor cursor = null;
        try {
            String query = "SELECT MAX(" + KEY_ORDENACION + ") FROM " + DATABASE_TABLE +
                    " WHERE " + KEY_ID_BLOQUES + " = " + idBloquesAcciones;
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
    public boolean deleteAcciones(long rowId) {
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
            Log.e(DATABASE_TABLE, "Error al obtener la ordenación de acciones", e);
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

    public boolean deleteAllAcciones(long idBloquesAcciones) {
        return mDb.delete(DATABASE_TABLE, KEY_ID_BLOQUES + "=" + idBloquesAcciones, null) > 0;
    }

    /**
     * Return a Cursor over the list of all presentations in the database
     *
     * @return Cursor over all notes
     */
    public Cursor fetchAllAcciones(long idBloquesAcciones) {
        return mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_FUNCIONALIDAD, KEY_CONFIGURACION, KEY_ID_BLOQUES, KEY_ORDENACION}, KEY_ID_BLOQUES+ "=" + idBloquesAcciones, null, null, null, KEY_ORDENACION);
    }


    /**
     * Return a Cursor positioned at the presentation that matches the given rowId
     *
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchAcciones(long rowId) throws SQLException {
        Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[]{KEY_ROWID, KEY_FUNCIONALIDAD, KEY_CONFIGURACION, KEY_ID_BLOQUES, KEY_ORDENACION}, KEY_ROWID + "=" + rowId, null, null, null, null, null);
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
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateAcciones(long rowId, String tipoConfiguracion, String tipoFuncionalidad) {
        boolean result = true;
        try {
            if (rowId <= -1) { result = false; }
            else if (tipoConfiguracion == null || tipoConfiguracion.isEmpty()) { result = false; }
            else if (tipoFuncionalidad == null || tipoFuncionalidad.isEmpty()) { result = false; }
        } catch (Exception e) {
            Log.w(DATABASE_TABLE, e.getStackTrace().toString());
        }

        if (result) {
            ContentValues args = new ContentValues();
            args.put(KEY_CONFIGURACION, tipoConfiguracion);
            args.put(KEY_FUNCIONALIDAD, tipoFuncionalidad);

            result = mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
        }

        return result;
    }

    public boolean updateOrdenacion(long rowId, long nuevaOrdenacion) {
        boolean result = true;
        mDb.beginTransaction();
        try {
            // Obtener el bloque de acciones a reordenar
            Cursor cursor = fetchAcciones(rowId);
            if (cursor == null || !cursor.moveToFirst()) {
                return false;
            }

            long idBloqueAcciones = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID_BLOQUES));
            long ordenacionActual = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ORDENACION));
            cursor.close();

            // Actualizar la ordenación de los demás bloques
            if (ordenacionActual < nuevaOrdenacion) {
                mDb.execSQL("UPDATE " + DATABASE_TABLE + " SET " + KEY_ORDENACION + " = " + KEY_ORDENACION + " - 1" +
                        " WHERE " + KEY_ID_BLOQUES + " = " + idBloqueAcciones +
                        " AND " + KEY_ORDENACION + " > " + ordenacionActual +
                        " AND " + KEY_ORDENACION + " <= " + nuevaOrdenacion);
            } else if (ordenacionActual > nuevaOrdenacion) {
                mDb.execSQL("UPDATE " + DATABASE_TABLE + " SET " + KEY_ORDENACION + " = " + KEY_ORDENACION + " + 1" +
                        " WHERE " + KEY_ID_BLOQUES + " = " + idBloqueAcciones +
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

    // Dato el id de un bloque de acciones devuelve un arraylist de tipo datamodel con las acciones de ese bloque
    public ArrayList<DataModel> getAccionesBloque(long idBloque) {
        ArrayList<DataModel> acciones = new ArrayList<DataModel>();
        Cursor cursor = null;
        try {
            String query = "SELECT " + AccionesDbAdapter.KEY_CONFIGURACION + ", " + AccionesDbAdapter.KEY_FUNCIONALIDAD +
                    " FROM " + AccionesDbAdapter.DATABASE_TABLE +
                    " WHERE " + AccionesDbAdapter.KEY_ID_BLOQUES + " = " + idBloque +
                    " ORDER BY " + AccionesDbAdapter.KEY_ORDENACION;
            cursor = mDb.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    DataModel dataModel = new DataModel(cursor.getString(0), cursor.getString(1));
                    acciones.add(dataModel);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(DATABASE_TABLE, "Error al obtener las acciones del bloque de acciones", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return acciones;
    }



}