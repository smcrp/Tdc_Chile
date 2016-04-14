package com.example.sarahrengel.tdc_chile;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by Sarah Rengel on 12-04-2016.
 */
public class httpHandler {


    public String post(String posturl, String json){

        try {

            HttpClient httpclient = new DefaultHttpClient();
/*Creamos el objeto de HttpClient que nos permitira conectarnos mediante peticiones http*/

            HttpPost httppost = new HttpPost(posturl);
/*El objeto HttpPost permite que enviemos una peticion de tipo POST a una URL especificada*/

            //AÑADIR PARAMETROS

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("data",json));

   /*Una vez añadidos los parametros actualizamos la entidad de httppost, esto quiere decir en pocas palabras anexamos los parametros al objeto para que al enviarse al servidor envien los datos que hemos añadido*/

            httppost.setEntity(new UrlEncodedFormEntity(params));

            /*Finalmente ejecutamos enviando la info al server*/

            HttpResponse resp = httpclient.execute(httppost);
            HttpEntity ent = resp.getEntity();/*y obtenemos una respuesta*/

            String text = EntityUtils.toString(ent);

            return text;

        }

        catch(Exception e) { return "error";}

    }



}
