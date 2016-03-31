package com.example.sarahrengel.tdc_chile;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sarahrengel.tdc_chile.R;

import java.util.ArrayList;

import Levantamiento.Question;
import Levantamiento.Registro;


public class AdapterList extends BaseAdapter {

    private final Activity _context;
    LayoutInflater lInflater;
    private static ArrayList<Question>_listData = null;


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
            v = lInflater.inflate(R.layout.content_levantamiento, null, true);
        }

        Question objprop = (Question) getItem(position);

        TextView tvNombre = (TextView) view.findViewById(R.id.name);
        EditText txtId = (EditText) view.findViewById(R.id.id);

        tvNombre.setText(objprop.getName().toString());
        /*txtId.setText(objprop.getId().toString());*/

        return v;
    }

    Question getQuestion(int position){
        return ((Question)getItem(position));
    }



}
