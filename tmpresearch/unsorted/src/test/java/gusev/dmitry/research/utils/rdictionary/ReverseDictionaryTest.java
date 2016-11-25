package gusev.dmitry.research.utils.rdictionary;

import org.junit.Test;

import java.util.TreeMap;

import static junit.framework.Assert.*;

/**
 * Пара тестов для тестирования методов модуля ReverseDictionary.
 * @author Gusev Dmitry (Гусев Дмитрий)
 * @version 1.0 (DATE: 10.08.11)
*/

public class ReverseDictionaryTest
 {
  @Test
  // тест метода invert()
  public void testInvertWord()
   {
    // проверяем на слове-эталоне
    assertEquals("Invalid word invert!", "тевирп", ReverseDictionary.invert("привет"));
    // проверка граничных значений
    assertNull("Must return null!", ReverseDictionary.invert("    "));
    assertNull("Must return null!", ReverseDictionary.invert(""));
    assertNull("Must return null!", ReverseDictionary.invert(null));
   }

  @Test
  // тест метода getStringDictionary
  public void testGetStringDictionary()
   {
    // Проверим граничные значения
    assertNotNull("Must return not null!", ReverseDictionary.getStringDictionary(null, 0));
    assertNotNull("Must return not null!", ReverseDictionary.getStringDictionary(new TreeMap<String, String>(), 0));
    // Тестовый словарь
    TreeMap<String, String> testMap = new TreeMap<String, String>();
    testMap.put("тевирп", "привет");
    testMap.put("тавир", "риват");
    // Словарь в строковом представлении
    String testResult = " риват\nпривет\n";
    // Прверяем совпадение
    assertEquals("Not equals with test map!", testResult, ReverseDictionary.getStringDictionary(testMap, 6));
   }

 }