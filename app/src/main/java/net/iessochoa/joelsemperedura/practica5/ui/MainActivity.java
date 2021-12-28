package net.iessochoa.joelsemperedura.practica5.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;

import net.iessochoa.joelsemperedura.practica5.R;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //inicia las views
        iniciaViews();

        //Asignar Toolbar a la actividad
        setSupportActionBar(toolbar);


    }
    //Cargar el menu personalizado
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    public void iniciaViews(){
        toolbar = findViewById(R.id.toolbar);
    }
}