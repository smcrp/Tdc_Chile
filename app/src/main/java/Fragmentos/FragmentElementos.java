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

import com.example.sarahrengel.tdc_chile.LevantamientoProductoActivity;
import com.example.sarahrengel.tdc_chile.MainActivity;
import com.example.sarahrengel.tdc_chile.MainElementosActivity;
import com.example.sarahrengel.tdc_chile.R;

import BD_Levantamiento.RegistroSQLiteHelper;
import Levantamiento.Question;
import Levantamiento.Registro;


public class FragmentElementos extends DialogFragment implements DialogInterface.OnClickListener {


    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(R.layout.activity_fragment_elementos, null);

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setTitle("PRODUCTO");

        String nombre_antena = getArguments().getString("nombre");
        String strtext = getArguments().getString("posicion");

        Button eliminar = (Button)view.findViewById(R.id.btn_eliminar);
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Question pregunta = new Question();
                pregunta.setId(Integer.parseInt(getArguments().getString("posicion")));
                RegistroSQLiteHelper db = new RegistroSQLiteHelper(((MainElementosActivity)getActivity()).getBaseContext());
                db.eliminarPregunta(pregunta);
                MainElementosActivity activity = (MainElementosActivity)getActivity();
                activity.consultarRegistros();
                activity.mostrarResultRegistros();
                dismiss();
            }
        });

        Button editar = (Button)view.findViewById(R.id.btn_editar);
        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


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
