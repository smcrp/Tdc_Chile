package com.example.sarahrengel.tdc_chile;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import Connection.HttpClient;
import Connection.OnHttpRequestComplete;
import Connection.Response;
import Levantamiento.Question;
import Levantamiento.Registro;


public class LevantamientoActivityCopia extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private LinearLayout StackContent;
    private JSONArray jsonArray = null;
    private JSONArray jsonQuest = null;
    private Registro registro;
    private Activity context;
    private ListView listaQuestion;
    AdapterList adapt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levantamiento);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;
        listaQuestion = (ListView) findViewById(R.id.listview);
       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            }
        });*/

       DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

       NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

      //  StackContent = (LinearLayout) findViewById(R.id.StackContent);
        /////////////////
       HttpClient client = new HttpClient(new OnHttpRequestComplete() {
            @Override
            public void onComplete(Response status) {
                if (status.isSuccess()){
                    Gson gson = new GsonBuilder().create();
                    Registro formulario = leerJson(status);
                    armarFormulario(formulario);
                    new CargarListTask().execute();
                }

            }
        });
        client.excecute("http://186.103.141.44/TorresUnidas.com.Api/index.php/api/Levantamiento/questionAntenna");
    }

    private Registro leerJson(Response status){
        try {
            JSONObject jsono = new JSONObject(status.getResult());
            jsonArray = jsono.getJSONArray("result");
            registro = new Registro();
            ArrayList<Question> arrayQuestions = new ArrayList<>();
            for (int i=0; i < jsonArray.length(); i++){
                JSONObject c = jsonArray.getJSONObject(i);

                registro.setId(c.getString("id"));
                registro.setName(c.getString("name"));

                jsonQuest = c.getJSONArray("questions");

                for (int j=0; j < jsonQuest.length(); j++) {
                    Question question = new Question();
                    JSONObject l = jsonQuest.getJSONObject(j);
                    Log.d("IDQ", l.getString("id"));
                    question.setId(l.getString("id"));
                    question.setName(l.getString("name"));
                    question.setType(l.getString("type"));
                    question.setIdType(l.getString("idtype"));
                    arrayQuestions.add(question);
                }
                registro.setQuestions(arrayQuestions);
                        /*Question ant = gson.fromJson(antena, Question.class);
                        TextView t = new TextView(getBaseContext());
                        t.setText(ant.getNameAntena());
                        StackContent.addView(t);*/
            }

        } catch (Exception e){

        }
        return registro;
    }

    private void armarFormulario(Registro formulario){

        Log.d("FORM ID", formulario.getId());
        Log.d("FORM NAME", formulario.getName());

        for (Question question : formulario.getQuestions()) {


            Log.d("PREG ID", question.getId());
            Log.d("PREG NAME", question.getName());
            Log.d("PREG TYPE", question.getType());
            Log.d("PREG IDTYPE", question.getIdType());
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_levantamiento, menu);
        return true;
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
    private class CargarListTask extends AsyncTask<Void, Void, AdapterList>{
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }
        @Override
        protected AdapterList doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            try{
            }catch(Exception ex){
                ex.printStackTrace();
            }
            AdapterList adaptador = new AdapterList(context,registro.getQuestions());
            return adaptador;
        }

    @Override
    protected void onPostExecute(AdapterList adaptador) {
        // TODO Auto-generated method stub
        super.onPostExecute(adaptador);
        listaQuestion.setAdapter(adaptador);
    }
}


}