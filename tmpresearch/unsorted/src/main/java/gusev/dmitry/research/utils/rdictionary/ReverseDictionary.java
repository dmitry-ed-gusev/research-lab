package gusev.dmitry.research.utils.rdictionary;

import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Программа-решение тестовой задачи: сортировка входящего потока слов в порядке обратного словаря.
 * Комментарии по ходу исходного текста достаточно полно документируют сам код.
 * @author Gusev Dmitry (Дмитрий)
 * @version 1.0 (DATE: 10.08.11)
*/

public class ReverseDictionary
 {
  // Команда для выхода из приложения
  private static final String COMMAND_EXIT = "!exit";
  // Команда для показа отсортированного словаря
  private static final String COMMAND_SHOW = "!show";
  // Команда помощи
  private static final String COMMAND_HELP = "!help";
  // Текст помощи/подсказки для программы
  private static final StringBuilder HELP = new StringBuilder().append("Supported commands: \n").
                                     append("  ").append(COMMAND_EXIT).append(" program exit\n").
                                     append("  ").append(COMMAND_SHOW).append(" shows the stored dictionary\n").
                                     append("  ").append(COMMAND_HELP).append(" shows this help\n");

  /**
   * Метод инвертирует слово: привет->тевирп. Если параметр пуст или null, метод возвращает null;
   * @param word String слово для инвертирования.
   * @return String инвертированное слово или null.
  */
  protected static String invert(String word)
   {
    if ((word != null) && (!word.trim().equals("")))
     {
      char[] source   = word.toCharArray();
      char[] inverted = new char[source.length];
      for (int i = source.length - 1; i >= 0; i--)
       {inverted[inverted.length - i - 1] = source[i];}
      return new String(inverted);
     }
    else {return null;}
   }

  /**
   * Метод возвращает строковое представление отсортированного обратного словаря. Если словарь пуст - метод
   * возвращает строковое значение "Dictionary is empty!\n". Параметр maxWordLength указывает максимальную длину
   * слова в словаре для выравнивания слов по правому краю (с помощью пробелов).
   * @param dictionary TreeMap[String, String] словарь, полученный от пользователя.
   * @param maxWordLength максимальная длина слова в словаре.
   * @return String строковое представление словаря.
  */
  protected static String getStringDictionary(TreeMap<String, String> dictionary, int maxWordLength)
   {
    String result;
    if ((dictionary != null) && (!dictionary.isEmpty()))
     {
      StringBuilder dict = new StringBuilder();
      for (Map.Entry entry : dictionary.entrySet())
       {
        String word = (String)entry.getValue();
        // Для выравнивания по правому краю дополняем слова пробелами слева
        if (word.length() < maxWordLength)
         {for (int i = 1; i <= (maxWordLength - word.length()); i++) {dict.append(" ");}}
        dict.append(word).append("\n");
       }
      result = dict.toString();
     }
    else {result = "Dictionary is empty!\n";}
    return result;
   }

  /**
   * Главный запускаемый метод приложения.
  */
  public static void main(String[] args)
   {
    // Покажем подсказку
    System.out.println(HELP.toString());
    // Настроимся на ввод с клавиатуры
    Scanner scanner = new Scanner(System.in);
    String word;
    // Здесь храним наш словарь
    TreeMap<String, String> dictionary = new TreeMap<String, String>();
    // Длина (макс.) слова в словаре
    int maxWordLength = 0;
    // Признак выхода из цикла ввода
    boolean exit = false;
    // Цикл ввода слов в словарь.
    do
     {
      System.out.print("Next word: ");
      word = scanner.next();
      // Команда выхода из цикла опроса ввода
      if (COMMAND_EXIT.equals(word))
       {exit = true;}
      // Команда отображения словаря
      else if (COMMAND_SHOW.equals(word))
       {System.out.println("\nSorted dictionary:\n" + ReverseDictionary.getStringDictionary(dictionary, maxWordLength));}
      // Команда отображения помощи
      else if (COMMAND_HELP.equals(word))
       {System.out.println(HELP);}
      // Добавление нового слова в словарь
      else
       {
        // Добавляем только непустое слово
        if ((word != null) && (!word.trim().equals("")))
         {
          // Добавляем слово и его инвертированный вариант
          dictionary.put(ReverseDictionary.invert(word.trim()), word.trim());
          // Пересчитываем (если надо) макс. длину слова в словаре
          if (word.trim().length() > maxWordLength) {maxWordLength = word.trim().length();}
         }
       }
     }
    while(!exit);
   }

 }