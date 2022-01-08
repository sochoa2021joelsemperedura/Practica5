package net.iessochoa.joelsemperedura.practica5.model;

/*
Clase que nos comunica con SQLite.
Patron Singleton
 */

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//TODO si no exporta los datos a la base de datos, el error estara por aqui
//Indicamos las entidades de la base de datos y la versión
@Database(entities = {DiaDiario.class},version = 1)
//Nos transforma las fecha a entero AUTO
@TypeConverters({TransformaFechaSQLite.class})
public abstract class DiarioDatabase extends RoomDatabase {

    //Acceso a los metodos CRUD
    public abstract DiarioDao diarioDao();

    //La base de datos
    private static volatile DiarioDatabase INSTANCE;

    /*
    para el manejo de base de datos con room necesitamos realzar las tareas CRUD en hilos,
    las consultas Select que devuelve LiveData, Room crea los hilos automáticamente, pero para las
    insercciones, acutalizaciones y borrado, tenemos que crear el hilo nosotros
    Utilizaremos ExecutorService para el control de los hilos.
     */

    private static final int NUMBER_OF_THREADS = 4;

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static DiarioDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (DiarioDatabase.class){
                if (INSTANCE == null){
                    //crear base de datos aqui
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            //nombre del fichero de la base de datos
                            DiarioDatabase.class,"diario_database")
                            //nos permite realizar tareas cuando es nueva o se ha creado una nueva version del programa
                            .addCallback(sRoomDatabaseCallback) //para ejecutar al crear o al abrir
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    //Crearemos una tarea en segundo plano que nos permite cargar los datos de ejemplo una vez se abra la base de datos
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db){
            super.onCreate(db);
            //creamos algunos contactos en un hilo
            databaseWriteExecutor.execute(()->{
                //Obtener base de datos
                DiarioDao mDao= INSTANCE.diarioDao();
                SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd-MM-yyyy");
                DiaDiario diaDiario = null;

                //creamos algunos dias, almenos 5
                try{
                    //1
                    diaDiario = new DiaDiario(formatoDelTexto.parse("12-3-2020"), 5, "Un día un poco aburrido, solo he visto Netflix",
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam rutrum lectus vestibulum, consectetur urna vel, rutrum tortor. Phasellus at leo nibh. Pellentesque lacinia blandit dui eu aliquam. Cras et suscipit nibh. " +
                                    "Cras vehicula lobortis ante, vel hendrerit diam convallis at. Nullam egestas vel dui sed tincidunt. " +
                                    "In placerat ac mauris eu faucibus. Nullam eu pretium justo. Suspendisse in leo nisi. Nulla hendrerit erat a finibus egestas. Nulla et libero eu purus euismod maximus.");
                            mDao.insert(diaDiario);
                    //2
                    diaDiario = new DiaDiario(formatoDelTexto.parse("13-3-2020"), 5, "Estoy haciendo esto mientras mi mujer cocina",
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam rutrum lectus vestibulum, consectetur urna vel, rutrum tortor. Phasellus at leo nibh. Pellentesque lacinia blandit dui eu aliquam. Cras et suscipit nibh. " +
                                    "Cras vehicula lobortis ante, vel hendrerit diam convallis at. Nullam egestas vel dui sed tincidunt. " +
                                    "In placerat ac mauris eu faucibus. Nullam eu pretium justo. Suspendisse in leo nisi. Nulla hendrerit erat a finibus egestas. Nulla et libero eu purus euismod maximus.");
                    mDao.insert(diaDiario);
                    //3
                    diaDiario = new DiaDiario(formatoDelTexto.parse("14-3-2020"), 8, "Si hoy termino esto mañana podre descansar un poco",
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam rutrum lectus vestibulum, consectetur urna vel, rutrum tortor. Phasellus at leo nibh. Pellentesque lacinia blandit dui eu aliquam. Cras et suscipit nibh. " +
                                    "Cras vehicula lobortis ante, vel hendrerit diam convallis at. Nullam egestas vel dui sed tincidunt. " +
                                    "In placerat ac mauris eu faucibus. Nullam eu pretium justo. Suspendisse in leo nisi. Nulla hendrerit erat a finibus egestas. Nulla et libero eu purus euismod maximus.");
                    mDao.insert(diaDiario);
                    //4
                    diaDiario = new DiaDiario(formatoDelTexto.parse("15-3-2020"), 3, "No me aguanto de la que llevo encima",
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam rutrum lectus vestibulum, consectetur urna vel, rutrum tortor. Phasellus at leo nibh. Pellentesque lacinia blandit dui eu aliquam. Cras et suscipit nibh. " +
                                    "Cras vehicula lobortis ante, vel hendrerit diam convallis at. Nullam egestas vel dui sed tincidunt. " +
                                    "In placerat ac mauris eu faucibus. Nullam eu pretium justo. Suspendisse in leo nisi. Nulla hendrerit erat a finibus egestas. Nulla et libero eu purus euismod maximus.");
                    mDao.insert(diaDiario);
                    //5
                    diaDiario = new DiaDiario(formatoDelTexto.parse("12-3-2020"), 2, "Tengo mucha hambre",
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam rutrum lectus vestibulum, consectetur urna vel, rutrum tortor. Phasellus at leo nibh. Pellentesque lacinia blandit dui eu aliquam. Cras et suscipit nibh. " +
                                    "Cras vehicula lobortis ante, vel hendrerit diam convallis at. Nullam egestas vel dui sed tincidunt. " +
                                    "In placerat ac mauris eu faucibus. Nullam eu pretium justo. Suspendisse in leo nisi. Nulla hendrerit erat a finibus egestas. Nulla et libero eu purus euismod maximus.");
                    mDao.insert(diaDiario);

                }catch (ParseException e){
                    e.printStackTrace();
                }
            });
        }
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db){
            super.onOpen(db);
            //si queremos hacer algo cuando se abre
        }
    };

}
