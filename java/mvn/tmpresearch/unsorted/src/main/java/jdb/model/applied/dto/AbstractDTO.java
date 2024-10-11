package jdb.model.applied.dto;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Класс реализует абстрактный DTO-компонент для прикладных задач. Все DTO компоненты для работы с БД должны быть
 * унаследованы от данного (они все должны иметь два метода - isEmpty() и toString()). Метод toString() использует
 * для работы (генерации строки внутреннего состояния) класс ToStringBuilder библиотеки org.apache.commons.lang и
 * стиль этой же библиотеки ToStringStyle.MULTI_LINE_STYLE, соответственно все классы-потомки также должны использовать
 * эти классы (ToStringBuilder и ToStringStyle). Также при реализации метода toString() в классе потомке необходимо
 * включать в его вывод информацию из данного метода класса-родителя, делается это с помощью конструкции
 * appendSuper(super.toString()).
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 11.02.2010)
 * @deprecated класс нафиг не нужен - усложняет работу!
*/

public abstract class AbstractDTO
 {
  private int id      = -1;
  private int deleted = 0;

  public int getId() {
   return id;
  }

  public void setId(int id) {
   this.id = id;
  }

  public int getDeleted() {
   return deleted;
  }

  public void setDeleted(int deleted) {
   this.deleted = deleted;
  }

  /**
   * Метод для определения пуст или нет экземпляр данного класса. Пуст - значит ключевые поля не заполнены данными.
   * Каждый потомок данного класса должен реализовывать свой метод isEmpty().
   * @return boolean ИСТИНА/ЛОЖЬ в зависимости от того, пуст или нет экземпляр класса.
  */
  public abstract boolean isEmpty();

  /**
   * Метод для представления внутреннего состояния экземпляра класса - показывает значение полей класса. Данный метод
   * используется для отладки приложений. Каждый потомок данного класса должен реализовывать свой метод toString().
   * В вывод своего метода toString() каждый потомок данного класса должен включать вывод данного метода класса-родителя.
   * @return String строковое представление состояния экземпляра класса.
   */
  @Override
  public String toString()
   {
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).
     append("id", id).
     append("deleted", deleted).
     toString();
   }

 }