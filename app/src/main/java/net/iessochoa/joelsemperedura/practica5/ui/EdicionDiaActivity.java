package net.iessochoa.joelsemperedura.practica5.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import net.iessochoa.joelsemperedura.practica5.R;
import net.iessochoa.joelsemperedura.practica5.model.DiaDiario;

import java.util.Calendar;
import java.util.Date;

public class EdicionDiaActivity extends AppCompatActivity {
    public static String EXTRA_EDICION_DIA = "EdicionDiaActivity.diaDiario"; // el objeto diaDiario que llega

    DiaDiario diaDiario;
    Intent iBack; //Devolver a la actividad principal el intent

    Spinner spValoracion;
    Button btnFecha;
    TextView tvFecha;
    Button btnGuardar;
    EditText etResumenBreve;
    EditText etDiarioTexto;

    Date newFecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edicion_dia);

        iniciaViews();

        //obtener los items desde el xml para el spinner
        ArrayAdapter<CharSequence>adaptadorValoracion = ArrayAdapter.createFromResource(this,
                R.array.valoracion, android.R.layout.simple_spinner_item);
        spValoracion.setAdapter(adaptadorValoracion);

        iBack = getIntent(); //recupera el intent y verifica si es nuevo o existente
        if (iBack.getParcelableExtra(EXTRA_EDICION_DIA) == null){
            this.setTitle(getString(R.string.stNuevaTarea)); //titulo
            this.diaDiario = null;
            //posicion del valor por defecto del spinner
            spValoracion.setSelection(5);
        }else{
            this.diaDiario = iBack.getParcelableExtra(EXTRA_EDICION_DIA); //recupero objeto
            this.setTitle(getString(R.string.stTarea)+" "+diaDiario.getId()); //id del diaDiario como titulo


            spValoracion.setSelection(adaptadorValoracion.getPosition(String.valueOf(diaDiario.getValoracionDia()))); //valoracion del objeto
            tvFecha.setText( DiaDiario.getFechaEstaticaFormatoLocal(diaDiario.getFecha()) ); //Revisar si captura la fecha como string
            etResumenBreve.setText( diaDiario.getResumen() );
            etDiarioTexto.setText( diaDiario.getContenido() );

        }


        //Boton fecha abrira un DatePickerDialog
        btnFecha.setOnClickListener(e->{
            onClickFecha();
        });

        //Operaciones realizadas al pulsar el boton guardar
        btnGuardar.setOnClickListener(e->{
            if (compruebaCampos()){
                guardarDia();
                setResult(RESULT_OK,iBack);
                finish();
            } else{
               abrirDialogo();
            }
        });


    }
    //*********VERIFICA SI ES UN DIA NUEVO O NO Y LO GUARDA COMO DIADIARIO*************//
    private void guardarDia() {
        if (diaDiario == null){
            //Guardar las preferencias del dia antes de salir
            guardarDiaPreferencias(newFecha);
            //si es un dia nuevo
             iBack.putExtra(EXTRA_EDICION_DIA,new DiaDiario(
                     newFecha,Integer.parseInt(spValoracion.getSelectedItem().toString()),
                     etResumenBreve.getText().toString(),etDiarioTexto.getText().toString()));
         }else{ //si ya existe
            diaDiario.setContenido(etDiarioTexto.getText().toString());
            //asi me quito un error
            if (diaDiario.getFecha() != null){
                diaDiario.setFecha(diaDiario.getFecha());
            }
            diaDiario.setValoracionDia(Integer.parseInt(spValoracion.getSelectedItem().toString()));
            diaDiario.setResumen(etResumenBreve.getText().toString());
            // diaDiario.setFotoUri();
            //Guardar las preferencias del dia antes de salir
            guardarDiaPreferencias(diaDiario.getFecha());
            iBack.putExtra(EXTRA_EDICION_DIA,diaDiario); //el mismo que nos dan se devuelve
         }


    }
    //*************GUARDA LA FECHA DE EL DIADIARIO AL QUE SE REFIERE EN EL FICHERO DE PREFERENCIAS*********//
    private void guardarDiaPreferencias(Date fecha){
        //buscamos el fichero de preferencias
        SharedPreferences sharedPref =
                this.getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE);
        //lo abrimos en modo edición
        SharedPreferences.Editor editor = sharedPref.edit();
        //guardamos la fecha del día como entero
        editor.putLong(getString(R.string.pref_key_ultimo_dia),
                fecha.getTime());
        //finalizamos
        editor.commit();
    }


    // en caso de faltar algun campo por rellener abre un dialogo
    private void abrirDialogo() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.stAlerta)
                .setMessage(R.string.stMensajeAlertDialog)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //no hace nada, solo avisa
                    }
                }).show();

    }

    //comprueba que el objeto puede ser creado y enviado a la activity principal
    private boolean compruebaCampos() {
        Boolean resultado = true;

        if (tvFecha.getText().toString().equals("")||
        etResumenBreve.getText().toString().equals("")||
        etDiarioTexto.getText().toString().equals("")){
            resultado = false;
        }

        return resultado;
    }

    public void iniciaViews(){
        spValoracion = findViewById(R.id.spValoracion);
        btnFecha = findViewById(R.id.btnFecha);
        tvFecha = findViewById(R.id.tvFecha);
        btnGuardar = findViewById(R.id.btnGuardar);
        etResumenBreve = findViewById(R.id.etResumenBreve);
        etDiarioTexto = findViewById(R.id.etDiarioTexto);
    }

    //Nos abre el click del boton fecha
    public void onClickFecha(){
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog dialogo = new DatePickerDialog(this, new
                DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int
                            monthOfYear, int dayOfMonth) {

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        Date fecha=calendar.getTime();
                        if (diaDiario == null){
                            newFecha = fecha;
                        }else{
                            diaDiario.setFecha(fecha);
                        }


                        //Asignacion de la fecha seleccionada en el dialogo al textView
                        tvFecha.setText(DiaDiario.getFechaEstaticaFormatoLocal(fecha));
                    }
                }, newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));
        dialogo.show();

    }

}