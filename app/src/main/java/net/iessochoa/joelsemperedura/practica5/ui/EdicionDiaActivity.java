package net.iessochoa.joelsemperedura.practica5.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import net.iessochoa.joelsemperedura.practica5.R;
import net.iessochoa.joelsemperedura.practica5.model.DiaDiario;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EdicionDiaActivity extends AppCompatActivity {
    Spinner spValoracion;
    Button btnFecha;
    TextView tvFecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edicion_dia);

        iniciaViews();

        //obtener los items desde el xml para el spinner
        ArrayAdapter<CharSequence>adaptadorValoracion = ArrayAdapter.createFromResource(this,
                R.array.valoracion, android.R.layout.simple_spinner_item);
        spValoracion.setAdapter(adaptadorValoracion);
        //posicion del valor por defecto del spinner
        spValoracion.setSelection(5);

        //Boton fecha abrira un DatePickerDialog
        btnFecha.setOnClickListener(e->{
            onClickFecha();
        });


    }

    public void iniciaViews(){
        spValoracion = findViewById(R.id.spValoracion);
        btnFecha = findViewById(R.id.btnFecha);
        tvFecha = findViewById(R.id.tvFecha);
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

                        //Asignacion de la fecha seleccionada en el dialogo al textView
                        tvFecha.setText(DiaDiario.getFechaEstaticaFormatoLocal(fecha));
                    }
                }, newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));
        dialogo.show();

    }

}