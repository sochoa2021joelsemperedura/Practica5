package net.iessochoa.joelsemperedura.practica5.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
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

    RecyclerView rvLista;
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

        //***establece el layout del reclyclerView y le añade el adapter***//
        final DiarioListAdapter adapter = new DiarioListAdapter(this);
        rvLista.setAdapter(adapter);
        rvLista.setLayoutManager(new LinearLayoutManager(this));


        //Recuperacion o creacion del viewModel
        diarioViewModel = new ViewModelProvider(this).get(DiarioViewModel.class);
        //Observer
        diarioViewModel.getAllDiarios().observe(this, new Observer<List<DiaDiario>>() {
            @Override
            public void onChanged(List<DiaDiario> diaDiarios) {
               adapter.setDiarios(diaDiarios);
               //Log.d("P5","tamaño: "+diaDiarios.size());
            }
        });

        //Nuevo DiaDiario
       fabAnyadir.setOnClickListener(e->{
          Intent intent = new Intent(MainActivity.this, EdicionDiaActivity.class);
            startActivityForResult(intent,OPTION_REQUEST_NUEVA); //abre edicionDiaActivity con un nuevo DiaDiario
        });

       //Borrar DiaDiario
        adapter.setOnBorrarClickListener(new DiarioListAdapter.onItemBorrarClickListener() {
            @Override
            public void onItemBorrarClick(DiaDiario diaDiario) {
                borrarDia(diaDiario);
            }
        });
        //editar DiaDiario
        adapter.setOnClickListener(new DiarioListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(DiaDiario diaDiario) {
                Intent intent = new Intent(MainActivity.this,EdicionDiaActivity.class);
                intent.putExtra(EdicionDiaActivity.EXTRA_EDICION_DIA,diaDiario);
                startActivityForResult(intent,OPTION_REQUEST_MODIFICAR);
            }
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
        rvLista = findViewById(R.id.rvLista);
    }

    //*************************MANEJO**DATOS****************************//
    private void anyadirDia(DiaDiario diaDiario){
        diarioViewModel.insert(diaDiario);
    }
    private void modificarDia(DiaDiario diaDiario){//TODO diarioViewModel.}
    //Borrado con dialogo de aviso.
    private void borrarDia(DiaDiario diaDiario){
        AlertDialog.Builder dialogo = new AlertDialog.Builder(MainActivity.this);
        dialogo.setTitle(getString(R.string.stAviso)); //titulo
        dialogo.setMessage(getString(R.string.stAvisoMensaje)+diaDiario.getId()); //mensaje

        //boton ok y evento
        dialogo.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // en caso de ok
                diarioViewModel.delete(diaDiario);
            }
        });
        dialogo.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //en caso de cancel nada
            }
        });
        dialogo.show();
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
                    modificarDia(diaDiario);
                    break;
            }
        }
    }
}