package BD_Levantamiento;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Sarah Rengel on 31-03-2016.
 */
public class Historico extends SQLiteOpenHelper {

   String Sql="CREATE TABLE tbl_Historico (id TEXT, name TEXT )";
    public Historico(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("base", "creada");
        db.execSQL(Sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

       db.execSQL("DROP TABLE IF EXISTS tbl_Historico");
        db.execSQL(Sql);

    }

}
