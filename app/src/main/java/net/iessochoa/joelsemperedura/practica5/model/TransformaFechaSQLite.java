package net.iessochoa.joelsemperedura.practica5.model;

import androidx.room.TypeConverter;

import java.util.Calendar;
import java.util.Date;

/**
 * SQLite no tiene campo de tipo fecha. Room, mediante esta clase, nos transformará automáticamente
 * los datos de tipo Date a Long. Para ello tenemos que indicarlo en la clase DiarioDatabase
 */

public class TransformaFechaSQLite {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }
    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        //le quitamos la hora, pero si la necesitarais, no llaméis a removeTime
        return date == null ? null : removeTime(date).getTime();
    }
    public static Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

}
