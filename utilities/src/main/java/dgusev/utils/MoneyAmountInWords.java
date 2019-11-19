package dgusev.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.math.BigDecimal;
 
/**
 * Process money amount (digits) and write it in words (russian).
 * @author Gusevd D.
 */

// todo: move message to constant
// todo: maybe change "00 копеек" -> "0 копеек" for any case

public class MoneyAmountInWords {

    private static final String NEGATIVE_VALUE = "Can't process negative value [%s]!";

    private static final String[][] SEX = {
            {"", "один", "два", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять"},
            {"", "одна", "две", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять"},
    };

    private static final String[]   STR100 = {
            "", "сто", "двести", "триста", "четыреста", "пятьсот", "шестьсот", "семьсот", "восемьсот", "девятьсот"
    };

    private static final String[]   STR11 = {
            "", "десять", "одиннадцать", "двенадцать", "тринадцать", "четырнадцать", "пятнадцать",
            "шестнадцать", "семнадцать", "восемнадцать", "девятнадцать", "двадцать"
    };

    private static final String[]   STR10 = {
            "", "десять", "двадцать", "тридцать", "сорок", "пятьдесят",
            "шестьдесят", "семьдесят", "восемьдесят", "девяносто"
    };

    private static final String[][] FORMS = {
            {"копейка", "копейки", "копеек", "1"},
            {"рубль", "рубля", "рублей", "0"},
            {"тысяча", "тысячи", "тысяч", "1"},
            {"миллион", "миллиона", "миллионов", "0"},
            {"миллиард","миллиарда","миллиардов","0"},
            {"триллион","триллиона","триллионов","0"},
            // можно добавлять дальше секстиллионы и т.д.
    };

    /**
     * Сумма денег
     */
    private BigDecimal amount;
 
    /**
     * Конструктор из Long
     */
    public MoneyAmountInWords(long l) {
        String s = String.valueOf(l);
        if (!s.contains(".") )
            s += ".0";
        this.amount = new BigDecimal( s );
    }
 
    /**
     * Конструктор из Double
     */
    public MoneyAmountInWords(double l) {
        String s = String.valueOf(l);
        if (!s.contains(".") )
            s += ".0";
        this.amount = new BigDecimal( s );
    }
 
    /**
     * Конструктор из String
     */
    public MoneyAmountInWords(String s) {

        String tmpString = (s == null ? "" : s.trim());

        if (!tmpString.contains(".") ) {
            tmpString += ".0";
        }

        this.amount = new BigDecimal(tmpString);
    }
 
    /**
     * Вернуть сумму как строку
     */
    public String asString() {
        return amount.toString();
    }
 
    /**
     * Вернуть сумму прописью, с точностью до копеек
     */
    public String num2str() {
        return num2str(false);
    }
 
    /**
     * Выводим сумму прописью
     * @param stripkop boolean флаг - показывать копейки или нет
     * @return String Сумма прописью
     */
    public String num2str(boolean stripkop) {

        if (amount.signum() == -1) { // check argument
            throw new IllegalArgumentException(String.format(NEGATIVE_VALUE, amount));
        }

        // получаем отдельно рубли и копейки
        long rub = amount.longValue();
        String[] moi = amount.toString().split("\\.");

        long kop = (moi.length > 1 ? Long.valueOf(moi[1]) : 0L); // for case xx.

        if (moi.length > 1 && !moi[1].substring( 0,1).equals("0")) { // начинается не с нуля
            if (kop<10 )
                kop *=10;
        }

        String kops = String.valueOf(kop);
        if (kops.length()==1 )
            kops = "0"+kops;
        long rub_tmp = rub;
        // Разбиватель суммы на сегменты по 3 цифры с конца
        ArrayList segments = new ArrayList();
        while(rub_tmp>999) {
            long seg = rub_tmp/1000;
            segments.add( rub_tmp-(seg*1000) );
            rub_tmp=seg;
        }
        segments.add( rub_tmp );
        Collections.reverse(segments);

        // Анализируем сегменты
        String o = "";
        if (rub== 0) {// если Ноль
            o = "ноль "+morph( 0, FORMS[1][ 0], FORMS[1][1], FORMS[1][2]);
            if (stripkop)
                return o;
            else
                return o +" "+kop+" "+morph(kop, FORMS[ 0][ 0], FORMS[ 0][1], FORMS[ 0][2]);
        }

        // Больше нуля
        int lev = segments.size();
        for (int i= 0; i<segments.size(); i++ ) {// перебираем сегменты
            int sexi = (int)Integer.valueOf( FORMS[lev][3].toString() );// определяем род
            int ri = (int)Integer.valueOf( segments.get(i).toString() );// текущий сегмент
            if (ri== 0 && lev>1) {// если сегмент ==0 И не последний уровень(там Units)
                lev--;
                continue;
            }

            String rs = String.valueOf(ri); // число в строку

            // нормализация
            if (rs.length()==1) rs = "00"+rs;// два нулика в префикс?
            if (rs.length()==2) rs = "0"+rs; // или лучше один?

            // получаем циферки для анализа
            int r1 = Integer.valueOf( rs.substring( 0,1) ); //первая цифра
            int r2 = Integer.valueOf( rs.substring(1,2) );  //вторая
            int r3 = Integer.valueOf( rs.substring(2,3) );  //третья
            int r22= Integer.valueOf( rs.substring(1,3) );  //вторая и третья
            // Супер-нано-анализатор циферок
            if (ri>99) o += STR100[r1]+" "; // Сотни
            if (r22>20) {// >20
                o += STR10[r2]+" ";
                o += SEX[ sexi ][r3]+" ";
            }
            else { // <=20
                if (r22>9) o += STR11[r22-9]+" "; // 10-20
                else o += SEX[ sexi ][r3]+" "; // 0-9
            }
            // Единицы измерения (рубли...)
            o += morph(ri, FORMS[lev][ 0], FORMS[lev][1], FORMS[lev][2])+" ";
            lev--;
        }
        // Копейки в цифровом виде
        if (stripkop) {
            o = o.replaceAll(" {2,}", " ");
        }
        else {
            o = o+""+kops+" "+morph(kop, FORMS[ 0][ 0], FORMS[ 0][1], FORMS[ 0][2]);
            o = o.replaceAll(" {2,}", " ");
        }
        return o;
    }
 
    /**
     * Склоняем словоформу
     * @param n Long количество объектов
     * @param f1 String вариант словоформы для одного объекта
     * @param f2 String вариант словоформы для двух объектов
     * @param f5 String вариант словоформы для пяти объектов
     * @return String правильный вариант словоформы для указанного количества объектов
     */
    private static String morph(long n, String f1, String f2, String f5) {
        n = Math.abs(n) % 100;
        long n1 = n % 10;
        if (n > 10 && n < 20) return f5;
        if (n1 > 1 && n1 < 5) return f2;
        if (n1 == 1) return f1;
        return f5;
    }

}