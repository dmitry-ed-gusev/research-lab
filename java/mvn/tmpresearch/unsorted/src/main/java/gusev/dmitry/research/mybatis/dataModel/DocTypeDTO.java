package gusev.dmitry.research.mybatis.dataModel;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * This class implements object for one record from docTypes table (from DBMS).
 * @author Gusev Dmitry
 * @version 2.0 (DATE: 28.06.2011)
*/

public final class DocTypeDTO
 {
  private final int     id;
  private final String  typeName;
  private final String  typeDesc;
  private final boolean deleted;

  public DocTypeDTO(int id, String typeName, String typeDesc, int deleted)
   {
    this.id       = id;
    this.typeName = typeName;
    this.typeDesc = typeDesc;
    this.deleted  = (deleted != 0);
   }

  public int      getId()       {return id;}
  public boolean  getDeleted()  {return deleted;}
  public String   getTypeName() {return typeName;}
  public String   getTypeDesc() {return typeDesc;}

  @Override
  public String toString() {
   return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).
           append("id", id).
           append("typeName", typeName).
           append("typeDesc", typeDesc).
           append("deleted", deleted).
           toString();
  }
 }