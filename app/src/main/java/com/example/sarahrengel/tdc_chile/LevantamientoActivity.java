package com.example.sarahrengel.tdc_chile;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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


    public class LevantamientoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

        private ListView listaQuestion;

        private LinearLayout StackContent;
        private Registro registro;

        ArrayList<HashMap<String,String>> antenalist;
        List param;
        private JSONArray jsonArray = null;
        private JSONArray jsonQuest = null;
        private JSONObject jsono = null;
        private Activity context;
        AdapterList adapter;
        ArrayList<Question> arrquest;
        ArrayList<Registro> arrregs;

        private static final String URL_ANTENA = "http://186.103.141.44/TorresUnidas.com.Api/index.php/api/Levantamiento/questionAntenna";



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_levantamiento);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            listaQuestion = (ListView) findViewById(R.id.listview);
            antenalist = new ArrayList<HashMap<String, String>>();
            new CargarListTask().execute();

                     DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

           NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

          //  StackContent = (LinearLayout) findViewById(R.id.StackContent);
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
            client.excecute("http://186.103.141.44/TorresUnidas.com.Api/index.php/api/Levantamiento/questionAntenna");


            /////////////////
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
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
      /*  if (id == R.id.action_settings) {
            return true;
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

                //JSONObject json = jsonParser.makeHttpRequest(URL_FORO,"GET",param);

                // Log.d("Fros",json.toString());

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
                            //arrayQuestions.add(question);
                            //arrquest.add(question);
                            //arrayQuestions.add(question);
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

                    adapter = new AdapterList(LevantamientoActivity.this, registro.getQuestions());
                    listaQuestion.setAdapter(adapter);

                }else
                {
                    Log.e("Error","ERROR de JSON");
                }

            }

            /*protected void onPostExecute(String s) {

                if (s == null){

                    adapter = new Adapterlist(LevantamientoActivity.this,arrquest);
                    listaQuestion.setAdapter(adapter);
                }
            }*/
        }

        public class AdapterList extends BaseAdapter {

            private final Activity _context;
            LayoutInflater lInflater;
            private ArrayList<Question>_listData;


            public AdapterList(Activity context, ArrayList<Question> listData) {
                this._context=context;
                this._listData=listData;
                lInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

            public View getView(int position,View view, ViewGroup parent){

                View v = view;
                //ASOCIAMOS LA VISTA AL LAYOUT DEL RECURSO XML DONDE ESTA LA BASE DE

                if(v==null){
                    LayoutInflater infalInflater = (LayoutInflater) this._context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = infalInflater.inflate(R.layout.elementos_lista_levantamiento, null);
                }

                Question objprop = (Question) getItem(position);

                TextView tvNombre = (TextView) v.findViewById(R.id.name);
                EditText txtId = (EditText) v.findViewById(R.id.id);

                tvNombre.setText(objprop.getName().toString());
        /*txtId.setText(objprop.getId().toString());*/

                return v;
            }

            Question getQuestion(int position){
                return ((Question)getItem(position));
            }



        }



        /*public class Adapterlist extends BaseExpandableListAdapter {

            public   Context _context;

            private ArrayList<Registro> _listRegistro;

            //private ArrayList<Question> _listData;


            public Adapterlist(Context context, ArrayList<Registro> listData) {
                this._context = context;
                this._listRegistro = listData;

                //lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            public Adapterlist(){

            }


            public int getGroupCount() {
                //return this._groups.size();
                return this._listRegistro.size();
            }


            public int getChildrenCount(int Groupposition) {
                return this._listRegistro.get(Groupposition).getQuestions().size();
            }

            public Object getGroup(int Groupposition) {
                return _listRegistro.get(Groupposition);
            }


            public Object getChild(int Groupposition, int Childposition) {
                return _listRegistro.get(Groupposition).getQuestions().get(Childposition);
            }


            public long getGroupId(int Groupposition) {

                return Groupposition;
            }


            public long getChildId(int Groupposition, int Childposition) {

                return Childposition;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                return null;
            }

            @Override
            public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
                Question objprop = (Question) getChild(groupPosition,childPosition);
                //View v = view;
                //ASOCIAMOS LA VISTA AL LAYOUT DEL RECURSO XML DONDE ESTA LA BASE DE

                if (view == null) {

                    LayoutInflater inflater = (LayoutInflater) this._context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.content_levantamiento,null);

                    //v = lInflater.inflate(R.layout.content_levantamiento, null, true);
                }

                TextView tvNombre = (TextView) view.findViewById(R.id.name);
                EditText txtId = (EditText) view.findViewById(R.id.id);

                tvNombre.setText(objprop.getName());


             //   return view;
            }

            @Override
            public boolean isChildSelectable(int groupPosition, int childPosition) {
                return false;
            }

            public View getView(int Groupposition,int Childposition, View view, ViewGroup parent) {
               // Question objprop = (Question) getChild(Groupposition,Childposition);
                //View v = view;
                //ASOCIAMOS LA VISTA AL LAYOUT DEL RECURSO XML DONDE ESTA LA BASE DE

                if (view == null) {

                    LayoutInflater inflater = (LayoutInflater) this._context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.content_levantamiento,null);

                    //v = lInflater.inflate(R.layout.content_levantamiento, null, true);
                }

                TextView tvNombre = (TextView) view.findViewById(R.id.name);
                EditText txtId = (EditText) view.findViewById(R.id.id);

                tvNombre.setText(objprop.getName());
        /*txtId.setText(objprop.getId().toString());*/

               // return view;
           // }

            /*Question getQuestion(int position) {
                return ((Question) getItem(position));
            }
        }*/



    }