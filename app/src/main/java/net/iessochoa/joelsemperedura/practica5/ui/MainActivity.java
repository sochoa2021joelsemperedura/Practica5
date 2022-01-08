package net.iessochoa.joelsemperedura.practica5.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.iessochoa.joelsemperedura.practica5.R;
import net.iessochoa.joelsemperedura.practica5.model.DiaDiario;
import net.iessochoa.joelsemperedura.practica5.viewmodels.DiarioViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int OPTION_REQUEST_NUEVA = 0; //nuevo día
    public static final int OPTION_REQUEST_MODIFICAR = 1; //modificar día

    // nos permite mantener los datos cuando se reconstruye la actividad
    private DiarioViewModel diarioViewModel;

    FloatingActionButton fabAnyadir;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //inicia las views
        iniciaViews();

        //Asignar Toolbar a la actividad
        setSupportActionBar(toolbar);

        //Recuperacion o creacion del viewModel
        diarioViewModel = new ViewModelProvider(this).get(DiarioViewModel.class);
        //Observer
        diarioViewModel.getAllDiarios().observe(this, new Observer<List<DiaDiario>>() {
            @Override
            public void onChanged(List<DiaDiario> diaDiarios) {
                //TODO Actualizamos el RecyclerView cuando exista
               Log.d("P5","tamaño: "+diaDiarios.size());
            }
        });

       fabAnyadir.setOnClickListener(e->{
          Intent intent = new Intent(MainActivity.this, EdicionDiaActivity.class);
            startActivityForResult(intent,OPTION_REQUEST_NUEVA); //abre edicionDiaActivity con un nuevo DiaDiario
        });

    }
    //*************************MENU****************************//
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }
    //*************************VIEWS****************************//
    public void iniciaViews(){
        fabAnyadir = findViewById(R.id.fabAnyadir);
        toolbar = findViewById(R.id.toolbar);
    }

    //*************************MANEJO**DATOS****************************//
    private void anyadirDia(DiaDiario diaDiario){
        diarioViewModel.insert(diaDiario);
    }

    //*************************OPCIONES**ITEMS**MENU****************************//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED){
            DiaDiario diaDiario;
            diaDiario = (DiaDiario)data.getParcelableExtra(EdicionDiaActivity.EXTRA_EDICION_DIA);
            switch (requestCode){
                case OPTION_REQUEST_NUEVA:
                    anyadirDia(diaDiario);
                    break;
                case OPTION_REQUEST_MODIFICAR:
                    //TODO metodo modificarDiaDiario(diaDiario) que traiga el dato desde viewModel
                    break;
            }
        }
    }
}