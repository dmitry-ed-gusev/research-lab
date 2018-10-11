package jdb.filter.sql;

import jdb.DBConsts;
import jlib.logging.InitLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Данный класс реализует фильтрацию sql-запроса от различных запрещенных символов. Из запроса удаляются все
 * непечатаемые символы с кодами \00 - \37 (в восьмеричной нотации) кроме слудующих: \11 - символ табуляции,
 * \12 - новая строка(перевод каретки, line feed), \15 - аналог новой строки (form-feed), \40 - символ пробела -
 * первый неспециальный отображаемый символ. Также символы [“, ”, '] будут заменены на символ ["].
 *
 * 02.04.08 Т.к. фильтр используется повторно множество раз, то при уровне отладки (DEBUG) он генерит очень много
 * отладочных сообщений - поэтому отладочные сообщения в методах класса выключены (закомментированы).
 *
 * @author Gusev Dmitry (019gus)
 * @version 3.1 (DATE: 16.03.2011)
*/

// todo: символ \ (нечетное число символов) также вызывают крах sql-запроса. Эти символы надо убирать/заменять???
 
// todo: случай кавычки в кавычках - не работает. Пример: "ddfds gdfsdfg "dwfsdf" dfsdf". Т.е. или убирать или
// todo: кавычки внутри других кавычек должны быть разными, например: "asfgsf gsdf gsdf g 'dfgdfg' sfgsfg". КАК?

public class SqlFilter
 {
  /** Компонент-логгер данного класса. */
  //private static Logger logger = Logger.getLogger(SqlFilter.class.getName());

  /**
   * Метод получает на входе строку str, из которой удаляются все символы из списка запрещенных (SQL_DEPRECATED_SYMBOLS).
   * @param sqlStr String строка, из которой удаляются нежелательные символы.
   * @return String строка без запрещенных символов.
  */
  public static String removeDeprecated(String sqlStr)
   {
    //logger.debug("WORKING SqlFilter.removeDeprecated().");
    String result = null;
    if (!StringUtils.isBlank(sqlStr))
     {
      result = sqlStr;
      // Удаляем все запрещенные символы из sql-запроса
      for (String aDeprecated : DBConsts.SQL_DEPRECATED_SYMBOLS) {result = result.replaceAll(aDeprecated, "");}
     }
    return result;
   }

  /**
   * Метод получает на входе строку sqlStr, в которой все символы кавычек, которые есть в списке SQL_DEPRECATED_QUOTES,
   * заменяются на символ кавычки по умолчанию - SQL_DEFAULT_QUOTE. Если на входе метод получает пустую строку или null, то
   * возвращаемое значение будет null.
   * @param sqlStr String строка, в которой заменяются кавычки на стандартные.
   * @return String строка с замененными на стандартные кавычками (или значение null).
  */
  public static String changeQuotes(String sqlStr)
   {
    //logger.debug("WORKING SqlFilter.changeQuotes().");
    String result = null;
    if (!StringUtils.isBlank(sqlStr))
     {
      result = sqlStr;
      // Заменяем все "неверные" кавычки на "верные"
      for (String aQuote : DBConsts.SQL_DEPRECATED_QUOTES) {result = result.replaceAll(aQuote, DBConsts.SQL_DEFAULT_QUOTE);}
     }
    return result;
   }

  /**
   * Метод удаляет из указанной строки кавычки - и "запрещенные" и "разрешенные". Если на входе пустая строка или null, то
   * возвращаемое значение будет null.
   * @param sqlStr String строка, в которой удаляются кавычки.
   * @return String строка без кавычек или значение null.
  */
  public static String removeQuotes(String sqlStr)
   {
    String result = null;
    if (!StringUtils.isBlank(sqlStr))
     {
      result = sqlStr;
      // Удаляем все "запрещенные" кавычки из строки
      for (String aDeprecated : DBConsts.SQL_DEPRECATED_QUOTES) {result = result.replaceAll(aDeprecated, "");}
      // Удаляем все "обычные" кавычки из строки
      result = result.replaceAll(DBConsts.SQL_DEFAULT_QUOTE, "");
     }
    return result;
   }


  /**
   * Метод заменяет угловые кавычки (скобки) (используются для HTML-тэгов) на их символьные коды. Если на входе пустая строка
   * или null, то возвращаемое значение будет null.
   * @param sqlStr String строка, в которой заменяются (скобки).
   * @return String строка с замененными скобками или значение null.
  */
  public static String changeChevronsToSymbols(String sqlStr)
   {
    String result = null;
    if (!StringUtils.isBlank(sqlStr))
     {
      result = sqlStr;
      result = result.replaceAll("<", "&lt;");
      result = result.replaceAll(">", "&gt;");
     }
    return result;
   }

  /**
   * Метод заменяет угловые кавычки (скобки) (используются для HTML-тэгов) на их цифровые коды. Если на входе пустая строка
   * или null, то возвращаемое значение будет null.
   * @param sqlStr String строка, в которой заменяются (скобки).
   * @return String строка с замененными скобками или значение null.
  */
  public static String changeChevronsToCode(String sqlStr)
   {
    String result = null;
    if (!StringUtils.isBlank(sqlStr))
     {
      result = sqlStr;
      result = result.replaceAll("<", "&#060;");
      result = result.replaceAll(">", "&#062;");
     }
    return result;
   }

  /**
   *  Метод только для тестирования.
   * @param args параметр метода.
  */
  public static void main(String[] args)
   {
    InitLogger.initLogger("jdb");
    Logger logger = Logger.getLogger("jdb");
    String sql    = "<script>FFFF</script>";
    logger.debug(SqlFilter.changeChevronsToSymbols(sql));
    logger.debug(SqlFilter.changeChevronsToCode(sql));
   }

 }