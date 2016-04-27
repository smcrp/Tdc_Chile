package com.example.sarahrengel.tdc_chile;

import android.Manifest;
import android.app.FragmentManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import BD_Levantamiento.HistoricoSQLiteHelper;
import BD_Levantamiento.RegistroSQLiteHelper;
import Connection.HttpClient;
import Connection.OnHttpRequestComplete;
import Connection.Response;
import Fragmentos.Fragment1;
import Levantamiento.Question;
import Levantamiento.Registro;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class MainActivity extends ListActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private LinearLayout StackContent;
    private RegistroSQLiteHelper db;
    private Registro registro;
    private ArrayList<String> results = new ArrayList<String>();
    ListView antenas;
    ArrayList<HashMap<String,String>> listaantenas;
    AdapterList3 adapterList3;
    private static final String TAG_IDANTENA = "id";
    private static final String TAG_DNIANTENA = "Answer";
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Torres");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){

                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                        PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

                            requestPermissions(new String[]{Manifest.permission.CAMERA},
                                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
                        }
                    }
                }
            }

        }

       // setSupportActionBar(toolbar);
        antenas = (ListView)findViewById(R.id.list_antena);
        db = new RegistroSQLiteHelper(getApplicationContext());
        /*Registro registro = new Registro();
        registro.setId(2);
        db.eliminarRegistro(registro);*/
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    AlertNoGps();
                        /*Intent settingsIntent = new
                                Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        startActivityForResult(settingsIntent, 0);*/
                }else {

                    Intent intent = new Intent(getBaseContext(), LevantamientoActivity.class);
                    startActivity(intent);
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.        string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        StackContent = (LinearLayout) findViewById(R.id.StackContent);

        consultarRegistros();
        mostrarResultRegistros();

        antenas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String i = listaantenas.get(position).get(TAG_IDANTENA);
                String e = listaantenas.get(position).get(TAG_DNIANTENA);

             //   Toast.makeText(MainActivity.this, i, Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();
                bundle.putString("posicion", i);
                bundle.putString("nombre", e);

                FragmentManager frm = getFragmentManager();
                Fragment1 fragment1 = new Fragment1();
                fragment1.setArguments(bundle);
                fragment1.show(frm, "alerta");
                fragment1.setCancelable(false);
            }
        });

    }

    class AdapterList3 extends BaseAdapter {

        private Context context;
        private ArrayList<HashMap<String,String>> antena;

        public AdapterList3(Context context, ArrayList<HashMap<String,String>> trabajador){
            super();
            this.context = context;
            this.antena = trabajador;
        }


        @Override
        public int getCount() {
            return antena.size();
        }

        @Override
        public Object getItem(int position) {
            return antena.get(position);
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
                convertView = infalInflater.inflate(R.layout.lista_elementos_antenas, null);
            }
            final TextView nombre = (TextView)convertView.findViewById(R.id.elem_nombre);
            nombre.setText(antena.get(position).get(TAG_DNIANTENA));

            return convertView;
        }
    };

    protected void onPause(){
        super.onPause();
    }

    public void consultarRegistros() {
        List<Question> preguntas = db.obtenerNombreAntena();
        listaantenas = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < preguntas.size(); i++){
            String id = String.valueOf(preguntas.get(i).getIdRegistro());
            String Answer = preguntas.get(i).getAnswer();

            HashMap map = new HashMap();
            map.put(TAG_IDANTENA, id);
            map.put(TAG_DNIANTENA, Answer);

            listaantenas.add(map);
        }
    }

    public void mostrarResultRegistros() {
        TextView tView = new TextView(this);
        tView.setText("Registros creados");//titulo del main (registro)
        getListView().addHeaderView(tView);

        adapterList3 = new AdapterList3(MainActivity.this,listaantenas);
        antenas.setAdapter(adapterList3);
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

    private void AlertNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }

}
