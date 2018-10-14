package jdb.nextGen.serialization;

import jdb.nextGen.DBasesLoaderCore;
import jdb.nextGen.exceptions.JdbException;
import jlib.exceptions.EmptyObjectException;
import jlib.logging.InitLogger;
import jlib.utils.FSUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Класс-описатель для сериализованных данных - таблиц и БД. Класс почти-immutable - он позволяет добавлять элементы во
 * внутренний список, но не позволяет менять имя и тип описываемого объекта.
 * @author Gusev Dmitry (Гусев Дмитрий)
 * @version 4.0 (DATE: 30.05.2011)
*/

// todo: может, сделать класс immutable?

public final class Descriptor implements Serializable
 {
  /** Поле для совместимости с последующими версиями класса. */
  static final long serialVersionUID = 6653925531273485678L;
  // Наименование объекта
  private final String                      objectName;
  // ВАЖНО! Всегда инициализируем данное поле - иначе необходимо добавить проверки на значение NULL (в методы).
  private final ArrayList<String>           objectItems = new ArrayList<String>();
  // Тип объекта
  private final DBasesLoaderCore.ObjectType objectType;

  /**
   * Конструктор дескриптора.
   * @param objectName String
   * @param objectType ObjectType
   * @throws JdbException
  */
  public Descriptor(String objectName, DBasesLoaderCore.ObjectType objectType) throws JdbException
   {
    if (!StringUtils.isBlank(objectName))
     {
      // Наименование объекта хранится в ВЕРХНЕМ регистре символов
      this.objectName = objectName.toUpperCase();
      // Тип объекта
      if (objectType != null) {this.objectType = objectType;}
      else {throw new JdbException("Object type for descriptor is NULL!");}
     }
    // Имя объекта пусто - ошибка!
    else {throw new JdbException("Object name for descriptor is empty!");}
   }

  public String                      getObjectName()  {return objectName;}
  public DBasesLoaderCore.ObjectType getObjectType()  {return objectType;}
  public ArrayList<String>           getObjectItems() {return new ArrayList<String>(this.objectItems);}

  /***/
  public void addItem(String item) throws JdbException
   {
    if (!StringUtils.isBlank(item)) {objectItems.add(item.toUpperCase());}
    else                            {throw new JdbException("Trying to add an empty item to descriptor!");}
   }

  /***/
  public boolean isEmpty()
   {
    boolean result = true;
    if (!this.objectItems.isEmpty()) {result = false;}
    return result;
   }

  /***/
  public boolean containsItem(String item)
   {
    boolean result = false;
    if (!StringUtils.isBlank(item) && (!this.objectItems.isEmpty()) && (this.objectItems.contains(item.toUpperCase())))
     {result = true;}
    return result;
   }

  /***/
  public static void main(String[] args)
   {
    InitLogger.initLoggers(new String[]{"jdb", "jlib"});
    Logger logger = Logger.getLogger("jdb");
    try
     {
      Descriptor descriptor = new Descriptor("testTable", DBasesLoaderCore.ObjectType.TABLE);
      descriptor.addItem("file1");
      descriptor.addItem("file2");
      descriptor.addItem("file3");
      FSUtils.serializeObject(descriptor, "c:\\temp\\", "descriptor", "tbl");
      Descriptor loadedDescriptor = (Descriptor)FSUtils.deserializeObject("c:\\temp\\descriptor.tbl", false);
      logger.info(loadedDescriptor.getObjectItems());
      logger.info(loadedDescriptor.getObjectType());
     }
    catch (IOException e)            {logger.error(e.getMessage());}
    catch (JdbException e)           {logger.error(e.getMessage());}
    catch (EmptyObjectException e)   {logger.error(e.getMessage());}
    catch (ClassNotFoundException e) {logger.error(e.getMessage());}
   }

 }