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


public class LevantamientoProductoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView listaQuestion;
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
        setContentView(R.layout.activity_levantamiento_producto);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listaQuestion = (ListView) findViewById(R.id.listview);
        listaQuestion.setItemsCanFocus(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leerRespuestas();
                /*db.guardarRegistro(registro);
                Intent intent = new Intent(getBaseContext(), MainElementosActivity.class);
                startActivity(intent);*/
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        HttpClient client = new HttpClient(new OnHttpRequestComplete() {
            @Override
            public void onComplete(Response status) {
                if (status.isSuccess()) {
                    //Gson gson = new GsonBuilder().create();
                    Log.e("onComplete", "Status: " + status.toString());
                    new CargarListTask().execute(status);
                }

            }
        });
        client.excecute(URL_PRODUCTO);
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
                adapter = new AdapterList(LevantamientoProductoActivity.this, registro.getQuestions());
                listaQuestion.setAdapter(adapter);
                callZXing();
            } else {
                Log.e("Error", "ERROR de JSON");
            }
        }
    }

    public class AdapterList extends BaseAdapter {

        private final Context _context;
        private ArrayList<Question> _listData;

        public AdapterList(Context context, ArrayList<Question> listData) {
            this._context = context;
            this._listData = listData;
        }

        @Override
        public int getCount() {
            return _listData.size();
        }

        @Override
        public Object getItem(int position) {
            return _listData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View view, ViewGroup parent) {

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.elements_list_levantamiento_productos, null);
            }
            Question objprop = (Question) getItem(position);

            TextView tvNombre = (TextView) view.findViewById(R.id.name);
            tvNombre.setText(objprop.getName().toString());
            tvNombre.setVisibility(View.VISIBLE);

            ImageButton photo = (ImageButton) view.findViewById(R.id.photo);
            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage();
                }
            });

            id = (EditText) view.findViewById(R.id.id);

            ImageButton qr = (ImageButton) view.findViewById(R.id.qr);
            /*qr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callZXing();
                }
            });*/

            id.setVisibility(View.GONE);
            photo.setVisibility(View.GONE);
            qr.setVisibility(View.GONE);

            if (objprop.getType().equalsIgnoreCase("PHOTO")) {
                photo.setVisibility(View.VISIBLE);
            } else if (objprop.getType().equalsIgnoreCase("QR")) {
                qr.setVisibility(View.VISIBLE);
            } else if (objprop.getType().equalsIgnoreCase("NUM")) {
                id.setVisibility(View.VISIBLE);
                id.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else if (objprop.getType().equalsIgnoreCase("TEXT")) {
                id.setVisibility(View.VISIBLE);
                id.setInputType(InputType.TYPE_CLASS_TEXT);
            }
            return view;
        }
    }

    private void leerRespuestas(){
        int count = listaQuestion.getChildCount();
        db = new RegistroSQLiteHelper(getApplicationContext());

        Intent intent = getIntent();
        String id_registro = intent.getStringExtra("idRegistro");
        try {
            Log.d("************ID REGIS: ", id_registro);
            Log.d("************CODIGO QR: ", codeQR);
            for (int i = 0; i < count; i++) {
                View row = (View) listaQuestion.getChildAt(i);
                TextView tvNombre = (TextView) row.findViewById(R.id.name);
                EditText editText = (EditText) row.findViewById(R.id.id);

                Log.d("**TITULO ****", tvNombre.getText().toString());
                Log.d("**EDIT", editText.getText().toString());

                Log.d("EDITREGIS", registro.getQuestions().get(i).getName());

                //registro.getQuestions().get(i).setAnswer(editText.getText().toString());
                //registro.getQuestions().get(i).setIdRegistro(id_registro);
            }
            //Log.d("REGISTRO", String.valueOf(registro.getId()));
            //Log.d("REGISTRO", String.valueOf(registro.getName()));

            /*for (int i = 0; i < registro.getQuestions().size(); i++){
                Log.d("RESP", String.valueOf(registro.getQuestions().get(i).getId()));
                Log.d("RESP", String.valueOf(registro.getQuestions().get(i).getIdRegistro()));
                Log.d("RESP", String.valueOf(registro.getQuestions().get(i).getAnswer()));
            }*/

        }catch (Exception e) {
            Log.e("Servicio Rest", "Error!", e);
        }

    }

    public void callZXing() {
        try {
            Intent it = new Intent(LevantamientoProductoActivity.this, com.google.zxing.client.android.CaptureActivity.class);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(LevantamientoProductoActivity.this);
        builder.setTitle("Agregar Foto");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Nueva Foto")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "img_" + timeStamp + ".jpg"); //ruta y nombre de la foto
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
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
        }else {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}