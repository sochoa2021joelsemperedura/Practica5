package net.iessochoa.joelsemperedura.practica5.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;

import net.iessochoa.joelsemperedura.practica5.R;
import net.iessochoa.joelsemperedura.practica5.model.DiaDiario;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //inicia las views
        iniciaViews();

        //Asignar Toolbar a la actividad
        setSupportActionBar(toolbar);

        // PRUEBA TODO ELIMINAR

        button.setOnClickListener(e->{
          Intent intent = new Intent(MainActivity.this, EdicionDiaActivity.class);
            startActivity(intent);
        });

    }
    //Cargar el menu personalizado
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    public void iniciaViews(){
        toolbar = findViewById(R.id.toolbar);
        button = findViewById(R.id.btnPrueba);
    }
}