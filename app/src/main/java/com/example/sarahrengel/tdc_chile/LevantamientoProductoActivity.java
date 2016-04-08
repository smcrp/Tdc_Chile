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
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

    ArrayList<HashMap<String, String>> antenalist;
    List param;
    private JSONArray jsonArray = null;
    private JSONArray jsonQuest = null;
    private JSONObject jsono = null;
    AdapterList adapter;
    ArrayList<Question> arrquest;
    ArrayList<Registro> arrregs;
    private ImageView imageView;
    public static final int REQUEST_CODE = 0;

    EditText id;
    EditText idqr;

    private static final String URL_PRODUCTO = "http://186.103.141.44/TorresUnidas.com.Api/index.php/api/Levantamiento/QuestionProduct";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levantamiento_producto);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listaQuestion = (ListView) findViewById(R.id.listview);
        listaQuestion.setItemsCanFocus(true);
        new CargarListTask().execute();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

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
            param = new ArrayList();
            boolean resultado = true;
            arrregs = new ArrayList<Registro>();

            try {

                jsono = new JSONObject(params[0].getResult());
                jsonArray = jsono.getJSONArray("result");

                ArrayList<Question> arrayQuestions = new ArrayList<>();
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
                        Log.d("IDQ", l.getString("id"));
                        question.setId(l.getInt("id"));
                        question.setName(l.getString("name"));
                        question.setType(l.getString("type"));
                        question.setIdType(l.getInt("idtype"));

                        Log.d("Id type:::::", l.getString("name"));
                        arrquest.add(question);
                    }
                    registro.setQuestions(arrquest);
                    arrregs.add(registro);
                    resultado = true;
                        /*Question ant = gson.fromJson(antena, Question.class);
                        TextView t = new TextView(getBaseContext());
                        t.setText(ant.getNameAntena());
                        StackContent.addView(t);*/
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultado;
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
            } else {
                Log.e("Error", "ERROR de JSON");
            }
        }

    }

    public class AdapterList extends BaseAdapter {

        private final Context _context;
        LayoutInflater lInflater;
        private ArrayList<Question> _listData;

        public AdapterList(Context context, ArrayList<Question> listData) {
            this._context = context;
            this._listData = listData;
            // lInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            ViewHolder holder;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                holder = new ViewHolder();
                view = inflater.inflate(R.layout.elements_list_levantamiento_productos, null);
                holder.caption = (EditText) view.findViewById(R.id.id);
                view.setTag(holder);
            }

            Question objprop = (Question) getItem(position);

            TextView tvNombre = (TextView) view.findViewById(R.id.name);
            tvNombre.setText(objprop.getName().toString());
            tvNombre.setVisibility(View.VISIBLE);

            ImageButton photo = (ImageButton) view.findViewById(R.id.photo);
            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //aqui
                    selectImage();
                }
            });

            id = (EditText) view.findViewById(R.id.id);
            idqr = (EditText) view.findViewById(R.id.idQr);

            ImageButton qr = (ImageButton) view.findViewById(R.id.qr);
            qr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callZXing(v);
                }
            });

            id.setVisibility(View.GONE);
            idqr.setVisibility(View.GONE);
            photo.setVisibility(View.GONE);
            qr.setVisibility(View.GONE);

            if (objprop.getType().equalsIgnoreCase("PHOTO")) {
                photo.setVisibility(View.VISIBLE);
            } else if (objprop.getType().equalsIgnoreCase("QR")) {
                qr.setVisibility(View.VISIBLE);

            } else {
                id.setVisibility(View.VISIBLE);
            }

            return view;
        }

    }

    class ViewHolder {
        EditText caption;
    }

    class ListItem {
        String caption;
    }

    public void callZXing(View view) {

        Intent it = new Intent(LevantamientoProductoActivity.this, com.google.zxing.client.android.CaptureActivity.class);
        Log.e("callZXing", "Se Inicio el Lector");
        startActivityForResult(it, REQUEST_CODE);
        Log.e("callZXing", "Obteniendo Parameteros!");

    }

    private void selectImage() {

        final CharSequence[] options = {"Nueva Foto", "Cancelar"};

        AlertDialog.Builder builder = new AlertDialog.Builder(LevantamientoProductoActivity.this);
        builder.setTitle("Agregar Foto");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Nueva Foto")){

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "img_"+ timeStamp +".jpg"); //ruta y nombre de la foto
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    //pic = f;
                    startActivityForResult(intent, 1);

                }  else if (options[item].equals("Cancelar")) {

                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    public int numeroAleatorio() {
        return (int) (Math.random() * 40);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE == requestCode && RESULT_OK == resultCode) {
            Log.e("onActivityResult", "RESULTADO: " + data.getStringExtra("SCAN_RESULT"));
            //CodeQR = data.getStringExtra("SCAN_RESULT");
            idqr.setVisibility(View.VISIBLE);
            idqr.setText(data.getStringExtra("SCAN_RESULT"));
        }


    }



}