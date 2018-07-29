package jlib.utils.common;

import jlib.common.Consts;
import java.io.*;
import java.util.*;

/**
 * Данный класс реализует чтение конфигурационных данных из стандартного ini-файла ОС Windows.
 * @author Gusev Dmitry
 * @version 2.0
*/

public class IniFileReader
 {
  /** Служебное поле */
  final static private int      NAME           = 0;
  /** Служебное поле */
  final static private int      VALUE          = 1;
  /** Константа - префикс названия секции в ини-файле */
  final static private String   SECTION_PREFIX = "[";
  /** Константа - постфикс названия секции в ини-файле */
  final static private String   SECTION_SUFFIX = "]";
  /** Константа - список, содержащий символы комментариев */
  final static private String[] COMMENT_SYMS   = {"//",";","#"};

  /** Переменная для хранения имени ини-файла. */
  private String                ini_file       = null;
  /** Переменная-список для хранения списка символов-индикаторов для комментариев в ини-файле. */
  private List<String>          comment_syms   = null;
  /** Переменная для хранения символа-префикса для наименования секции в ини-файле. */
  private String                section_prefix = SECTION_PREFIX;
  /** Переменная для хранения символа-постфикса для наименования секции в ини-файле. */
  private String                section_suffix = SECTION_SUFFIX;

  /**
   * Конструктор. Проверяется существование конфиг-файла iniFileName.
   * @param iniFileName String имя конфигурационного файла. Если пусто или
   * null - ошибка.
   * @throws Exception ИС возникает, если файл iniFileName не существует или
   * переменная пуста или равна null.
  */
  public IniFileReader(String iniFileName) throws Exception
   {
    // Проверка существования конфиг. файла
    if ((iniFileName != null) && new File(iniFileName).exists()) this.ini_file = iniFileName;
    else throw new Exception("CONFIG FILE <" + iniFileName + "> DOESN'T EXISTS!");
    // Создаем список символов-индикаторов для комментариев и заполняем его
    this.comment_syms = new ArrayList<String>();
    this.comment_syms.addAll(Arrays.asList(IniFileReader.COMMENT_SYMS));
   }

  /**
   *
   * @param prefix String
  */
  public void setSectionPrefix(String prefix)
   {if ((prefix != null) && (!prefix.trim().equals(""))) this.section_prefix = prefix;}

  /**
   *
   * @param suffix String
  */
  public void setSectionSuffix(String suffix)
   {if ((suffix != null) && (!suffix.trim().equals(""))) this.section_suffix = suffix;}

  /**
   *
   * @param symbol String
  */
  public void addCommentSymbol(String symbol)
   {if ((symbol != null) && (!symbol.trim().equals(""))) this.comment_syms.add(symbol);}

  /**
   * Возвращает массив строковых значений для ключа key из секции section.
   * Значения ключа разделяются символами patternStr2, ключ от значения
   * отделяется символом patternStr.
   *
   * @param section String
   * @param key String
   * @throws Exception ИС
   * @return String[]
  */
  public String[] getArray(String section, String key) throws Exception
   {return this.getString(section, key).split(Consts.VALUES_DELIM);}

  /**
   * Метод читает строковое значение, соответствующее ключу key из конфигурационного
   * файла, значение берется из секции section.
   * @param section String наименование секции конфиг. файла для чтения параметра.
   * Указывается без каких-либо символов ограничителей ([,{ и т.д.).
   * @param key String ключ, значение которого читается из файла.
   * @throws Exception ИС возникает при ошибке во входных параметрах или в формате файлаю
   * @return String строковое значение параметра, соотв. ключу key.
  */
  public String getString(String section, String key) throws Exception
   {
     String value    = null;
     String   str;
     BufferedReader rd = null;
     if (
         (key != null)     && (!key.trim().equals("")) &&
         (section != null) && (!section.trim().equals(""))
        )
      {
       try
        {
         // Открываем ини-файл для чтения
         rd = new BufferedReader(new FileReader(this.ini_file));
         // Читаем строки из входного файла пока не достигнем его конца
         while ((str = rd.readLine()) != null)
          {
           if (!isComment(str)) // Если очередная строка не комментарий - работаем
            {
             // Разбиваем прочтенную строку на слова (разделитель - LIBC.KEY_VALUE_DELIM)
             String[] fields = str.trim().split(Consts.KEY_VALUE_DELIM);
             // Если в строке 1 слово и оно соответствует названию секции,
             // то читаем данную секцию до нахождения нужного ключа
             // или до конца секции(или файла)
             if (
                 (fields.length == 1) &&
                 (fields[0].trim().equals(section_prefix + section + section_suffix))
                )
              {
               // Непосредственное чтение секции
               while ((str = rd.readLine()) != null)
                {
                 // Нам необходимо получить значение ключа, которое находится справа
                 // от самого первого слева знака LIBC.KEY_VALUE_DELIM
                 // (в строке 1=2=3;4=5;6=7 мы должны получить: key={1}, value={2=3;4=5;6=7}).
                 // Для этого в методе split указывается второй параметр - N. Данный параметр
                 // (если > 0) указывает на то, что символ-разделитель (шаблон) будет применен
                 // к строке (N-1) раз. Т.о. для нашей цели N=2.
                 fields = str.trim().split(Consts.KEY_VALUE_DELIM, 2);
                 // Если мы нашли ключ - берем его значение(если оно есть),
                 // а затем выходим из цикла чтения секции
                 if ((fields.length > 1) && (fields[NAME].trim().compareTo(key) == 0))
                  {value = fields[VALUE].trim(); break;}
                 // Если дошли до следующей секции - то выходим из цикла чтения секции
                 if ((fields.length == 1) && (fields[NAME].trim().startsWith("["))) {break;}
                }// Конец цикла чтения секции ини-файла
              }
            }
           // Если мы нашли нужное значение - выходим из цикла чтения входного потока
           if (value != null) {break;}
          }// Конец цикла чтения входного файла
        }// Конец секции try {}
       // Перехватим исключительную ситуацию
       catch (Exception e) {throw new Exception("ERROR WHILE READING INI-FILE (" + e.getMessage() + ")");}
       // Файл нужно попытаться закрыть в любом случае
       finally {if (rd != null) rd.close();}
      }
     return value;
    }

  /**
   * Данный метод возвращает ИСТИНА, если секция(раздел) sectionName существует
   * в данном конфигурационном файле. Если параметр sectionName пуст или равен null,
   * то метод вернет ЛОЖЬ.
   * @param sectionName String наименование секции для поиска в конфиг-файле
   * @return boolean ИСТИНА/ЛОЖЬ в зависимости от результата поиска
   * @throws Exception ИС может возникнуть при чтении конфиг-файла
  */
  public boolean isSectionExists(String sectionName) throws Exception
   {
    String         str;
    BufferedReader rd = null;
    if ((sectionName != null) && (!sectionName.trim().equals("")))
     try
      {
       // Открываем ини-файл для чтения
       rd = new BufferedReader(new FileReader(this.ini_file));
       // Читаем строки из входного файла пока не достигнем его конца
       while ((str = rd.readLine()) != null)
        if (!isComment(str)) // Если очередная строка не комментарий - работаем
         {
          // Разбиваем прочтенную строку на слова (разделитель - LIBC.KEY_VALUE_DELIM)
          String[] fields = str.trim().split(Consts.KEY_VALUE_DELIM);
          // Если в строке 1 слово и оно соответствует названию секции -> BINGO!
          if ((fields.length == 1) && (fields[0].trim().equals(section_prefix + sectionName + section_suffix)))
           return true;
         }
      }// Конец секции try {}
     // Перехватим исключительную ситуацию
     catch (Exception e) {throw new Exception("ERROR WHILE READING INI-FILE (" + e.getMessage() + ")");}
     // Файл нужно попытаться закрыть в любом случае
     finally {if (rd != null) rd.close();}
    return false;
   }

  /**
   * Возвращает ИСТИНА, если строка str является комментарием (начинается с
   * символа комментария). Символы комментариев находятся в массиве
   * comment_syms.
   *
   * @param str String
   * @return boolean
  */
  private boolean isComment(String str)
   {
    boolean result = false;
    // Если переданная нам строка не пуста - работаем
    if ((str != null) && (!str.trim().equals("")))
     // Проходим в цикле по всем элементам списка
     for (Object commentSymbol : this.comment_syms)
      if (str.trim().startsWith((String) commentSymbol)) result = true;
    return result;
   }

 /**
  * Метод main используется только для тестирования данного класса.
  * @param args String[]
  */
 public static void main(String[] args)
   {}

 } // END OF CLASS INI_READER
