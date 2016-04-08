package com.example.sarahrengel.tdc_chile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import BD_Levantamiento.HistoricoSQLiteHelper;
import BD_Levantamiento.RegistroSQLiteHelper;
import Connection.HttpClient;
import Connection.OnHttpRequestComplete;
import Connection.Response;
import Levantamiento.Question;
import Levantamiento.Registro;


    public class LevantamientoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

        private ListView listaQuestion;

        private LinearLayout StackContent;
        private Registro registro;
        private RegistroSQLiteHelper db;
        private ArrayList<HashMap<String,String>> antenalist;
        private List param;
        private JSONArray jsonArray = null;
        private JSONArray jsonQuest = null;
        private JSONObject jsono = null;
        private AdapterList adapter;
        private ArrayList<Question> arrquest;

        private static final String URL_ANTENA = "http://186.103.141.44/TorresUnidas.com.Api/index.php/api/Levantamiento/questionAntenna";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_levantamiento);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            listaQuestion = (ListView) findViewById(R.id.listview);
            listaQuestion.setItemsCanFocus(true);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    leerRespuestas();
                    db.guardarRegistro(registro);
                    /*final Intent intent = new Intent(getBaseContext(), LevantamientoProductoActivity.class);
                    startActivity(intent);*/
                    final Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                }
            });

           /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
              this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();*/

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
            client.excecute(URL_ANTENA);

        }
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_levantamiento, menu);
            return true;
        }
        @Override
        public void onBackPressed() {
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {

            int id = item.getItemId();
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
            //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            //drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        private void leerRespuestas(){
            int count = listaQuestion.getChildCount();
            db = new RegistroSQLiteHelper(getApplicationContext());
            int id_registro = db.obtenerUltIdRegistro();
            try {
                for (int i = 0; i < count; i++) {
                    View row = (View) listaQuestion.getChildAt(i);
                    TextView tvNombre = (TextView) row.findViewById(R.id.name);
                    EditText editText = (EditText) row.findViewById(R.id.id);

                    String tNombre = tvNombre.getText().toString();
                    String eId = editText.getText().toString();

                    Log.d("Entra en el for", "Mensage");
                    Log.d("TEXT", tvNombre.getText().toString());
                    Log.d("EDIT", editText.getText().toString());
                    Log.d("EDITREGIS", registro.getQuestions().get(i).getName());

                    registro.getQuestions().get(i).setAnswer(editText.getText().toString());
                    registro.getQuestions().get(i).setIdRegistro(id_registro);
                }

                Log.d("REGISTRO", String.valueOf(registro.getId()));
                Log.d("REGISTRO", String.valueOf(registro.getName()));

                for (int i = 0; i < registro.getQuestions().size(); i++){
                    Log.d("RESP", String.valueOf(registro.getQuestions().get(i).getId()));
                    Log.d("RESP", String.valueOf(registro.getQuestions().get(i).getIdRegistro()));
                    Log.d("RESP", String.valueOf(registro.getQuestions().get(i).getAnswer()));
                }

            }catch (Exception e) {
                Log.e("Servicio Rest", "Error!", e);
            }

        }

        //HILO PARA CARGAR LOS DATOS EN EL LISTVIEW
        class CargarListTask extends AsyncTask<Response, Response, Boolean> {

            @Override
            protected Boolean doInBackground(Response... params) {
                param = new ArrayList();
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
                            question.setLevel(1);
                            Log.d("IDQ", l.getString("id"));
                            Log.d("NAME", l.getString("name"));
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

                if (aBoolean == true){
                    adapter = new AdapterList(LevantamientoActivity.this, registro.getQuestions());
                    listaQuestion.setAdapter(adapter);
                }else
                {
                    Log.e("Error","ERROR de JSON");
                }

            }

        }

        public class AdapterList extends BaseAdapter {

            private final Context _context;
            private ArrayList<Question>_listData;

            public AdapterList(Context context, ArrayList<Question> listData) {
                this._context=context;
                this._listData=listData;
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
                if(view==null) {
                    LayoutInflater inflater = (LayoutInflater) this._context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.elements_list_levantamiento, null);
                }
                Question objprop = (Question) getItem(position);

                TextView tvNombre = (TextView) view.findViewById(R.id.name);
                tvNombre.setText(objprop.getName().toString());

                EditText txtId = (EditText) view.findViewById(R.id.id);
                if (objprop.getType().equalsIgnoreCase("COORD")) {
                    //txtId.setText("Coordenadas");
                }
                return view;
            }

        }

    }