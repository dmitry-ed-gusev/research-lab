package gusev.dmitry.research.mybatis.dataModel;

import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

/**
 * @author Gusev Dmitry
 * @version 1.0 (DATE: 04.07.11)
 */
public interface DocsMapper
 {
  //@Select("select * from docTypes where id = #{id}")
  DocTypeDTO            selectDocType(@Param("id") int id, @Param("deleted") boolean deleted);
  ArrayList<DocTypeDTO> selectAllDocTypes(@Param("deleted") boolean deleted);
 }
