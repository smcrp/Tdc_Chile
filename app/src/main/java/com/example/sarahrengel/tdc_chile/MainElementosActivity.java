package com.example.sarahrengel.tdc_chile;

import android.app.FragmentManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import BD_Levantamiento.RegistroSQLiteHelper;
import Fragmentos.Fragment1;
import Fragmentos.FragmentElementos;
import Levantamiento.Question;
import Levantamiento.Registro;

public class MainElementosActivity extends ListActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private LinearLayout StackContent;
    private RegistroSQLiteHelper db;
    private Registro registro;
    private ArrayList<String> results = new ArrayList<String>();
    ListView element;
    ArrayList<HashMap<String,String>> listaelementos;
    AdapterList3 adapterList3;
    private static final String TAG_IDANTENA = "id";
    private static final String TAG_DNIANTENA = "Answer";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elementos_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);
        listaelementos = new ArrayList<HashMap<String, String>>();
        element = (ListView)findViewById(R.id.list_elementos);
        db = new RegistroSQLiteHelper(getApplicationContext());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getBaseContext(), LevantamientoActivity.class);
                startActivity(intent);
                 /*final Intent intent = new Intent(getBaseContext(), LevantamientoProductoActivity.class);
                startActivity(intent);*/
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.        string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

       String idRegistro = getIntent().getStringExtra("idRegistro");
//        Log.d("PROBANDO ID REGISTRO",idRegistro);

        StackContent = (LinearLayout) findViewById(R.id.StackContent);
        //List<Registro> registros = db.obtenerRegistros();

        List<Question> elementos = db.obtenerElementosdeTorre(idRegistro);
        db.cerrarBD();
        for (int i = 0; i < elementos.size(); i++){

            String id = String.valueOf(elementos.get(i).getIdRegistro());
            String Answer = elementos.get(i).getAnswer();
            Log.d("Id", String.valueOf(elementos.get(i).getId()));

            HashMap map = new HashMap();
            map.put(TAG_IDANTENA, id);
            map.put(TAG_DNIANTENA, Answer);

            listaelementos.add(map);
        }
        Log.d("Elementos:::: ", String.valueOf(elementos.size()));

        if (elementos.size() > 0) {
            mostrarResultRegistros();
        }
    }

    class AdapterList3 extends BaseAdapter {

        private Context context;
        private ArrayList<HashMap<String,String>> Antena;

        public AdapterList3(Context context, ArrayList<HashMap<String,String>> trabajador){
            super();
            this.context = context;
            this.Antena = trabajador;
        }


        @Override
        public int getCount() {
            return Antena.size();
        }

        @Override
        public Object getItem(int position) {
            return Antena.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.lista_elementos_productos, null);
            }
            final TextView nombre = (TextView)convertView.findViewById(R.id.elem_nombre);
            nombre.setText(Antena.get(position).get(TAG_DNIANTENA));


            return convertView;
        }
    };


    protected void onPause(){
        super.onPause();
    }

    private void mostrarResultRegistros() {
        TextView tView = new TextView(this);
        tView.setText("Registros creados");//titulo del main (registro)
        getListView().addHeaderView(tView);

        adapterList3 = new AdapterList3(MainElementosActivity.this,listaelementos);
        //lista.setAdapter(adapterList3);
        element.setAdapter(adapterList3);

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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

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



}
