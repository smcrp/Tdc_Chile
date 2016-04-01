package com.example.sarahrengel.tdc_chile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Connection.HttpClient;
import Connection.OnHttpRequestComplete;
import Connection.Response;
import Levantamiento.Question;
import Levantamiento.Registro;


public class LevantamientoProductoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView listaQuestion;
    private Registro registro;

    ArrayList<HashMap<String,String>> antenalist;
    List param;
    private JSONArray jsonArray = null;
    private JSONArray jsonQuest = null;
    private JSONObject jsono = null;
    AdapterList adapter;
    ArrayList<Question> arrquest;
    ArrayList<Registro> arrregs;
    public static final int REQUEST_CODE = 0;

    EditText id, id_Qr;

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
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
                    Log.e("onComplete","Status: "+status.toString());
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

                    registro.setId(c.getString("id"));
                    registro.setName(c.getString("name"));


                    jsonQuest = c.getJSONArray("questions");
                    arrquest = new ArrayList<Question>();

                    Log.e("Entra en el array", jsonQuest.toString());

                    for (int j = 0; j < jsonQuest.length(); j++) {
                        Question question = new Question();
                        JSONObject l = jsonQuest.getJSONObject(j);
                        Log.d("IDQ", l.getString("id"));
                        question.setId(l.getString("id"));
                        question.setName(l.getString("name"));
                        question.setType(l.getString("type"));
                        question.setIdType(l.getString("idtype"));

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

            if (aBoolean == true){
                adapter = new AdapterList(LevantamientoProductoActivity.this, registro.getQuestions());
                listaQuestion.setAdapter(adapter);

            }else
            {
                Log.e("Error","ERROR de JSON");
            }

        }

    }

    public class AdapterList extends BaseAdapter {

        private final Context _context;
        LayoutInflater lInflater;
        private ArrayList<Question>_listData;

        public AdapterList(Context context, ArrayList<Question> listData) {
            this._context=context;
            this._listData=listData;
            // lInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return _listData.size();
        }

        @Override
        public Object getItem(int position) { return _listData.get(position); }

        @Override
        public long getItemId(int position) { return position; }

        public View getView(int position,View view, ViewGroup parent){
            ViewHolder holder;
            if(view==null) {
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

            ImageView photo = (ImageView)view.findViewById(R.id.photo);
            id = (EditText) view.findViewById(R.id.id);
            id_Qr = (EditText) view.findViewById(R.id.idQr);

            ImageButton qr = (ImageButton) view.findViewById(R.id.qr);
            qr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callZXing(v);
                }
            });

            id.setVisibility(View.GONE);
            id_Qr.setVisibility(View.GONE);
            photo.setVisibility(View.GONE);
            qr.setVisibility(View.GONE);

            if (objprop.getType().equalsIgnoreCase("PHOTO")){
                photo.setVisibility(View.VISIBLE);
            }
            else if (objprop.getType().equalsIgnoreCase("QR")){
                qr.setVisibility(View.VISIBLE);
                id_Qr.setVisibility(View.VISIBLE);
            }
            else {
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

    public void callZXing(View view){
        Log.e("callZXing", "Iniciando el Lector.......");
        Intent it = new Intent(LevantamientoProductoActivity.this, com.google.zxing.client.android.CaptureActivity.class);
        Log.e("callZXing", "Se Inicio el Lector");
        startActivityForResult(it, REQUEST_CODE);
        Log.e("callZXing", "Obteniendo Parameteros!");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(REQUEST_CODE == requestCode && RESULT_OK == resultCode){
            Log.e("onActivityResult", "RESULTADO: " + data.getStringExtra("SCAN_RESULT"));
            //CodeQR = data.getStringExtra("SCAN_RESULT");
               id_Qr.setText(data.getStringExtra("SCAN_RESULT"));
        }

    }
}