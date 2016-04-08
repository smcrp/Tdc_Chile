package BD_Levantamiento;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Levantamiento.Question;
import Levantamiento.Registro;

/**
 * Created by georgeperez on 4/4/16.
 */
public class RegistroSQLiteHelper extends SQLiteOpenHelper {

    // LOG
    private static final String LOG = RegistroSQLiteHelper.class.getName();

    // VERSION BASE DE DATOS
    private static final int DATABASE_VERSION = 1;

    // NOMBRE DE BASE DE DATOS
    private static final String DATABASE_NAME = "TDCCHILE";

    // TABLAS
    private static final String TABLE_REGISTRO = "Registro";
    private static final String TABLE_PREGUNTA = "Pregunta";

    // NOMBRE COLUMNAS COMPARTIDAS
    private static final String ID = "id";
    private static final String ID_JSON = "id_json";
    private static final String NAME = "name";

    //TABLA REGISTRO
    private static final String STATUS = "status";
    private static final String CREATED = "created";

    //TABLA PREGUNTA
    private static final String ID_REGISTRO = "id_registro";
    private static final String ID_TYPE = "id_type";
    private static final String TYPE = "type";
    private static final String LEVEL = "level";
    private static final String ANSWER = "answer";

    // CREAMOS TABLA REGISTRO
    private static final String CREA_TABLA_REGISTRO = "CREATE TABLE "
            + TABLE_REGISTRO + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ID_JSON + " INTEGER, " + NAME + " TEXT, "
            + STATUS + " INTEGER, " + CREATED + " DATETIME" + ")";

    // CREAMOS TABLA PREGUNTA
    private static final String CREA_TABLA_PREGUNTA = "CREATE TABLE "
            + TABLE_PREGUNTA + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ID_JSON + " INTEGER, " + ID_REGISTRO + " INTEGER, " + ID_TYPE + " INTEGER, "
            + NAME + " TEXT, " + TYPE + " TEXT, " + LEVEL + " INTEGER, "
            + ANSWER + " TEXT " + ")";

    public RegistroSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Se ejecuta la sentencia SQL de creación de la tabla
        db.execSQL(CREA_TABLA_REGISTRO);
        db.execSQL(CREA_TABLA_PREGUNTA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGISTRO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREGUNTA);
        // create new tables
        onCreate(db);
    }

    /**
     * GUARDANDO REGISTRO
     */
    public void guardarRegistro(Registro registro) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID_JSON, registro.getId());
        values.put(NAME, registro.getName());
        values.put(STATUS, 1);
        values.put(CREATED, getDateTime());

        // INSERT REGISTRO
        db.insert(TABLE_REGISTRO, null, values);

        for (Question preg: registro.getQuestions()) {
            guardarPregunta(preg);
        }
    }

    /**
     * GUARDANDO PREGUNTA
     */
    public void guardarPregunta(Question pregunta) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID_JSON, pregunta.getId());
        values.put(ID_REGISTRO, pregunta.getIdRegistro());
        values.put(ID_TYPE, pregunta.getIdType());
        values.put(NAME, pregunta.getName());
        values.put(TYPE, pregunta.getIdType());
        values.put(LEVEL, pregunta.getLevel());
        values.put(ANSWER, pregunta.getAnswer());

        // INSERT REGISTRO
        db.insert(TABLE_PREGUNTA, null, values);
    }

    /**
     * OBTENEMOS EL ULTIMO ID
     */
    public int obtenerUltIdRegistro() {
        int max=0;

        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT id FROM " + TABLE_REGISTRO
                + " WHERE id = (SELECT MAX(id) FROM " + TABLE_REGISTRO + ")";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null){
            if(cursor.moveToFirst())
            {
                max= cursor.getInt(0);
            }
        }
        return max+1;
    }

    /**
     * OBTENEMOS REGISTRO POR ID
     */
    public Registro obtenerRegistro(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_REGISTRO + " WHERE "
                + ID + " = " + id;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        Registro registro = new Registro();
        registro.setId(c.getInt(c.getColumnIndex(ID)));
        registro.setIdJson(c.getInt(c.getColumnIndex(ID_JSON)));
        registro.setName(c.getString(c.getColumnIndex(NAME)));
        registro.setStatus(c.getInt(c.getColumnIndex(STATUS)));
        registro.setCreate(c.getString(c.getColumnIndex(CREATED)));

        return registro;
    }


    /**
     * OBTENEMOS TODOS LOS REGISTROS
     * */
    public List<Registro> obtenerRegistros() {
        List<Registro> registros = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_REGISTRO;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Registro registro = new Registro();
                registro.setId(c.getInt(c.getColumnIndex(ID)));
                registro.setIdJson(c.getInt(c.getColumnIndex(ID_JSON)));
                registro.setName(c.getString(c.getColumnIndex(NAME)));
                registro.setStatus(c.getInt(c.getColumnIndex(STATUS)));
                registro.setCreate(c.getString(c.getColumnIndex(CREATED)));

                registros.add(registro);
            } while (c.moveToNext());
        }

        return registros;
    }

    /**
     * Obtenemos Nombres de las antenas
     * */
    public List<Question> obtenerNombreAntena() {
        List<Question> preguntas = new ArrayList<>();
        String selectQuery = "SELECT " + ANSWER + " FROM " + TABLE_PREGUNTA
                + " WHERE " + LEVEL + " = 1 AND " + ID_JSON + " = 20 ";

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Question pregunta = new Question();
                pregunta.setAnswer(c.getString(c.getColumnIndex(ANSWER)));
                preguntas.add(pregunta);
            } while (c.moveToNext());
        }
        return preguntas;
    }



    /**
     * Actualizamos registro de Antena
     */
//    public int actualizarAntena(Antena antena) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(NOMBRE, antena.getNombre());
//        values.put(DIRECCION, antena.getDireccion());
//        values.put(EMPRESA, antena.getEmpresa());
//        values.put(IDENTIFICADOR, antena.getIdentificador());
//        values.put(LATITUD, antena.getLatitud());
//        values.put(LONGITUD, antena.getLongitud());
//        values.put(ACTIVO, antena.getActivo());
//
//        // actualizando registro
//        return db.update(TABLE_ANTENA, values, ID + " = ?",
//                new String[] { String.valueOf(antena.getId()) });
//    }

    // Cerramos BD
    public void cerrarBD() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    /**
     * get datetime
     * */
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
