package jdb.model.dto;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Класс реализует модель одной записи (со всеми полями и их значениями) из любой таблицы любой БД.
 * Данный класс может быть сериализован.
 * @author Gusev Dmitry (019gus)
 * @version 2.0 (DATE: 24.03.2008)
*/

public class RowDTOModel implements Serializable
 {
  static final long serialVersionUID = 796418028120737040L;

  /** Список полей одной записи с их значениями. */
  private ArrayList<FieldDTOModel> fieldsModels = null;

  public RowDTOModel() {}

  public ArrayList<FieldDTOModel> getFields() {return fieldsModels;}
  public void setFields(ArrayList<FieldDTOModel> fieldModels) {this.fieldsModels = fieldModels;}

  /**
   * Метод добавляет одно поле(экземпляр класса TableFieldDTO) к списку полей данной записи. Если поле пусто - оно не
   * будет добавлено. Если список еще пуст - он будет проинициализирован.
   * @param fieldModel TableFieldDTO добавляемое к списку поле.
  */
  public void addField(FieldDTOModel fieldModel)
   {
    if (fieldModel != null)
     {if (this.fieldsModels == null) this.fieldsModels = new ArrayList<FieldDTOModel>(); this.fieldsModels.add(fieldModel);}
   }

  /**
   * Метод находим в текущей строке TableRowDTO поле с именем fieldName и возвращает его. Если параметр fieldName пуст
   * или такое поле не найдено - метод возвращает значение null.
   * @param fieldName String имя искомого поля.
   * @return TableFieldDTO найденное по имени поле или значение null.
  */
  public FieldDTOModel getFieldByName(String fieldName)
   {
    FieldDTOModel fieldModel = null;
    // Т.к. имена полей хранятся в верхнем регистре, то имя поля для поиска также переводим в верхний регистр
    if (!StringUtils.isBlank(fieldName) && (this.fieldsModels != null) && (!this.fieldsModels.isEmpty()))
     {
      for (FieldDTOModel localFieldModel : this.fieldsModels)
       {
        if ((localFieldModel != null) && (fieldName.toUpperCase().equals(localFieldModel.getFieldName())))
         {fieldModel = localFieldModel;}
       }
     }
    return fieldModel;
   }

  /**
   * Метод возвращает CSV-список полей данной строки или значение null, если список пуст.
   * @return String CSV-список полей данной строки или значение null.
  */
  public String getCSVFieldsList()
   {
    StringBuilder csvList = null;
    if ((this.fieldsModels != null) && (!this.fieldsModels.isEmpty()))
     {
      csvList = new StringBuilder();
      for (int i = 0; i < this.fieldsModels.size(); i++)
       {
        csvList.append(this.fieldsModels.get(i).getFieldName());
        if (i < this.fieldsModels.size() - 1) csvList.append(",");
       }
     }
    // Формируем результат
    String result;
    if (csvList == null) {result = null;} else {result = csvList.toString();}
    return result;
   }

  /** Метод формирует и возвращает строковое представление экземпляра данного класса. */
  public String toString() {return ("\n ROW: " + this.fieldsModels);}

  /**
   * Метод возвращает значение ИСТИНА, если данный экземпляр класса пуст (пуст список полей).
   * @return boolean метод возвращает ИСТИНА/ЛОЖЬ в зависимости от того, пуст ли список моделей полей для данной записи.
  */
  public boolean isEmpty() {return ((this.fieldsModels == null) || (this.fieldsModels.isEmpty()));}

 }