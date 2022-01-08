package net.iessochoa.joelsemperedura.practica5.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface DiarioDao {

    //Nuevo dia sustituyendo si ya existe
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DiaDiario diaDiario);

    @Delete
    void deleteByDiaDiario(DiaDiario diaDiario);

    @Update
    void update(DiaDiario diaDiario);

    @Query("DELETE FROM "+DiaDiario.TABLE_NAME)
    void deleteAll(); //Borra el diario completo

    @Query("SELECT * FROM "+DiaDiario.TABLE_NAME)
    LiveData<List<DiaDiario>>getAllDiario(); //Nos devuelve el diario completo

    @Query("SELECT * FROM "+DiaDiario.TABLE_NAME+" WHERE "+DiaDiario.RESUMEN+" LIKE '%' || :resumen || '%'")
    LiveData<List<DiaDiario>>getDiarioOrderBy(String resumen); //Nos devuelve el diario con aquellos dias que contienen la palabra "resumen" en el campo Resumen del diaDiario

    @Query("SELECT AVG("+DiaDiario.VALORACION_DIA+") FROM "+DiaDiario.TABLE_NAME)
    Single<Float>getValoracionTotal(); //Nos devuelve el AVG de la valoracion de todos los dias en un objeto observable(rxJava).Aun por ver

}
