package com.example.sanbotapp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
public class PresentacionesDbAdapter {

    public static final String KEY_NOMBRE = "nombre";
    public static final String KEY_ROWID = "_id";

    private static final String DATABASE_TABLE = "presentaciones";
    private final Context mCtx;

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    public PresentacionesDbAdapter(Context ctx) {
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
    public PresentacionesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new presentation using the name provided. If the presentation is
     * successfully created return the new rowId for that presentation, otherwise return
     * a -1 to indicate failure.
     *
     * @param nombre      the title of the note
     * @return rowId or -1 if failed
     */
    public long createPresentacion(String nombre) {
        long result = 0;
        try {
            if (nombre == null || nombre.length() <= 0 ) { result =  -1; }
        } catch (Exception e) {
            Log.w(DATABASE_TABLE, e.getStackTrace().toString());
        }

        if (result != -1) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_NOMBRE, nombre);
            result = mDb.insert(DATABASE_TABLE, null, initialValues);
        }

        return result;
    }

    /**
     * Delete the presentation with the given rowId
     *
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deletePresentacion(long rowId) {
        try {
            if (rowId < 1) { return false; }
        } catch (Exception e) {
            Log.w(DATABASE_TABLE, e.getStackTrace().toString());
        }

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean deleteAllPresentacion() {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + KEY_ROWID, null) > 0;
    }

    /**
     * Return a Cursor over the list of all presentations in the database
     *
     * @return Cursor over all notes
     */
    public Cursor fetchAllPresentaciones() {
        return mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_NOMBRE}, null, null, null, null, null);
    }


    /**
     * Return a Cursor positioned at the presentation that matches the given rowId
     *
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchPresentacion(long rowId) throws SQLException {
        Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[]{KEY_ROWID, KEY_NOMBRE}, KEY_ROWID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Update the presentation using the details provided. The presentation to be updated is
     * specified using the rowId, and it is altered to use the name
     * value passed in
     *
     * @param rowId       id of note to update
     * @param nombre      value to set note title to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateHabitacion(long rowId, String nombre) {
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
}