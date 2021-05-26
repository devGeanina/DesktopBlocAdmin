package blocadmin.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Utils {
    
    public static final String DB_URL_PREF = "dbURL";
    public static final String DB_USER_PREF = "dbUser";
    public static final String DB_PASS_PREF = "dbPassword";
    public static final String DATE_TIME_FORMAT = "dd-MM-yyyy";
    public static final String APP_VERSION = "1.0";
    
    public static java.sql.Date getDateSql(Date utilDate){
        Calendar cal = Calendar.getInstance();
        cal.setTime(utilDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);    
        java.sql.Date sqlDate = new java.sql.Date(cal.getTime().getTime());
        return sqlDate;
    }
    
    public static String convertDateToString(Date date) {
        Calendar cal = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
        cal.setTime(date);
        return sdf.format(date);
    }
}
