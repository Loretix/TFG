package com.example.sanbotapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 *
 */
class DatabaseHelper extends SQLiteOpenHelper {

    private static final int    DATABASE_VERSION = 3;
    private static final String DATABASE_NAME    = "data";
    private static final String TAG              = "DatabaseHelper";

    /**
     * Database creation sql statement
     */
    private static final String ACCIONES = "create table acciones ("
            + "_id                  integer primary key autoincrement,"
            + "tipoFuncionalidad    text    not null,"
            + "tipoConfiguracion    text    not null,"
            + "imagen               text    ,"
            + "ordenacion           integer not null,"
            + "idBloquesAcciones    integer not null,"
            + "foreign key(idBloquesAcciones) references bloques_acciones(_id) on delete cascade"
            + "); ";

    private static final String BLOQUES_ACCIONES = "create table bloques_acciones ("
            + "_id           integer primary key autoincrement,"
            + "ordenacion           integer not null,"
            + "nombre        text    not null,"
            + "idPresentaciones       integer not null,"
            + "foreign key(idPresentaciones) references presentaciones(_id) on delete cascade"
            + "); ";

    private static final String PRESENTACIONES = "create table presentaciones ("
            + "_id           integer primary key autoincrement,"
            + "nombre        text not null,"
            + "movnatural    boolean not null,"
            + "ruido         boolean not null,"
            + "localizacion  boolean not null,"
            + "personas      boolean not null,"
            + "facial        boolean not null"
            + "); ";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ACCIONES);
        db.execSQL(BLOQUES_ACCIONES);
        db.execSQL(PRESENTACIONES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");

        if (oldVersion < 3) {
            // Añadir columnas booleanas con valor por defecto (0 = false, 1 = true)
            db.execSQL("ALTER TABLE presentaciones ADD COLUMN movnatural INTEGER NOT NULL DEFAULT 0");
            db.execSQL("ALTER TABLE presentaciones ADD COLUMN ruido INTEGER NOT NULL DEFAULT 0");
            db.execSQL("ALTER TABLE presentaciones ADD COLUMN localizacion INTEGER NOT NULL DEFAULT 0");
            db.execSQL("ALTER TABLE presentaciones ADD COLUMN personas INTEGER NOT NULL DEFAULT 0");
            db.execSQL("ALTER TABLE presentaciones ADD COLUMN facial INTEGER NOT NULL DEFAULT 0");
        } else{
            db.execSQL("DROP TABLE IF EXISTS acciones");
            db.execSQL("DROP TABLE IF EXISTS bloques_acciones");
            db.execSQL("DROP TABLE IF EXISTS presentaciones");
            onCreate(db);
        }
    }

}
