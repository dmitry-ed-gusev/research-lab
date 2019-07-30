package dgusev.xml;

/**
 * Данный класс содержит набор констант-тегов для xml-фйала конфигурации различных модулей.
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 23.01.2008)
*/

public class TagsXML
 {
  /** Тэг xml-файла - раздел настроек приложения. */
  public static final String APP_SETTINGS_HEADER     = "app-settings";
  /** Тэг xml-файла - список настроек/параметров приложения. */
  public static final String PROPERTIES_LIST_HEADER  = "properties";
  /** Тэг xml-файла - один из параметров приложения. */
  public static final String PROPERTY_HEADER         = "property";
  /** Атрибут "ключ" тэга PROPERTY_HEADER. */
  public static final String KEY_NAME                = "key";
 }