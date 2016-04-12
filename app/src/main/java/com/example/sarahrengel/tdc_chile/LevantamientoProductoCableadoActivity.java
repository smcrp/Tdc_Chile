package com.example.sarahrengel.tdc_chile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import BD_Levantamiento.RegistroSQLiteHelper;
import Connection.HttpClient;
import Connection.OnHttpRequestComplete;
import Connection.Response;
import Levantamiento.PHOTO;
import Levantamiento.Question;
import Levantamiento.Registro;


public class LevantamientoProductoCableadoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Registro registro;
    private JSONArray jsonArray = null;
    private JSONArray jsonQuest = null;
    private JSONObject jsono = null;
    private AdapterList adapter;
    private RegistroSQLiteHelper db;
    private ArrayList<Question> arrquest;
    public static final int REQUEST_CODE = 0;

    private EditText id;
    private EditText idqr;

    private static final String URL_PRODUCTO = "http://186.103.141.44/TorresUnidas.com.Api/index.php/api/Levantamiento/QuestionProduct";
    private String codeQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levantamiento_producto_cableado);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarRespuestas();
            }
        });

        ImageButton foto = (ImageButton) findViewById(R.id.photo);
        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        callZXing();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_levantamiento, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
      /*  if (id == R.id.action_settings) { return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {
            // Handle the camera action
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //HILO PARA CARGAR LOS DATOS EN EL LISTVIEW
    class CargarListTask extends AsyncTask<Response, Response, Boolean> {

        @Override
        protected Boolean doInBackground(Response... params) {
            try {
                jsono = new JSONObject(params[0].getResult());
                jsonArray = jsono.getJSONArray("result");

                for (int i = 0; i < jsonArray.length(); i++) {
                    registro = new Registro();
                    JSONObject c = jsonArray.getJSONObject(i);

                    registro.setId(c.getInt("id"));
                    registro.setName(c.getString("name"));

                    jsonQuest = c.getJSONArray("questions");
                    arrquest = new ArrayList<Question>();

                    Log.e("Entra en el array", jsonQuest.toString());

                    for (int j = 0; j < jsonQuest.length(); j++) {
                        Question question = new Question();
                        JSONObject l = jsonQuest.getJSONObject(j);
                        question.setId(l.getInt("id"));
                        question.setName(l.getString("name"));
                        question.setType(l.getString("type"));
                        question.setIdType(l.getInt("idtype"));
                        question.setLevel(2);
                        arrquest.add(question);
                    }
                    registro.setQuestions(arrquest);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean == true) {
                /*adapter = new AdapterList(LevantamientoProductoCableadoActivity.this, registro.getQuestions());
                listaQuestion.setAdapter(adapter);*/
                callZXing();
            } else {
                Log.e("Error", "ERROR de JSON");
            }
        }
    }

    private void guardarRespuestas(){
        db = new RegistroSQLiteHelper(getApplicationContext());

        Intent intent = getIntent();
        String id_registro = intent.getStringExtra("idRegistro");
        try {
            Log.d("************ID REGIS: ", id_registro);
            Log.d("************CODIGO QR: ", codeQR);

            TextView etiqueta_qr = (TextView) findViewById(R.id.etiqueta_qr);
            Question qr = new Question();
            qr.setLevel(2); //cableado
            qr.setId(1); //json
            qr.setIdType(1); //json
            qr.setType("QR");//json
            qr.setName(etiqueta_qr.getText().toString());//Json
            qr.setIdRegistro(Integer.parseInt(id_registro));//bd
            qr.setAnswer(codeQR);//vista
            db.guardarPregunta(qr);

            TextView etiqueta_modelo = (TextView) findViewById(R.id.etiqueta_modelo);
            EditText texto_modelo = (EditText) findViewById(R.id.texto_modelo);
            Log.d("************texto_modelo: ", texto_modelo.getText().toString());
            Question q1 = new Question();
            q1.setLevel(2); //cableado
            q1.setId(3); //json
            q1.setIdType(3); //json
            q1.setType("TEXT");//json
            q1.setName(etiqueta_modelo.getText().toString());//Json
            q1.setIdRegistro(Integer.parseInt(id_registro));//bd
            q1.setAnswer(texto_modelo.getText().toString());//vista
            db.guardarPregunta(q1);

            TextView etiqueta_marca = (TextView) findViewById(R.id.etiqueta_marca);
            EditText texto_marca = (EditText) findViewById(R.id.texto_marca);
            Log.d("************texto_marca: ", texto_marca.getText().toString());
            Question q2 = new Question();
            q2.setLevel(2); //cableado
            q2.setId(4); //json
            q2.setIdType(3); //json
            q2.setType("TEXT");//json
            q2.setName(etiqueta_marca.getText().toString());//Json
            q2.setIdRegistro(Integer.parseInt(id_registro));//bd
            q2.setAnswer(texto_marca.getText().toString());//vista
            db.guardarPregunta(q2);

            TextView etiqueta_tipo = (TextView) findViewById(R.id.etiqueta_tipo);
            EditText texto_tipo = (EditText) findViewById(R.id.texto_tipo);
            Log.d("************texto_tipo: ", texto_tipo.getText().toString());
            Question q3 = new Question();
            q3.setLevel(2); //cableado
            q3.setId(5); //json
            q3.setIdType(3); //json
            q3.setType("TEXT");//json
            q3.setName(etiqueta_tipo.getText().toString());//Json
            q3.setIdRegistro(Integer.parseInt(id_registro));//bd
            q3.setAnswer(texto_tipo.getText().toString());//vista
            db.guardarPregunta(q3);

            TextView etiqueta_tipoBanda = (TextView) findViewById(R.id.etiqueta_tipo_banda);
            EditText texto_tipo_banda = (EditText) findViewById(R.id.texto_tipo_banda);
            Log.d("************texto_tipo_banda: ", texto_tipo_banda.getText().toString());
            Question q4 = new Question();
            q4.setLevel(2); //cableado
            q4.setId(6); //json
            q4.setIdType(3); //json
            q4.setType("TEXT");//json
            q4.setName(etiqueta_tipoBanda.getText().toString());//Json
            q4.setIdRegistro(Integer.parseInt(id_registro));//bd
            q4.setAnswer(texto_tipo_banda.getText().toString());//vista
            db.guardarPregunta(q4);

            TextView etiqueta_banda = (TextView) findViewById(R.id.etiqueta_banda);
            EditText texto_banda = (EditText) findViewById(R.id.texto_banda);
            Log.d("************texto_banda: ", texto_banda.getText().toString());
            Question q5 = new Question();
            q5.setLevel(2); //cableado
            q5.setId(7); //json
            q5.setIdType(3); //json
            q5.setType("TEXT");//json
            q5.setName(etiqueta_banda.getText().toString());//Json
            q5.setIdRegistro(Integer.parseInt(id_registro));//bd
            q5.setAnswer(texto_banda.getText().toString());//vista
            db.guardarPregunta(q5);

            TextView etiqueta_frecuencia = (TextView) findViewById(R.id.etiqueta_frecuencia);
            EditText texto_frecuencia = (EditText) findViewById(R.id.texto_frecuencia);
            Log.d("************texto_frecuencia: ", texto_frecuencia.getText().toString());
            Question q6 = new Question();
            q6.setLevel(2); //cableado
            q6.setId(8); //json
            q6.setIdType(3); //json
            q6.setType("TEXT");//json
            q6.setName(etiqueta_frecuencia.getText().toString());//Json
            q6.setIdRegistro(Integer.parseInt(id_registro));//bd
            q6.setAnswer(texto_frecuencia.getText().toString());//vista
            db.guardarPregunta(q6);

            TextView etiqueta_electrical = (TextView) findViewById(R.id.etiqueta_eletrical_tilt);
            EditText texto_eletrical_tilt = (EditText) findViewById(R.id.texto_eletrical_tilt);
            Log.d("************texto_eletrical_tilt: ", texto_eletrical_tilt.getText().toString());
            Question q7 = new Question();
            q7.setLevel(2); //cableado
            q7.setId(12); //json
            q7.setIdType(3); //json
            q7.setType("TEXT");//json
            q7.setName(etiqueta_electrical.getText().toString());//Json
            q7.setIdRegistro(Integer.parseInt(id_registro));//bd
            q7.setAnswer(texto_eletrical_tilt.getText().toString());//vista
            db.guardarPregunta(q7);

            TextView etiqueta_luzH = (TextView) findViewById(R.id.etiqueta_luz_horizontal);
            EditText texto_luz_horizontal = (EditText) findViewById(R.id.texto_luz_horizontal);
            Log.d("************texto_luz_horizontal: ", texto_luz_horizontal.getText().toString());
            Question q8 = new Question();
            q8.setLevel(2); //cableado
            q8.setId(9); //json
            q8.setIdType(4); //json
            q8.setType("NUM");//json
            q8.setName(etiqueta_luzH.getText().toString());//Json
            q8.setIdRegistro(Integer.parseInt(id_registro));//bd
            q8.setAnswer(texto_luz_horizontal.getText().toString());//vista
            db.guardarPregunta(q8);

            TextView etiqueta_luzV = (TextView) findViewById(R.id.etiqueta_luz_vertical);
            EditText texto_luz_vertical = (EditText) findViewById(R.id.texto_luz_vertical);
            Log.d("************texto_luz_vertical: ", texto_luz_vertical.getText().toString());
            Question q9 = new Question();
            q9.setLevel(2); //cableado
            q9.setId(10); //json
            q9.setIdType(4); //json
            q9.setType("NUM");//json
            q9.setName(etiqueta_luzV.getText().toString());//Json
            q9.setIdRegistro(Integer.parseInt(id_registro));//bd
            q9.setAnswer(texto_luz_vertical.getText().toString());//vista
            db.guardarPregunta(q9);

            TextView etiqueta_ganancia = (TextView) findViewById(R.id.etiqueta_ganancia);
            EditText texto_ganancia = (EditText) findViewById(R.id.texto_ganancia);
            Log.d("************texto_ganancia: ", texto_ganancia.getText().toString());
            Question q10 = new Question();
            q10.setLevel(2); //cableado
            q10.setId(11); //json
            q10.setIdType(4); //json
            q10.setType("NUM");//json
            q10.setName(etiqueta_ganancia.getText().toString());//Json
            q10.setIdRegistro(Integer.parseInt(id_registro));//bd
            q10.setAnswer(texto_ganancia.getText().toString());//vista
            db.guardarPregunta(q10);

            TextView etiqueta_instalacion = (TextView) findViewById(R.id.etiqueta_altura_instalacion);
            EditText texto_altura_instalacion = (EditText) findViewById(R.id.texto_altura_instalacion);
            Log.d("************texto_altura_instalacion: ", texto_altura_instalacion.getText().toString());
            Question q11 = new Question();
            q11.setLevel(2); //cableado
            q11.setId(13); //json
            q11.setIdType(4); //json
            q11.setType("NUM");//json
            q11.setName(etiqueta_instalacion.getText().toString());//Json
            q11.setIdRegistro(Integer.parseInt(id_registro));//bd
            q11.setAnswer(texto_altura_instalacion.getText().toString());//vista
            db.guardarPregunta(q11);

            TextView etiqueta_largo = (TextView) findViewById(R.id.etiqueta_largo);
            EditText texto_largo = (EditText) findViewById(R.id.texto_largo);
            Log.d("************texto_largo: ", texto_largo.getText().toString());
            Question q12 = new Question();
            q12.setLevel(2); //cableado
            q12.setId(15); //json
            q12.setIdType(4); //json
            q12.setType("NUM");//json
            q12.setName(etiqueta_largo.getText().toString());//Json
            q12.setIdRegistro(Integer.parseInt(id_registro));//bd
            q12.setAnswer(texto_largo.getText().toString());//vista
            db.guardarPregunta(q12);

            TextView etiqueta_alto = (TextView) findViewById(R.id.etiqueta_alto);
            EditText texto_alto = (EditText) findViewById(R.id.texto_alto);
            Log.d("************texto_alto: ", texto_alto.getText().toString());
            Question q13 = new Question();
            q13.setLevel(2); //cableado
            q13.setId(16); //json
            q13.setIdType(4); //json
            q13.setType("NUM");//json
            q13.setName(etiqueta_alto.getText().toString());//Json
            q13.setIdRegistro(Integer.parseInt(id_registro));//bd
            q13.setAnswer(texto_alto.getText().toString());//vista
            db.guardarPregunta(q13);

            TextView etiqueta_profundidad = (TextView) findViewById(R.id.etiqueta_profundidad);
            EditText texto_profundidad = (EditText) findViewById(R.id.texto_profundidad);
            Log.d("************texto_profundidad: ", texto_profundidad.getText().toString());
            Question q14 = new Question();
            q14.setLevel(2); //cableado
            q14.setId(17); //json
            q14.setIdType(4); //json
            q14.setType("NUM");//json
            q14.setName(etiqueta_profundidad.getText().toString());//Json
            q14.setIdRegistro(Integer.parseInt(id_registro));//bd
            q14.setAnswer(texto_profundidad.getText().toString());//vista
            db.guardarPregunta(q14);

            Intent intentMain = new Intent(LevantamientoProductoCableadoActivity.this, MainElementosActivity.class);
            intentMain.putExtra("idRegistro", id_registro);
            startActivity(intentMain);

        }catch (Exception e) {
            Log.e("Servicio Rest", "Error!", e);
        }

    }

    public void callZXing() {
        try {
            Intent it = new Intent(LevantamientoProductoCableadoActivity.this, com.google.zxing.client.android.CaptureActivity.class);
            Log.e("callZXing", "Se Inicio el Lector");
            startActivityForResult(it, REQUEST_CODE);
            Log.e("callZXing", "Obteniendo Parameteros!");
        } catch (Exception e) {
            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
            startActivity(marketIntent);
        }
    }

    private void selectImage() {

        final CharSequence[] options = {"Nueva Foto", "Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(LevantamientoProductoCableadoActivity.this);
        builder.setTitle("Agregar Foto");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Nueva Foto")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "img_" + timeStamp + ".jpg"); //ruta y nombre de la foto
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 2);
                } else if (options[item].equals("Cancelar")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE == requestCode && RESULT_OK == resultCode) {
            Log.d("RESULTADO: ", data.getStringExtra("SCAN_RESULT"));
            codeQR = data.getStringExtra("SCAN_RESULT");
            Log.d("QR",codeQR);
        } else if(2 == requestCode) {
            Log.d("FOTO","Evaluar foto");
          //  Log.d("RESULTADO: ", data.getStringExtra(MediaStore.EXTRA_OUTPUT));
        } else {
            Intent intent2 = getIntent();
            String activityAnterior = intent2.getStringExtra("activityAnterior");
            if (activityAnterior==null){
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            } else if (activityAnterior.equalsIgnoreCase("MainElementosActivity")){
                String id_registro = intent2.getStringExtra("idRegistro");
                Intent intent = new Intent(getBaseContext(), MainElementosActivity.class);
                intent.putExtra("idRegistro", id_registro);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }


        }
    }
}