package net.iessochoa.joelsemperedura.practica5.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import net.iessochoa.joelsemperedura.practica5.model.DiaDiario;
import net.iessochoa.joelsemperedura.practica5.repository.DiarioRepository;

import java.util.List;

public class DiarioViewModel extends AndroidViewModel {
    private DiarioRepository mRepository;
    //Lista devuelta mediante Room por sql
    private LiveData<List<DiaDiario>> mAllDiarios;
    //Lista mutable que depende de otros elementos
    private MutableLiveData<String>condicionBusquedaLiveData;


    public DiarioViewModel(@NonNull Application application) {
        super(application);
        mRepository=DiarioRepository.getInstance(application);
        //Recuperamos el LiveData de todos los diasDiarios
        mAllDiarios=mRepository.getAllDiarios();

        //Este Livedata estará asociado al editext de busqueda por nombre
        condicionBusquedaLiveData=new MutableLiveData<String>();
        //en el primer momento no hay condición
        condicionBusquedaLiveData.setValue("");

        //switchMap nos me permite cambiar el livedata de la consulta SQL
        // al modificarse la consulta de busqueda(cuando cambia condicionBusquedaLiveData)
        mAllDiarios=Transformations.switchMap(condicionBusquedaLiveData,
                resumen -> mRepository.getOrderBy(resumen));


    }

    public LiveData<List<DiaDiario>> getAllDiarios() {
        return mAllDiarios;
    }
    //Insercion y borrado que se reflejara automaticamente gracias al observador creado en la actividad
    public void insert(DiaDiario diaDiario){
        mRepository.insert(diaDiario);
    }
    public void delete(DiaDiario diaDiario){
        mRepository.delete(diaDiario);
    }
    public void update(DiaDiario diaDiario){ mRepository.update(diaDiario); }

    //todo revisar
    public LiveData<List<DiaDiario>> getByResumen()
    {
        return mAllDiarios;
    }
    public void setCondicionBusqueda(String condicionBusqueda) {
        condicionBusquedaLiveData.setValue(condicionBusqueda);
    }

}
