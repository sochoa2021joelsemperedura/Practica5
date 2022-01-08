package net.iessochoa.joelsemperedura.practica5.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import net.iessochoa.joelsemperedura.practica5.model.DiaDiario;
import net.iessochoa.joelsemperedura.practica5.model.DiarioDao;
import net.iessochoa.joelsemperedura.practica5.model.DiarioDatabase;

import java.util.List;

import io.reactivex.Single;

public class DiarioRepository {
    //Implementamos Singleton
    private static volatile DiarioRepository INSTANCE;

    private DiarioDao mDiarioDao;
    private LiveData<List<DiaDiario>> mAllDiarios;

    //singleton
    public static DiarioRepository getInstance(Application application){
        if (INSTANCE == null) {
            synchronized (DiarioRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE=new DiarioRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    private DiarioRepository(Application application){
        //Creamos la base de datos
        DiarioDatabase db= DiarioDatabase.getDatabase(application);
        //Recuperamos el DAO necesario para el CRUD de la base de datos
        mDiarioDao = db.diarioDao();
        //Recuperamos la lista como un LiveData
        mAllDiarios = mDiarioDao.getAllDiario();
    }
    //Devuelve todos
    public LiveData<List<DiaDiario>> getAllDiarios() {
        return mAllDiarios;
    }
    //Devuelve ordenado por resumen
    public LiveData<List<DiaDiario>> getOrderBy(String resumen) {
        mAllDiarios=mDiarioDao.getDiarioOrderBy(resumen);
        return mAllDiarios;
    }
    //Devuelve valoracion media de los dias
    public Single<Float>getValoracionMediaTotal(){
        return mDiarioDao.getValoracionTotal();
    }

    /*
    Insert: Nos obliga a crear tarea en segundo plano
     */
    public void insert(DiaDiario diaDiario){
        //administracion con el Executor
        DiarioDatabase.databaseWriteExecutor.execute(()->{
            mDiarioDao.insert(diaDiario);
        });
    }
    /*
    Delete: Nos obliga a crear tarea en segundo plano
     */
    public void delete(DiaDiario diaDiario){
        //administracion con el Executor
        DiarioDatabase.databaseWriteExecutor.execute(()->{
            mDiarioDao.deleteByDiaDiario(diaDiario);
        });
    }
    //update
    public void update(DiaDiario diaDiario){
        DiarioDatabase.databaseWriteExecutor.execute(()->{
            mDiarioDao.update(diaDiario);
        });
    }
    //TODO deleteAll

}
