package net.iessochoa.joelsemperedura.practica5.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import net.iessochoa.joelsemperedura.practica5.model.DiaDiario;
import net.iessochoa.joelsemperedura.practica5.repository.DiarioRepository;

import java.util.List;

import io.reactivex.Single;

public class DiarioViewModel extends AndroidViewModel {
    //Opciones para ordenar
    public static final String POR_FECHA = DiaDiario.FECHA;
    public static final String POR_VALORACION = DiaDiario.VALORACION_DIA;
    public static final String POR_RESUMEN = DiaDiario.RESUMEN;

    private DiarioRepository mRepository;
    //Lista devuelta mediante Room por sql
    private LiveData<List<DiaDiario>> mAllDiarios;
    //Lista mutable que depende de otros elementos
    private MutableLiveData<String>condicionBusquedaLiveData;

    //Lista ordenada por..
    private LiveData<List<DiaDiario>> diariosOrderByFechaLiveData;
    private LiveData<List<DiaDiario>> diariosOrderByValoracionLiveData;
    private LiveData<List<DiaDiario>> diariosOrderByResumenLiveData;
    //Mantener el orden actual
    private String ordenadoPor = POR_RESUMEN;

    //Mediator asigna un livedata u otro en funcion del orden seleccionado
    private MediatorLiveData<List<DiaDiario>> listaDiariosMediatorLiveData;

    public DiarioViewModel(@NonNull Application application) {
        super(application);
        mRepository=DiarioRepository.getInstance(application);

        //Recuperamos el LiveData de todos los diasDiarios
        mAllDiarios=mRepository.getAllDiarios();

        //Recuperamos las listas ordenadas por
        diariosOrderByFechaLiveData = mRepository.getDiariosOrderByFecha();
        diariosOrderByValoracionLiveData = mRepository.getDiariosOrderByValoracion();
        diariosOrderByResumenLiveData = mRepository.getDiariosOrderByResumen();

        listaDiariosMediatorLiveData = new MediatorLiveData<>();

        //OPCIONES POSIBLES DEL MEDIATOR
        listaDiariosMediatorLiveData.addSource(diariosOrderByFechaLiveData, new Observer<List<DiaDiario>>() {
            @Override
            public void onChanged(List<DiaDiario> diaDiarios) {
                //como cuando se añada o se elimine un elemento afecta a las dos listas SQL, asignamos la que
                //corresponda a la actual, ya que se ejecutarán los dos eventos
                if (ordenadoPor.equals(POR_FECHA))
                    listaDiariosMediatorLiveData.setValue(diaDiarios);
            }
        });
        listaDiariosMediatorLiveData.addSource(diariosOrderByResumenLiveData, new Observer<List<DiaDiario>>() {
            @Override
            public void onChanged(List<DiaDiario> diaDiarios) {
                if (ordenadoPor.equals(POR_RESUMEN))
                    listaDiariosMediatorLiveData.setValue(diaDiarios);
            }
        });
        listaDiariosMediatorLiveData.addSource(diariosOrderByValoracionLiveData, new Observer<List<DiaDiario>>() {
            @Override
            public void onChanged(List<DiaDiario> diaDiarios) {
                if (ordenadoPor.equals(POR_VALORACION))
                    listaDiariosMediatorLiveData.setValue(diaDiarios);
            }
        });


        //Este Livedata estará asociado al editext de busqueda por nombre
        condicionBusquedaLiveData=new MutableLiveData<String>();
        //en el primer momento no hay condición
        condicionBusquedaLiveData.setValue("");

        //switchMap nos me permite cambiar el livedata de la consulta SQL
        // al modificarse la consulta de busqueda(cuando cambia condicionBusquedaLiveData)
        mAllDiarios=Transformations.switchMap(condicionBusquedaLiveData,
                resumen -> mRepository.getOrderBy(resumen));


    }

    public MediatorLiveData<List<DiaDiario>> getAllDiarios() {
        return listaDiariosMediatorLiveData;
    }
    //Insercion y borrado que se reflejara automaticamente gracias al observador creado en la actividad
    public void insert(DiaDiario diaDiario){
        mRepository.insert(diaDiario);
    }
    public void delete(DiaDiario diaDiario){
        mRepository.delete(diaDiario);
    }
    public void update(DiaDiario diaDiario){ mRepository.update(diaDiario); }

    /*
    rxJava: Este método nos permite recuperar el total de contactos con un observable de la
    libreria RXJava. La clase Single nos permite una única observación, es suficiente para una consulta
    única como es nuestro caso
     */
    public Single<Float> getMediaValoracionDias(){
        return mRepository.getValoracionMediaTotal();
    }

    /**
     * Nos permite asignar el orden actual de la lista
     * @param ordenActual
     */
    public void setOrdenadoPor(String ordenActual) {
        ordenadoPor = ordenActual;
        if (ordenadoPor.equals(POR_RESUMEN)) {
            listaDiariosMediatorLiveData.setValue(diariosOrderByResumenLiveData.getValue());
        }else if (ordenadoPor.equals(POR_VALORACION)) {
            listaDiariosMediatorLiveData.setValue(diariosOrderByValoracionLiveData.getValue());
        }else if (ordenadoPor.equals(POR_FECHA)){
            listaDiariosMediatorLiveData.setValue(diariosOrderByFechaLiveData.getValue());
        }
    }

    public LiveData<List<DiaDiario>> getByResumen()
    {
        return mAllDiarios;
    }
    public void setCondicionBusqueda(String condicionBusqueda) {
        condicionBusquedaLiveData.setValue(condicionBusqueda);
    }

}
