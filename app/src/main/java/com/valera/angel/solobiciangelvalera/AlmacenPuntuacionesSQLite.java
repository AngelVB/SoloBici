package com.valera.angel.solobiciangelvalera;

import java.util.Vector;


        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class AlmacenPuntuacionesSQLite extends SQLiteOpenHelper implements AlmacenPuntuaciones {

    String sqlCreate = "CREATE TABLE PUNTUACIONES (Puntos INTEGER, Nombre VARCHAR," +
            " Fecha LONG)";

    // Constructor vacio
    public AlmacenPuntuacionesSQLite(Context contexto, String nombre, CursorFactory factory,
                                     int version){
        super(contexto, "PUNTUACIONES", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva){

    }

    @Override
    public void guardarPuntuacion(int puntos, String nombre, long fecha) {

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO PUNTUACIONES VALUES(" + puntos + ", '" + nombre + "', " + fecha + ")");
        db.close();
    }

    @Override
    public Vector<String> listaPuntuaciones(int cantidad) {
        Vector <String> resultado = new Vector<String>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Puntos, Nombre, Fecha FROM PUNTUACIONES ORDER BY Puntos DESC LIMIT " +
                cantidad, null);

        // Recorro el cursor con los resultados de la consulta y los guardo en el Vector
        while (cursor.moveToNext()){
            resultado.add(cursor.getInt(0) + " " + cursor.getString(1) + " " + cursor.getLong(2));
        }
        // Cierro el cursor
        cursor.close();
        // Cierro la conexiÛn a a la base de datos
        db.close();

        // Devuelvo el vector con el resultado
        return resultado;
    }


}
