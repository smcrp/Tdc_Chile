package Fragmentos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sarahrengel.tdc_chile.LevantamientoProductoCableadoActivity;
import com.example.sarahrengel.tdc_chile.MainActivity;
import com.example.sarahrengel.tdc_chile.MainElementosActivity;
import com.example.sarahrengel.tdc_chile.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import BD_Levantamiento.HistoricoSQLiteHelper;
import BD_Levantamiento.RegistroSQLiteHelper;
import Connection.JsonParser;
import Levantamiento.AnswerJson;
import Levantamiento.Products;
import Levantamiento.ProductsJson;
import Levantamiento.Question;
import Levantamiento.Registro;
import Levantamiento.RegistroJson;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.entity.mime.content.StringBody;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;


public class Fragment1 extends DialogFragment implements DialogInterface.OnClickListener {


    String foto;
    String dnfoto;
    ProgressDialog pDialog;
    ProgressDialog dialog;
    private static final String URL="http://186.103.141.44/TorresUnidas.com.Api/index.php/api/Levantamiento/uploadImage";
    JSONArray arrayFormularios = new JSONArray();
    Gson gson;
    JsonParser jsonParser = new JsonParser();
    String jsonForm = "";
    String jsonForm2 = "";
    String id_antena;

    private static final String REGISTER_URL ="http://186.103.141.44/TorresUnidas.com.Api/index.php/api/Levantamiento/productsAntenna";
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(R.layout.activity_fragment1, null);

        alertDialogBuilder.setView(view);

        String nombre_antena = getArguments().getString("nombre");
        id_antena = getArguments().getString("id");

        alertDialogBuilder.setTitle("TORRE");
        //TextView texto = (TextView) view.findViewById(R.id.posicion);

