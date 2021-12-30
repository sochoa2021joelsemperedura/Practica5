package net.iessochoa.joelsemperedura.practica5.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.iessochoa.joelsemperedura.practica5.R;
import net.iessochoa.joelsemperedura.practica5.model.DiaDiario;

public class MainActivity extends AppCompatActivity {
    public static final int OPTION_REQUEST_NUEVA = 0; //nuevo día
    public static final int OPTION_REQUEST_MODIFICAR = 1; //modificar día

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

       fabAnyadir.setOnClickListener(e->{
          Intent intent = new Intent(MainActivity.this, EdicionDiaActivity.class);
            startActivityForResult(intent,OPTION_REQUEST_NUEVA); //probando, main aun no realizado
        });

    }
    //Cargar el menu personalizado
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    public void iniciaViews(){
        fabAnyadir = findViewById(R.id.fabAnyadir);
        toolbar = findViewById(R.id.toolbar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED){
            DiaDiario diaDiario;
            diaDiario = (DiaDiario)data.getParcelableExtra(EdicionDiaActivity.EXTRA_EDICION_DIA);
            switch (requestCode){
                case OPTION_REQUEST_NUEVA:

                    break;
                case OPTION_REQUEST_MODIFICAR:
                    break;
            }
        }
    }
}