package net.iessochoa.joelsemperedura.practica5.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.iessochoa.joelsemperedura.practica5.model.DiaDiario;
import net.iessochoa.joelsemperedura.practica5.repository.DiarioRepository;

import java.util.List;
//TODO punto 12 parte 2 , define los metodos necesarios y las propiedades para viewModel, si falta algo completar
public class DiarioViewModel extends AndroidViewModel {
    private DiarioRepository mRepository;
    private LiveData<List<DiaDiario>> mAllDiarios;


    public DiarioViewModel(@NonNull Application application) {
        super(application);

        mRepository=DiarioRepository.getInstance(application);
        //Recuperamos el LiveData de todos los diasDiarios
        mAllDiarios=mRepository.getAllDiarios();

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
    //todo revisar
    public void update(DiaDiario diaDiario){
            mRepository.update(diaDiario);

    }

}