        Button agregar = (Button)view.findViewById(R.id.btn_agregar);
            agregar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    //final Intent intent = new Intent(((MainActivity)getActivity()).getBaseContext(), LevantamientoProductoActivity.class);
                    final Intent intent = new Intent(((MainActivity) getActivity()).getBaseContext(), LevantamientoProductoCableadoActivity.class);
                    intent.putExtra("idRegistro", getArguments().getString("posicion"));
                    startActivity(intent);
                }
            });

        Button eliminar = (Button)view.findViewById(R.id.btn_eliminar);
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Registro registro = new Registro();
                registro.setId(Integer.parseInt(getArguments().getString("posicion")));
                RegistroSQLiteHelper db = new RegistroSQLiteHelper(((MainActivity)getActivity()).getBaseContext());
                db.eliminarRegistro(registro);
                MainActivity activity = (MainActivity)getActivity();
                activity.consultarRegistros();
                activity.mostrarResultRegistros();
                dismiss();
            }
        });

        Button enviar = (Button)view.findViewById(R.id.btn_enviar);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RegistroSQLiteHelper db = new RegistroSQLiteHelper(((MainActivity)getActivity()).getBaseContext());
                RegistroJson registroJson = db.obtenerRegistroJson(Integer.parseInt(getArguments().getString("posicion")));
                ArrayList<Question> preguntasNivel1 = db.obtenerPreguntaJson(Integer.parseInt(getArguments().getString("posicion")), 1);

                /****CABLEANDO RESPUESTAS (MEJORAR)****/

                /*for (Antena antena: registroJson) {
                    AntennaJson antJson = new AntennaJson();
                    ArrayList<AntennaJson> antenaListJson = new ArrayList<>();*/
                    for (Question respuesta : preguntasNivel1) {
                        if (respuesta.getId() == 20) { //nombre de la antena
                            // registroJson.setId(respuesta.getId());
                            registroJson.setValue(respuesta.getAnswer());
                        } /*else if (respuesta.getId()==21){ //direccion
                        registroJson.setDireccion(respuesta.getAnswer());
                    } */
                }
                List<RegistroJson> listReg = new ArrayList<RegistroJson>();
                listReg.add(registroJson);

                ArrayList<Products> listaProductos = db.obtenerProductos(Integer.parseInt(getArguments().getString("posicion")));
                ArrayList<ProductsJson> productsListJson = new ArrayList<ProductsJson>();

                for (Products product: listaProductos) {
                   /* ProductsJson prodJson = new ProductsJson();
                    prodJson.setIdproduct(product.getId());*/
                    ArrayList<AnswerJson> answersListJson = new ArrayList<AnswerJson>();
                    for (Question pregunta: product.getQuestions()) {
                        AnswerJson answerJson = new AnswerJson();
                        answerJson.setId(pregunta.getId());
                        answerJson.setValue(pregunta.getAnswer());
                        Log.e("Preg", String.valueOf(pregunta.getId()));
                        Log.e("Preg", pregunta.getAnswer());
                        answersListJson.add(answerJson);
                    }
                    ProductsJson prodJson = new ProductsJson();
                    prodJson.setIdproduct(product.getId());
                    prodJson.setAnswers(answersListJson);
                    productsListJson.add(prodJson);
                }

                /*ArrayList<Question> preguntasNivel2 = db.obtenerPreguntaJson(Integer.parseInt(getArguments().getString("posicion")), 2);
                ArrayList<Products> products = new ArrayList<Products>();
                for (Question respuesta: preguntasNivel2) {
                    Products prod = new Products();
                    prod.setId(respuesta.getId());
                    prod.setValue(respuesta.getAnswer());
                    products.add(prod);
                }*/


                HistoricoSQLiteHelper helper = new HistoricoSQLiteHelper(getActivity(),"Historico",null,1);
                SQLiteDatabase db1 = helper.getWritableDatabase();

                Cursor c =  db1.rawQuery("select * from foto", null);

                if (c.moveToFirst()){
                    foto = c.getString(0);
                    dnfoto = c.getString(1);
                }
                db1.close();
                db.close();
                final String title = "foto";
                final String coordx ="32552452552";
                final String coordy = "5363663663";
                final String idproduct ="11";
                final String idquestion ="11";

                new Enviarfoto().execute(foto, dnfoto, title, coordx, coordy, idproduct, idquestion);

              //  Log.d("RUTA FOTO", foto);
                //Log.d("DN FOTO", dnfoto);

                gson = new Gson();
                String json1 = gson.toJson(listReg);
                String json2 = gson.toJson(productsListJson);
                Log.e("JSON1", json1);
                Log.e("JSON2", json2);
                Log.e("JSON3",json1+json2);

                /*listReg.add(new BasicNameValuePair("antenas", json1));
                String json2 = gson.toJson(products);
                jsonForm = json1 + json2;*/
                jsonForm = json1 + json2;
                //jsonForm = "[{ \"id\": 8,\"value\": \"prueba3\"}][{\"idproduct\": 20,\"answers\": [{\"id\": 20,\"value\": \"prueba20\"},{\"id\": 20,\"value\": \"prueba20\"}]},{\"idproduct\": 21,\"answers\": [{\"id\": 21,\"value\": \"prueba21\"},{\"id\": 21,\"value\": \"prueba21\"}]}]";

                //Log.d("String jsonForm", jsonForm);

               // new HttpAsyncTask().execute(REGISTER_URL);
                // new Enviarformulario().execute(jsonForm.toString());
               // HttpResponse.class(REGISTER_URL, gson.toJson(registroJson));
               // httpHandler handler = new httpHandler();
                //String txt = handler.post("http://186.103.141.44/TorresUnidas.com.Api/index.php/api/Levantamiento/productsAntenna",gson.toJson(registroJson));
                new EnvioDatos().execute(json1, json2);
                Log.e("Formato jsonForm arreglo", jsonForm);//JSON ARMADO LISTO PARA ENVIAR
            }
        });


        Button ver = (Button)view.findViewById(R.id.btn_ver);
        ver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(((MainActivity) getActivity()).getBaseContext(), MainElementosActivity.class);
                intent.putExtra("idRegistro", getArguments().getString("posicion"));
                Log.d("idRegistro", getArguments().getString("posicion"));
                startActivity(intent);
            }
        });

        //texto.setText(strtext);

        //El botón cerrar
        alertDialogBuilder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return alertDialogBuilder.create();
    }


    @Override
    public void onClick(DialogInterface dialog, int position) {
        Log.d("DIALOG", String.valueOf(position));
    }

    class Enviarfoto extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {

            try {

                HttpClient httpClient = new DefaultHttpClient();
                //HttpPost httpPost = new HttpPost(URL);
                HttpPost httpPost = new HttpPost(URL);

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();

                File file = new File(params[0]);
                FileBody body = new FileBody(file);

                builder.addPart("uploaded_file", body);
                builder.addPart("dnfoto", new StringBody(params[1], ContentType.TEXT_PLAIN));
                builder.addPart("title", new StringBody(params[2], ContentType.TEXT_PLAIN));
                builder.addPart("coordx", new StringBody(params[3], ContentType.TEXT_PLAIN));
                builder.addPart("coordy", new StringBody(params[4], ContentType.TEXT_PLAIN));
                builder.addPart("idproduct", new StringBody(params[5], ContentType.TEXT_PLAIN));
                builder.addPart("idquestion", new StringBody(params[6], ContentType.TEXT_PLAIN));

                Log.d("RegistroWIFI", "JSON A Enviar= " + builder);

                HttpEntity httpEntity = builder.build();

                httpPost.setEntity(httpEntity);

                HttpResponse response = httpClient.execute(httpPost);
                httpClient.getConnectionManager().shutdown();

                String json = response.toString();

                Log.d("JSON",json);


            }catch (Exception e){

                e.printStackTrace();

            }

            return null;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Subiendo la informacion" );
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();
        }
    }

    public static String POST(String url, String json){
        InputStream inputStream = null;
        String result = "";
        try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.e("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0],jsonForm);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.e("RESULTADO",result);
            dismiss();
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    class EnvioDatos extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {

            String antena = params[0];
            String prod = params[1];
            int success;

            try{
                List datos = new ArrayList();
                datos.add(new BasicNameValuePair("antenna", antena));
                datos.add(new BasicNameValuePair("products", prod));


                Log.d("request!", "starting");
                JSONObject json = jsonParser.makeHttpRequest(REGISTER_URL,"POST",datos);

                Log.e("JSON A Enviar", "JSON A Enviar= " + datos);

                success = json.getInt("code");

                if (success == 0){
                    Log.d("Datos enviandos:", json.toString());

                    return json.getString("description");

                }else{
                    Log.d("Falla al enviar", json.getString("description"));
                    return json.getString("description");
                }

            }catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Enviando información...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {

            if (s != null){
                Registro registro = new Registro();
                registro.setId(Integer.parseInt(getArguments().getString("posicion")));
                RegistroSQLiteHelper db = new RegistroSQLiteHelper(((MainActivity)getActivity()).getBaseContext());
                db.eliminarRegistro(registro);
                MainActivity activity = (MainActivity)getActivity();
                activity.consultarRegistros();
                activity.mostrarResultRegistros();
                dialog.setMessage("Datos enviados exitosamente");
                Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
            }
            dialog.dismiss();
            getDialog().dismiss();
        }
    }



}
