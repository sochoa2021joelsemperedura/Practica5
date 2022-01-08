package net.iessochoa.joelsemperedura.practica5.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
//TODO hacer la base de datos
@Entity(tableName = DiaDiario.TABLE_NAME,
        indices = {@Index(value = {DiaDiario.FECHA},unique = true)})
public class DiaDiario implements Parcelable {
    /*
    POJO que mantendra la informacion de un dia del diario.
     */

    public static final String TABLE_NAME = "diario";
    public static final String ID = BaseColumns._ID;
    public static final String FECHA ="fecha";
    public static final String VALORACION_DIA = "valoracion_dia";
    public static final String RESUMEN = "resumen";
    public static final String CONTENIDO = "contenido";
    public static final String FOTO_URI = "foto_uri";

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = ID)
    private int id; //clave del dia

    @ColumnInfo(name = FECHA)
    @NonNull
    private Date fecha; //dia del diario, UNIQUE

    @ColumnInfo(name = VALORACION_DIA)
    private int valoracionDia; //entero 0 - 10

    @ColumnInfo(name = RESUMEN)
    @NonNull
    private String resumen; //Breve resumen del dia

    @ColumnInfo(name = CONTENIDO)
    @NonNull
    private String contenido; //el contenido escrito por el usuario en el diario

    @ColumnInfo(name = FOTO_URI)
    @NonNull
    private String fotoUri; //imagen representativa del dia


    //*************CONSTRUCTORES*************//

    /*
    public DiaDiario(Date fecha, int valoracionDia, String resumen, String contenido, String fotoUri) {
        this.fecha = fecha;
        this.valoracionDia = valoracionDia;
        this.resumen = resumen;
        this.contenido = contenido;
        this.fotoUri = fotoUri;
    }
     */

    //Room solo nos permite el uso de un constructor
    public DiaDiario(@NonNull Date fecha, int valoracionDia,@NonNull String resumen,@NonNull String contenido) {
        this.fecha = fecha;
        this.valoracionDia = valoracionDia;
        this.resumen = resumen;
        this.contenido = contenido;
        this.fotoUri = ""; //cadena vacia
    }

    //*********GETTER**&**SETTER*********//


    protected DiaDiario(Parcel in) {
        id = in.readInt();
        valoracionDia = in.readInt();
        resumen = in.readString();
        contenido = in.readString();
        fotoUri = in.readString();
    }

    public static final Creator<DiaDiario> CREATOR = new Creator<DiaDiario>() {
        @Override
        public DiaDiario createFromParcel(Parcel in) {
            return new DiaDiario(in);
        }

        @Override
        public DiaDiario[] newArray(int size) {
            return new DiaDiario[size];
        }
    };

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Date getFecha() {
        return fecha;
    }
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    public int getValoracionDia() {
        return valoracionDia;
    }
    public void setValoracionDia(int valoracionDia) {
        this.valoracionDia = valoracionDia;
    }
    public String getResumen() {
        return resumen;
    }
    public void setResumen(String resumen) {
        this.resumen = resumen;
    }
    public String getContenido() {
        return contenido;
    }
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
    public String getFotoUri() {
        return fotoUri;
    }
    public void setFotoUri(String fotoUri) {
        this.fotoUri = fotoUri;
    }

    //***************PARCELABLE***************//

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(valoracionDia);
        dest.writeString(resumen);
        dest.writeString(contenido);
        dest.writeString(fotoUri);
    }

    //*********OTROS**METODOS*********//

    //metodo que devuelve un numero segun la valoracion del dia (3 opciones)
    public int getValoracionResumida(){
        int devolucion;
        if (valoracionDia < 5) {
            devolucion = 1;
        } else if (valoracionDia < 8) {
            devolucion = 2;
        } else {
            devolucion = 3;
        }
        return devolucion;
    }

    //metodo igual que el anterior, solo que es estatico y recibe un entero como parametro
    public static int getValoracionEstaticaResumida(int numero){
        int devolucion;
        if (numero < 5) {
            devolucion = 1;
        } else if (numero < 8) {
            devolucion = 2;
        } else {
            devolucion = 3;
        }
        return devolucion;
    }

    //Devolucion de fecha en funcion del idioma configurado en el sistema
    public String getFechaFormatoLocal(){
        DateFormat df = DateFormat.getDateInstance(
                DateFormat.MEDIUM, Locale.getDefault()
        );
        return df.format(fecha);
    }
    public static String getFechaEstaticaFormatoLocal(Date date){
        DateFormat df = DateFormat.getDateInstance(
                DateFormat.MEDIUM, Locale.getDefault()
        );
        return df.format(date);
    }

}
