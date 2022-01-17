package net.iessochoa.joelsemperedura.practica5.ui;

import static net.iessochoa.joelsemperedura.practica5.model.DiaDiario.getValoracionEstaticaResumida;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.iessochoa.joelsemperedura.practica5.R;
import net.iessochoa.joelsemperedura.practica5.model.DiaDiario;
import net.iessochoa.joelsemperedura.practica5.viewmodels.DiarioViewModel;

import java.util.Date;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    public static final int OPTION_REQUEST_NUEVA = 0; //nuevo día
    public static final int OPTION_REQUEST_MODIFICAR = 1; //modificar día
    RecyclerView rvLista;
    // nos permite mantener los datos cuando se reconstruye la actividad
    private DiarioViewModel diarioViewModel;
    FloatingActionButton fabAnyadir;
    Toolbar toolbar;
    private SearchView svBusqueda;
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
        //Dos formas de visualizacion del RecyclerView segun su orientacion
        int orientation = getResources().getConfiguration().orientation;
        if(orientation== Configuration.ORIENTATION_PORTRAIT)//una fila
            rvLista.setLayoutManager(new LinearLayoutManager(this));
        else
            rvLista.setLayoutManager(new GridLayoutManager(this, 2));//2 es el num columnas
        //Evento swiper y manejo
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT |
                        ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        //realizamos un cast del viewHolder y obtenemos el dia borrar
                        DiaDiario diaDelete=((DiarioListAdapter.DiarioViewHolder)viewHolder).getDia();
                        borrarDia(diaDelete);

                        //Si cambia borra, si se le da a cancelar vuelve a dibujar
                        adapter.notifyDataSetChanged();

                        //final int posicion=viewHolder.getBindingAdapterPosition();
                        //adapter.notifyItemChanged(posicion);
                    }
                };

        //Creamos el objeto de ItemTouchHelper que se encargará del trabajo
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(simpleItemTouchCallback);
        //lo asociamos a nuestro reciclerView
        itemTouchHelper.attachToRecyclerView(rvLista);


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
        //Buscar diaDiarios
        //Asignar el observer a la busqueda hecha. si hay cambiaos actualizar adaptador
        diarioViewModel.getByResumen().observe(this, diaDiarios -> adapter.setDiarios(diaDiarios));
        //Cuando cambie el campo de busqueda, transformation.switchMap cambiara la condicion de busqueda del livedata
        svBusqueda.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Cuando el usuario pulsa intro enviamos la nueva consulta
                diarioViewModel.setCondicionBusqueda(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //comprobamos si el tamaño es cero porque el searchview no responde al intro cuando no hay texto
                if (newText.length() == 0)
                    diarioViewModel.setCondicionBusqueda("");
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.setDefaultValues(this,R.xml.root_preferences,false); //TODO : Prguntar como recuperar los valores del settings
        //Si la pantalla es grande establece una configuracion diferente
        opcionesPantallaXL();




    }

    //**********Averiguar tamaño pantalla*****************//
    private void opcionesPantallaXL() {
        int pantalla = (getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK);
        if ( (pantalla ==
                Configuration.SCREENLAYOUT_SIZE_LARGE) ||
                pantalla == Configuration.SCREENLAYOUT_SIZE_XLARGE ){

                    setTitle("Probando"); //TODO Obtener usuario establecido en la configuracion
        }
    }

    //*************************MENU****************************//
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }
    //*************************Opciones de los items del menu****************************//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //Ordenar
            case R.id.action_ordenar:
                ordenarDiaDiarios();
                return true;
                //Acerca de
            case R.id.action_about:
                FragmentManager fg = getSupportFragmentManager();
                DialogoAlerta dialogo = new DialogoAlerta();
                dialogo.show(fg,getString(R.string.stAcercaDe));
                return true;
                //Media valoracion vida
            case R.id.action_valoraVida:
                imgMediaValoracionVida();
                return true;
                //Muestra ultimo dia guardado
            case R.id.action_lastDay:
                    muestraUltimoDia();
                return true;
                //Ir a PreferenciasActivity
            case R.id.action_setting:
                Intent intent = new Intent(this,PreferenciasActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //*************************VIEWS****************************//
    public void iniciaViews(){
        fabAnyadir = findViewById(R.id.fabAnyadir);
        toolbar = findViewById(R.id.toolbar);
        rvLista = findViewById(R.id.rvLista);
        svBusqueda = findViewById(R.id.svBusqueda);
    }

    //*************************CRUD****************************//
    private void anyadirDia(DiaDiario diaDiario){
        diarioViewModel.insert(diaDiario);
    }
    private void modificarDia(DiaDiario diaDiario){
       diarioViewModel.update(diaDiario);
    }
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
    //TODO : Punto 8 Practica 5 parte 3 OPCIONAL HACER
    //Muestra ultimo dia guardado
    private void muestraUltimoDia() {
        //recuperamos las preferencias
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file),Context.MODE_PRIVATE);
        //recuperamos la fecha del últimos día editado
        long ultimoDia=sharedPref.getLong(getString(R.string.pref_key_ultimo_dia),0);
        //mostramos el resultado
        Toast.makeText(this,getString(R.string.stUltimoDia)+" "+DiaDiario.getFechaEstaticaFormatoLocal(new Date(ultimoDia)),Toast.LENGTH_LONG).show();
    }

    //********METODO QUE NOS DEVUELVE LA IMAGEN DE LA MEDIA DE LOS DIADIARIOS******//
    private void imgMediaValoracionVida() {
        diarioViewModel.getMediaValoracionDias() //Obtenemos el objeto reactivo de un solo uso para la consulta en segundo plano en un hilo
                .subscribeOn(Schedulers.io())//el observable(la consulta sql) se ejecuta en uno diferente
                .observeOn(AndroidSchedulers.mainThread()) //indicamos que el observador es el hilo principal  de Android
                .subscribe(new SingleObserver<Float>() { //Creamos el observador
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {

                    }
                    @Override //cuando termine  la consulta de la base de datos recibimos el valor
                    public void onSuccess(@NonNull Float aFloat) {
                        int resultado = Math.round(aFloat); //redondeo el valor recibido
                        ImageView imagen = new ImageView(MainActivity.this);
                        switch(getValoracionEstaticaResumida(resultado)){ //se lo paso a un metodo estatico del pojo
                            case 1:
                                imagen.setImageResource(R.drawable.ic_gr_sad); //segun devolucion asigno un drawable
                                break;
                            case 2:
                                imagen.setImageResource(R.drawable.ic_gr_neu);
                                break;
                            case 3:
                                imagen.setImageResource(R.drawable.ic_gr_smi);
                                break;
                        }
                        //Dialogo con imagen
                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(MainActivity.this).
                                        setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setView(imagen);
                        builder.create().show();
                    }
                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }
                });
    }
    private void ordenarDiaDiarios() {
        //Abrir un dialogo
        // alertSimpleListView();
        // ********DIALOGO*********//
        final CharSequence[] items = getResources().getStringArray(R.array.action_ordenar);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getResources().getString(R.string.stOrdenar));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item){
                    case 0:
                        diarioViewModel.setOrdenadoPor(DiarioViewModel.POR_FECHA);
                        break;
                    case 1:
                        diarioViewModel.setOrdenadoPor(DiarioViewModel.POR_VALORACION);
                        break;
                    case 2:
                        diarioViewModel.setOrdenadoPor(DiarioViewModel.POR_RESUMEN);
                        break;
                }
                dialog.dismiss();

            }
        }).show();
    }
}