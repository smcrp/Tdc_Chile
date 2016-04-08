package BD_Levantamiento;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Levantamiento.Antena;
import Levantamiento.Registro;


/**
 * Created by Sarah Rengel on 31-03-2016.
 */
public class HistoricoSQLiteHelper extends SQLiteOpenHelper {
    private static final String LOG = HistoricoSQLiteHelper.class.getName();

    // Nombre de columnas compartidas
    private static final String ID = "id";
    private static final String ACTIVO = "activo";
    private static final String CREADO = "creado";

    // Antena TABLA - Nombre de Columnas
    private static final String NOMBRE = "nombre";
    private static final String DIRECCION = "direccion";
    private static final String EMPRESA = "empresa";
    private static final String IDENTIFICADOR = "idN";
    private static final String LATITUD = "latitud";
    private static final String LONGITUD = "longitud";
    private static final String EDIT = "name";


  // String Sql="CREATE TABLE tbl_Historico (id INTEGER, idN TEXT, name TEXT)";
    String CREA_TABLA = "CREATE TABLE "
            + "tbl_Historico" + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + IDENTIFICADOR + " TEXT," + EDIT + " TEXT" + ")";

    public HistoricoSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
       db.execSQL(CREA_TABLA);
        Log.e("SE CREA LA BD", "Creada!!!!!!");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

       db.execSQL("DROP TABLE IF EXISTS tbl_Historico");
        onCreate(db);

    }

    /**
     * Guardando Antena
     */
    public void guardarRegistro(Registro registro) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(IDENTIFICADOR, registro.getId());
        values.put(EDIT, registro.getName());
       // values.put(EMPRESA, antena.getEmpresa());
        //values.put(IDENTIFICADOR, antena.getIdentificador());
       // values.put(LATITUD, antena.getLatitud());
       // values.put(LONGITUD, antena.getLongitud());
        // INSERT Antena
        db.insert("tbl_Historico", null, values);
      //  db.execSQL("INSERT INTO tbl_Historico (id, idN, name) " + "VALUES (" + ID + ",'" + IDENTIFICADOR + "', '" + EDIT + "')");
    }


    /**
     * Obtenemos todos los registros
     * */
    public List<Registro> obtenerRegistro() {
        List<Registro> registros = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + "tbl_Historico";

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Registro registro = new Registro();
                //registro.setId(c.getString(c.getColumnIndex(IDENTIFICADOR)));
                registro.setName(c.getString(c.getColumnIndex(EDIT)));

                Log.d("VAlOR", c.getString(c.getColumnIndex(IDENTIFICADOR)));
                Log.d("VAlOR NAME", c.getString(c.getColumnIndex(EDIT)));

                registros.add(registro);
            } while (c.moveToNext());
        }

        return registros;
    }
}
