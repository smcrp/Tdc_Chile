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

import Levantamiento.Products;
import Levantamiento.Question;
import Levantamiento.Registro;
import Levantamiento.RegistroJson;

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
    private static final String TABLE_PRODUCTO = "Producto";
    private static final String TABLE_PREGUNTA = "Pregunta";

    // NOMBRE COLUMNAS COMPARTIDAS
    private static final String ID = "id";
    private static final String ID_JSON = "id_json";
    private static final String NAME = "name";

    //TABLA REGISTRO
    private static final String STATUS = "status";
    private static final String CREATED = "created";

    //TABLA PRODUCTO
    private static final String ID_PRODUCT = "idproduct";

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

    // CREAMOS TABLA REGISTRO DE PRODUCTO
    private static final String CREA_TABLA_PRODUCTO = "CREATE TABLE "
            + TABLE_PRODUCTO + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ID_REGISTRO + " INTEGER, " + ID_PRODUCT + " TEXT " + ")";

    // CREAMOS TABLA PREGUNTA
    private static final String CREA_TABLA_PREGUNTA = "CREATE TABLE "
            + TABLE_PREGUNTA + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ID_JSON + " INTEGER, " + ID_REGISTRO + " INTEGER, " + ID_TYPE + " INTEGER, "
            + NAME + " TEXT, " + TYPE + " TEXT, " + LEVEL + " INTEGER, " + ID_PRODUCT + " TEXT, "
            + ANSWER + " TEXT, " + " FOREIGN KEY(" + ID_REGISTRO + ") REFERENCES "
            + TABLE_PREGUNTA + "("+ ID +"))";

    public RegistroSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Se ejecuta la sentencia SQL de creación de la tabla
        db.execSQL(CREA_TABLA_REGISTRO);
        db.execSQL(CREA_TABLA_PREGUNTA);
        db.execSQL(CREA_TABLA_PRODUCTO);
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
        cerrarBD();
    }

    /**
     * GUARDANDO PRODUCTO
     */
    public void guardarProducto(String id_producto, int id_registro) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID_REGISTRO, id_registro);
        values.put(ID_PRODUCT, id_producto);

        // INSERT REGISTRO
        db.insert(TABLE_PRODUCTO, null, values);

        cerrarBD();
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
        values.put(TYPE, pregunta.getType());
        values.put(LEVEL, pregunta.getLevel());
        values.put(ID_PRODUCT, pregunta.getIdQr());
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
        return max;
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
     * OBTENEMOS REGISTRO POR ID
     */
    public RegistroJson obtenerRegistroJson(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT " + ID_JSON + "," + NAME + " FROM " + TABLE_REGISTRO + " WHERE "
                + ID + " = " + id;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();

        RegistroJson registroJson = new RegistroJson(c.getInt(c.getColumnIndex(ID_JSON)),c.getString(c.getColumnIndex(NAME)));
        //registro.setId(c.getInt(c.getColumnIndex(ID)));
        //registro.setStatus(c.getInt(c.getColumnIndex(STATUS)));
        //registro.setCreate(c.getString(c.getColumnIndex(CREATED)));

        /*ArrayList<Question> preguntas = new ArrayList<>();
        String selectQueryPreg = "SELECT * FROM " + TABLE_PREGUNTA
                + " WHERE " + ID_REGISTRO + " = " + id;

        Log.e(LOG, selectQueryPreg);

        Cursor c2 = db.rawQuery(selectQueryPreg, null);

        if (c2.moveToFirst()) {
            do {
                Question pregunta = new Question();
                pregunta.setId(c2.getInt(c2.getColumnIndex(ID_JSON)));
                pregunta.setAnswer(c2.getString(c2.getColumnIndex(ANSWER)));
                preguntas.add(pregunta);
            } while (c2.moveToNext());
            registro.setQuestions(preguntas);
        }*/
        return registroJson;
    }

    /**
     * OBTENEMOS PRODUCTOS POR ID
     */
    public RegistroJson obtenerProductosRegistroJson(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT " + ID_PRODUCT + " FROM " + TABLE_PRODUCTO + " WHERE "
                + ID + " = " + id;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();

        RegistroJson registroJson = new RegistroJson(c.getInt(c.getColumnIndex(ID_JSON)),c.getString(c.getColumnIndex(NAME)));
        //registro.setId(c.getInt(c.getColumnIndex(ID)));
        //registro.setStatus(c.getInt(c.getColumnIndex(STATUS)));
        //registro.setCreate(c.getString(c.getColumnIndex(CREATED)));

        /*ArrayList<Question> preguntas = new ArrayList<>();
        String selectQueryPreg = "SELECT * FROM " + TABLE_PREGUNTA
                + " WHERE " + ID_REGISTRO + " = " + id;

        Log.e(LOG, selectQueryPreg);

        Cursor c2 = db.rawQuery(selectQueryPreg, null);

        if (c2.moveToFirst()) {
            do {
                Question pregunta = new Question();
                pregunta.setId(c2.getInt(c2.getColumnIndex(ID_JSON)));
                pregunta.setAnswer(c2.getString(c2.getColumnIndex(ANSWER)));
                preguntas.add(pregunta);
            } while (c2.moveToNext());
            registro.setQuestions(preguntas);
        }*/
        return registroJson;
    }

    /**
     * OBTENEMOS PREGUNTAS POR ID
     */
    public ArrayList<Question> obtenerPreguntaJson(long idRegistro, int lvl) {
        ArrayList<Question> preguntas = new ArrayList<>();
        String selectQuery = "SELECT " + TABLE_PREGUNTA + "." + ID_JSON + ","
                + TABLE_PREGUNTA + "." + ANSWER + " FROM " + TABLE_REGISTRO
                + " INNER JOIN " + TABLE_PREGUNTA  + " ON "
                + TABLE_REGISTRO + "." + ID + " = "
                + TABLE_PREGUNTA + "." + ID_REGISTRO + " WHERE "
                + TABLE_PREGUNTA + "." + LEVEL + " = " + lvl + " AND "
                + TABLE_PREGUNTA + "." + ID_REGISTRO + " =  " +idRegistro + " AND "
                + TABLE_REGISTRO + "." + STATUS + " = 1 ";

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Question pregunta = new Question();
                pregunta.setId(c.getInt(c.getColumnIndex(ID_JSON)));
                pregunta.setAnswer(c.getString(c.getColumnIndex(ANSWER)));
                preguntas.add(pregunta);
            } while (c.moveToNext());
        }
        return preguntas;
    }

    /**
     * OBTENEMOS PREGUNTAS POR ID
     */
    public ArrayList<Question> obtenerPreguntaQrJson(long idRegistro, String codeQr) {
        ArrayList<Question> preguntas = new ArrayList<>();
        String selectQuery = "SELECT " + TABLE_PREGUNTA + "." + ID_JSON + ","
                + TABLE_PREGUNTA + "." + ANSWER + ","
                + TABLE_PRODUCTO + "." + ID_PRODUCT
                + " FROM " + TABLE_REGISTRO
                + " INNER JOIN " + TABLE_PREGUNTA  + " ON "
                + TABLE_REGISTRO + "." + ID + " = "
                + TABLE_PREGUNTA + "." + ID_REGISTRO

                + " INNER JOIN " + TABLE_PRODUCTO  + " ON "
                + TABLE_PREGUNTA + "." + ID_PRODUCT + " = "
                + TABLE_PRODUCTO + "." + ID_PRODUCT

                + " WHERE "
                + TABLE_PREGUNTA + "." + LEVEL + " = 2 AND "
                //+ TABLE_PRODUCTO + "." + ID_PRODUCT + " = " + codeQr + " AND "
                + TABLE_PRODUCTO + "." + ID_REGISTRO + " = " + idRegistro + " AND "
                + TABLE_REGISTRO + "." + STATUS + " = 1 ";

        Log.e(LOG, selectQuery);


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Question pregunta = new Question();
                pregunta.setId(c.getInt(c.getColumnIndex(ID_JSON)));
                pregunta.setAnswer(c.getString(c.getColumnIndex(ANSWER)));
                pregunta.setIdQr(c.getString(c.getColumnIndex(ID_PRODUCT)));
                //Log.e("QR", c.getString(c.getColumnIndex(ID_PRODUCT)));
                preguntas.add(pregunta);
            } while (c.moveToNext());
        }
        return preguntas;
    }

    /**
     * OBTENEMOS PREGUNTAS POR ID
     */
    public ArrayList<Products> obtenerProductos(long idRegistro) {
        ArrayList<Products> productos = new ArrayList<>();
        String selectQuery = "SELECT " + ID_PRODUCT
                + " FROM " + TABLE_PRODUCTO;
                //+ " INNER JOIN " + TABLE_PRODUCTO  + " ON "
                //+ TABLE_REGISTRO + "." + ID + " = "
                //+ TABLE_PRODUCTO + "." + ID_REGISTRO
                //+ " WHERE "
                //+ TABLE_PRODUCTO + "."
                //+ ID_REGISTRO + " = " + idRegistro;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Products producto = new Products();
                producto.setId(c.getString(c.getColumnIndex(ID_PRODUCT)));
                producto.setQuestions(obtenerPreguntaQrJson(idRegistro,c.getString(c.getColumnIndex(ID_PRODUCT))));
                productos.add(producto);
            } while (c.moveToNext());
        }
        return productos;
    }
    /**
     * OBTENEMOS TODOS LOS REGISTROS
     * */
    public Registro obtenerRegistro() {

        String selectQuery = "SELECT * FROM " + TABLE_REGISTRO;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        Registro registro = new Registro();
        if (c.moveToFirst()) {
            do {
                registro.setId(c.getInt(c.getColumnIndex(ID)));
                registro.setIdJson(c.getInt(c.getColumnIndex(ID_JSON)));
                registro.setName(c.getString(c.getColumnIndex(NAME)));
                registro.setStatus(c.getInt(c.getColumnIndex(STATUS)));
                registro.setCreate(c.getString(c.getColumnIndex(CREATED)));
            } while (c.moveToNext());
        }

        return registro;
    }

    /**
     * Obtenemos Nombres de las antenas
     * */
    public List<Question> obtenerNombreAntenaViejo() {
        List<Question> preguntas = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PREGUNTA
                + " WHERE " + LEVEL + " = 1 AND " + ID_JSON + " = 20 ";

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Question pregunta = new Question();
                pregunta.setAnswer(c.getString(c.getColumnIndex(ANSWER)));
                pregunta.setIdRegistro(c.getInt(c.getColumnIndex(ID_REGISTRO)));
                preguntas.add(pregunta);
            } while (c.moveToNext());
        }
        return preguntas;
    }

    public List<Question> obtenerNombreAntena() {
        List<Question> preguntas = new ArrayList<>();
        String selectQuery = "SELECT " + TABLE_PREGUNTA + "." + ID_REGISTRO + ","
                + TABLE_PREGUNTA + "." + ANSWER + " FROM " + TABLE_REGISTRO
                + " INNER JOIN " + TABLE_PREGUNTA  + " ON "
                + TABLE_REGISTRO + "." + ID + " = "
                + TABLE_PREGUNTA + "." + ID_REGISTRO + " WHERE "
                + TABLE_PREGUNTA + "." + LEVEL + " = 1 AND "
                + TABLE_PREGUNTA + "." + ID_JSON + " = 20 AND "
                + TABLE_REGISTRO + "." + STATUS + " = 1 ";

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Question pregunta = new Question();
                pregunta.setAnswer(c.getString(c.getColumnIndex(ANSWER)));
                pregunta.setIdRegistro(c.getInt(c.getColumnIndex(ID_REGISTRO)));
                preguntas.add(pregunta);
            } while (c.moveToNext());
        }
        return preguntas;
    }

    /**
     * Obtenemos Nombres de los productos
     * */
    public List<Question> obtenerElementosdeTorreviejo() {
        List<Question> elementos = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PREGUNTA
                + " WHERE " + LEVEL + " = 2 AND " + ID_JSON + " = 3 ";

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Question elemento = new Question();
                elemento.setAnswer(c.getString(c.getColumnIndex(ANSWER)));
                elemento.setIdRegistro(c.getInt(c.getColumnIndex(ID_REGISTRO)));
                elementos.add(elemento);
            } while (c.moveToNext());
        }
        cerrarBD();
        return elementos;
    }

    public List<Question> obtenerElementosdeTorre(int idRegistro) {
        List<Question> preguntas = new ArrayList<>();
        String selectQuery = "SELECT " + TABLE_PREGUNTA + "." + ID + ","
                + TABLE_PREGUNTA + "." + ANSWER + " FROM " + TABLE_REGISTRO
                + " INNER JOIN " + TABLE_PREGUNTA  + " ON "
                + TABLE_REGISTRO + "." + ID + " = "
                + TABLE_PREGUNTA + "." + ID_REGISTRO + " WHERE "
                + TABLE_PREGUNTA + "." + LEVEL + " = 2 AND "
                + TABLE_PREGUNTA + "." + ID_JSON + " = 3 AND "
                + TABLE_PREGUNTA + "." + ID_REGISTRO + " =  " +idRegistro + " AND "
                + TABLE_REGISTRO + "." + STATUS + " = 1 ";

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Question pregunta = new Question();
                pregunta.setAnswer(c.getString(c.getColumnIndex(ANSWER)));
                pregunta.setId(c.getInt(c.getColumnIndex(ID)));
                preguntas.add(pregunta);
            } while (c.moveToNext());
        }
        return preguntas;
    }

    /**
     * Eliminar Registro
     */
    public void eliminarRegistro(Registro registro){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STATUS, 0);
        db.update(TABLE_REGISTRO, values, ID + " = ?",
                new String[]{String.valueOf(registro.getId())});
        cerrarBD();
    }

    /**
     * Eliminar Pregunta
     */
    public void eliminarPregunta(Question pregunta){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PREGUNTA, ID +" = " + pregunta.getId(), null);
        cerrarBD();
    }

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
