package jdb.processing.sql.execution.batch.executors.multiThreadsHelpers;

/**
 *  ласс предназначен дл€ использовани€ в методе многопотокового выполнени€ sql-батчей.
 * «адача данного класса - подсчет и хранение общего количества всех выполненных sql-запросов во всех потоках.
 * @author Gusev Dmitry (ƒмитрий)
 * @version 1.0 (DATE: 06.05.2010)
*/

public class TotalProcessedQueries
 {
  // «десь храним общее число обработанных запросов
  private int total = 0;

  // ”величение общего числа обработанных запросов
  public void addTotal(int count) {total += count;}

  // ѕолучение общего числа обработанных запросов
  public int  getTotal() {return total;}
 }