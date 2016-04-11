package Fragmentos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sarahrengel.tdc_chile.LevantamientoActivity;
import com.example.sarahrengel.tdc_chile.LevantamientoProductoActivity;
import com.example.sarahrengel.tdc_chile.LevantamientoProductoCableadoActivity;
import com.example.sarahrengel.tdc_chile.MainActivity;
import com.example.sarahrengel.tdc_chile.MainElementosActivity;
import com.example.sarahrengel.tdc_chile.R;
import com.google.gson.Gson;

import BD_Levantamiento.RegistroSQLiteHelper;
import Levantamiento.Registro;


public class Fragment1 extends DialogFragment implements DialogInterface.OnClickListener {


    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(R.layout.activity_fragment1, null);

        alertDialogBuilder.setView(view);

        String nombre_antena = getArguments().getString("nombre");
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
                Registro registro = db.obtenerRegistro(Integer.parseInt(getArguments().getString("posicion")));

                Gson gson = new Gson();
                Log.e("Formato Json arreglo", gson.toJson(registro));
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

}
