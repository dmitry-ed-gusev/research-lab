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
 * �����-��������� ��� ��������������� ������ - ������ � ��. ����� �����-immutable - �� ��������� ��������� �������� ��
 * ���������� ������, �� �� ��������� ������ ��� � ��� ������������ �������.
 * @author Gusev Dmitry (����� �������)
 * @version 4.0 (DATE: 30.05.2011)
*/

// todo: �����, ������� ����� immutable?

public final class Descriptor implements Serializable
 {
  /** ���� ��� ������������� � ������������ �������� ������. */
  static final long serialVersionUID = 6653925531273485678L;
  // ������������ �������
  private final String                      objectName;
  // �����! ������ �������������� ������ ���� - ����� ���������� �������� �������� �� �������� NULL (� ������).
  private final ArrayList<String>           objectItems = new ArrayList<String>();
  // ��� �������
  private final DBasesLoaderCore.ObjectType objectType;

  /**
   * ����������� �����������.
   * @param objectName String
   * @param objectType ObjectType
   * @throws JdbException
  */
  public Descriptor(String objectName, DBasesLoaderCore.ObjectType objectType) throws JdbException
   {
    if (!StringUtils.isBlank(objectName))
     {
      // ������������ ������� �������� � ������� �������� ��������
      this.objectName = objectName.toUpperCase();
      // ��� �������
      if (objectType != null) {this.objectType = objectType;}
      else {throw new JdbException("Object type for descriptor is NULL!");}
     }
    // ��� ������� ����� - ������!
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