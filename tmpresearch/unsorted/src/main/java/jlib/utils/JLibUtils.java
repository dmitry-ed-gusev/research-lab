package jlib.utils;

import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Gusev Dmitry (√усев ƒмитрий)
 * @version 1.0 (DATE: 22.06.11)
*/

public final class JLibUtils
 {
  public static enum DatePeriod {YEAR, MONTH, DAY}

  private static Logger logger = Logger.getLogger(JLibUtils.class.getName());

  //
  private JLibUtils() {}

  /***/
  public static String trans(String name)
   {
    StringBuilder name_trans = new StringBuilder("");
        char name_char;
        int index_c;
        String rus = "јаЅб¬в√гƒд≈е∆ж«з»и…й кЋлћмЌнќоѕп–р—с“т”у‘ф’х÷ц„чЎшўщЏъџы№ьЁэёюя€";
        String[] eng = {"A", "a", "B", "b", "V", "v", "G", "g", "D", "d", "E", "e", "ZH", "zh", "Z", "z", "I", "i", "Y", "y", "K", "k", "L", "l",
                "M", "m", "N", "n", "O", "o", "P", "p", "R", "r", "S", "s", "T", "t", "U", "u", "F", "f", "KH", "kh", "TS", "ts", "CH", "ch",
                "SH", "sh", "SHCH", "shch", "Y", "y", "Y", "y", "Y", "y", "E", "e", "YU", "yu", "YA", "ya"};

        if (name != null && name.length() > 0) {
            for (int i = 0; i < name.length(); i++) {
                name_char = name.charAt(i);
                index_c = rus.indexOf(name_char);
                if (index_c > -1) {
                    name_trans.append(eng[index_c]);
                } else {
                    name_trans.append(name_char);
                }
            }
        } else {
            name_trans.append("");
        }
        return name_trans.toString();
    }

  /**ѕреобразование строковой даты в строку определенного формата
     * @param date - дата в виде строки
     * @param inPattern - формат даты dateTime, например: ddMMyy
     * @param outPattern - шаблон даны дл€ преобразовани€, например: dd.MM.yyyy
     * @param defaultValue - возвращаемое значение по умолчанию, если не удастс€ преобразовать дату
     * @return - строкова€ дата отформатированна€ по шаблону*/
    public static String dateStrToPattern(String date, String inPattern, String outPattern, String defaultValue){

        String returnDate = defaultValue;

        if(date != null && !date.trim().equals("")){
            java.text.SimpleDateFormat df = new java.text.SimpleDateFormat(inPattern);
            java.text.SimpleDateFormat df2 = new java.text.SimpleDateFormat(outPattern);

            try {
                returnDate = df2.format(df.parse(date));

            } catch (Exception e) {e.getMessage();}
        }

        return returnDate;
    }


   /**ѕреобразование даты (java.sql.Date) в строку определенного формата
     * @param date - дата
     * @param outPattern - шаблон даны дл€ преобразовани€, например: dd.MM.yyyy
     * @param defaultValue - возвращаемое значение по умолчанию, если не удастс€ преобразовать дату
     * @return - строкова€ дата отформатированна€ по шаблону*/
    public static String dateToPattern(java.sql.Date date, String outPattern, String defaultValue){

        String returnDate = defaultValue;

        if(date != null){

            java.text.SimpleDateFormat df2 = new java.text.SimpleDateFormat(outPattern);

            try {
                returnDate = df2.format(date);

            } catch (Exception e) {e.getMessage();}
        }

        return returnDate;
    }

    /**ѕреобразование даты (java.util.Date) в строку определенного формата
     * @param date - дата
     * @param outPattern - шаблон даны дл€ преобразовани€, например: dd.MM.yyyy
     * @param defaultValue - возвращаемое значение по умолчанию, если не удастс€ преобразовать дату
     * @return - строкова€ дата отформатированна€ по шаблону*/
    public static String dateToPattern(java.util.Date date, String outPattern, String defaultValue){

        String returnDate = defaultValue;

        if(date != null){

            java.text.SimpleDateFormat df2 = new java.text.SimpleDateFormat(outPattern);

            try {
                returnDate = df2.format(date);

            } catch (Exception e) {e.getMessage();}
        }

        return returnDate;
    }

   /**ѕреобразование строковой даты  в дату типа java.util.Date, с возможностью изменени€ на N-ое количество дней/мес€цев/лет
     * @param date - дата
     * @param inPattern - формат даты date, например: ddMMyy
     * @param period - количество дней/мес€цев/лет на которые требуетс€ перенести дату
     * @param typePeriod - тип периода: день/мес€ц/год
     * @return -  дата типа java.util.Date*/
    public static Date dateToPeriod(String date, String inPattern, int period, DatePeriod typePeriod) throws ParseException
     {return JLibUtils.dateToPeriod(new SimpleDateFormat(inPattern).parse(date), period, typePeriod);}

    /**»зменение на N-ое количество дней/мес€цев/лет даты типа java.util.Date
     * @param date - дата
     * @param period - количество дней/мес€цев/лет на которые требуетс€ перенести дату
     * @param typePeriod - тип периода: день/мес€ц/год
     * @return -  дата типа java.util.Date*/
    public static Date dateToPeriod(Date date, int period, DatePeriod typePeriod){

        Date returnDate = null;

        if(date != null){
                Calendar cal = new GregorianCalendar();
                cal.setTime(date);

                if(DatePeriod.YEAR.equals(typePeriod))       {cal.add(Calendar.YEAR, period);}
                else if(DatePeriod.MONTH.equals(typePeriod)) {cal.add(Calendar.MONTH, period);}
                else if(DatePeriod.DAY.equals(typePeriod))   {cal.add(Calendar.DATE, period);}

                returnDate = cal.getTime();

        }
        else {logger.warn("Input data is NULL!");}
        return returnDate;
    }
 }