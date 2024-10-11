package jdb.processing.sql.execution.batch.executors.multiThreadsHelpers;

/**
 * Класс предназначен для использования в методе многопотокового выполнения sql-батчей.
 * Задача данного класса - подсчет и хранение общего количества всех выполненных sql-запросов во всех потоках.
 * @author Gusev Dmitry (Дмитрий)
 * @version 1.0 (DATE: 06.05.2010)
*/

public class TotalProcessedQueries
 {
  // Здесь храним общее число обработанных запросов
  private int total = 0;

  // Увеличение общего числа обработанных запросов
  public void addTotal(int count) {total += count;}

  // Получение общего числа обработанных запросов
  public int  getTotal() {return total;}
 }