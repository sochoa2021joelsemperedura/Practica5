package net.iessochoa.joelsemperedura.practica5.ui;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import net.iessochoa.joelsemperedura.practica5.R;
import net.iessochoa.joelsemperedura.practica5.model.DiaDiario;

import java.util.Calendar;
import java.util.Date;

public class EdicionDiaActivity extends AppCompatActivity {
    public static String EXTRA_EDICION_DIA = "EdicionDiaActivity.diaDiario"; // el objeto diaDiario que llega
    private static final int STATUS_CODE_SELECCION_IMAGEN = 300;
    private static final int MY_PERMISSIONS = 100;

    DiaDiario diaDiario;
    Intent iBack; //Devolver a la actividad principal el intent

    Spinner spValoracion;
    Button btnFecha;
    TextView tvFecha;
    Button btnGuardar;
    EditText etResumenBreve;
    EditText etDiarioTexto;
    Button btnImagen;
    ImageView ivImagenDia;
    ConstraintLayout clPrincipal;

    Date newFecha;
    private Uri uriFoto = null;

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
            uriFoto = Uri.parse(diaDiario.getFotoUri()); //uriFoto recibe el uri almacenado en diaDiario
            muestraFoto(); //se carga en el ImageView gracias al metodo Glide

        }
        //permite ocultar teclado
        ocultarTeclado();
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
        //Boton imagen
        btnImagen.setOnClickListener(e->{
            if (noNecesarioSolicitarPermisos()){
                muestraOpcionesImagen();
            }else{

            }

        });
    }

    //**********PERMISOS USUARIO*************//
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]
            permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions,
                grantResults);
        if (requestCode == MY_PERMISSIONS) {
            if (grantResults.length == 2 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED && grantResults[1] ==
                    PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.stPermisosAceptados),
                        Toast.LENGTH_SHORT).show();
                muestraOpcionesImagen();
            } else {//si no se aceptan los permisos
                muestraExplicacionDenegacionPermisos();
            }
        }
    }

    //*********VERIFICA SI ES UN DIA NUEVO O NO Y LO GUARDA COMO DIADIARIO*************//
    private void guardarDia() {
        if (diaDiario == null){
            //Guardar las preferencias del dia antes de salir
            guardarDiaPreferencias(newFecha);
            //si es un dia nuevo
            DiaDiario newDia = new DiaDiario(
                    newFecha,Integer.parseInt(spValoracion.getSelectedItem().toString()),
                    etResumenBreve.getText().toString(),etDiarioTexto.getText().toString());
             iBack.putExtra(EXTRA_EDICION_DIA,newDia);

            if (uriFoto != null){
                newDia.setFotoUri(String.valueOf(uriFoto)); //si el usuario ha elegido una foto entonces la establecemos en el objeto diaDiario
            }
         }else{ //si ya existe
            diaDiario.setContenido(etDiarioTexto.getText().toString());
            //asi me quito un error
            if (diaDiario.getFecha() != null){
                diaDiario.setFecha(diaDiario.getFecha());
            }
            diaDiario.setFotoUri(String.valueOf(uriFoto)); //Asigna la uri de la imagen a mostrar
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

    //Metodo abrir galeria
    private void elegirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent.createChooser(intent, getString(R.string.stSeleccioneImagen)), STATUS_CODE_SELECCION_IMAGEN);
    }

    //Asignar imagen en el metodo
    private void muestraFoto(){
        Glide.with(this)
                .load(uriFoto) // Uri of the picture
                .into(ivImagenDia);//imageView
    }
    //Muestra las opciones para la imagen
    private void muestraOpcionesImagen() {
        final CharSequence[] option = {getString(R.string.stTomarFoto),
                getString(R.string.stElegirGaleria), getString(android.R.string.cancel)};
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(android.R.string.dialog_alert_title);
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        //TODO : abrirCamara();//opcional p4
                        break;
                    case 1:
                        elegirGaleria();
                        break;
                }
                dialog.dismiss();
            }
        });
        builder.show();
    }
    //si devuelve false hay que pedir los permisos
    //Comprueba que se han establecido los permisos requeridos para el correcto funcionamiento de las opciones que la aplicacion nos proporciona
    private boolean noNecesarioSolicitarPermisos() {
        //si la versión es inferior a la 6
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;
        //comprobamos si tenemos los permisos
        if ((checkSelfPermission(WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(CAMERA) ==
                        PackageManager.PERMISSION_GRANTED))
            return true;
        //indicamos al usuario porqué necesitamos los permisos siempre que no haya indicado que no lo volvamos a hacer
        if ((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) ||
                (shouldShowRequestPermissionRationale(CAMERA))) {
            Snackbar.make(clPrincipal, getString(R.string.stPedirPermisos),
                    Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok,
                    new View.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onClick(View v) {
                            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,
                                    CAMERA}, MY_PERMISSIONS);
                        }
                    }).show();
        } else {//pedimos permisos sin indicar el porqué
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA},
                    MY_PERMISSIONS);
        }
        return false;//necesario pedir permisos
    }
    /**
     * Si se deniegan los permisos mostramos las opciones de la aplicación
     para que el usuario acepte los permisos
     */
    private void muestraExplicacionDenegacionPermisos() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.stPeticionPermisos));
                builder.setMessage(getString(R.string.stRequerirPermisos));
        builder.setPositiveButton(android.R.string.ok, new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();

                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        builder.show();
    }
    /**
     * Permite ocultar el teclado
     */
    private void ocultarTeclado() {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        // mgr.showSoftInput(etDatos, InputMethodManager.HIDE_NOT_ALWAYS);
        if (imm != null) {
            imm.hideSoftInputFromWindow(etResumenBreve.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(etDiarioTexto.getWindowToken(), 0);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED){
            switch (requestCode){
                case STATUS_CODE_SELECCION_IMAGEN:
                    uriFoto = data.getData();
                    muestraFoto(); //muestra la imagen
                    break;
            }
        }
    }


    public void iniciaViews(){
        clPrincipal = findViewById(R.id.clPrincipal);
        spValoracion = findViewById(R.id.spValoracion);
        btnFecha = findViewById(R.id.btnFecha);
        tvFecha = findViewById(R.id.tvFecha);
        btnGuardar = findViewById(R.id.btnGuardar);
        etResumenBreve = findViewById(R.id.etResumenBreve);
        etDiarioTexto = findViewById(R.id.etDiarioTexto);
        btnImagen = findViewById(R.id.btnImagen);
        ivImagenDia = findViewById(R.id.ivImagenDia);
    }

}