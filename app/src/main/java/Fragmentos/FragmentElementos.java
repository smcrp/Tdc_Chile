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
import com.example.sarahrengel.tdc_chile.R;


public class FragmentElementos extends DialogFragment implements DialogInterface.OnClickListener {


    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(R.layout.activity_fragment1, null);

        alertDialogBuilder.setView(view);

        String nombre_antena = getArguments().getString("nombre");
        alertDialogBuilder.setTitle("TORRE");
        //TextView texto = (TextView) view.findViewById(R.id.posicion);

        String strtext = getArguments().getString("posicion");

        Button agregar = (Button)view.findViewById(R.id.btn_agregar);
        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(((MainActivity)getActivity()).getBaseContext(), LevantamientoProductoActivity.class);
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
