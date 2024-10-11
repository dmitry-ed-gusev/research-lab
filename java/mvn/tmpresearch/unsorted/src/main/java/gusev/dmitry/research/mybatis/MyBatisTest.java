package gusev.dmitry.research.mybatis;

import gusev.dmitry.research.mybatis.dataModel.DocTypeDTO;
import gusev.dmitry.research.mybatis.dataModel.DocsMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

/**
 * @author Gusev Dmitry
 * @version 1.0 (DATE: 01.07.11)
 */
public class MyBatisTest
 {
  public static void main(String[] args)
   {
    SqlSession session = null;
    try
     {
      String resource = "dmitry/gusev/mybatisExample/dataModel/Configuration.xml";
      Reader reader = Resources.getResourceAsReader(resource);
      String env = "development";
      SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(reader, env);
      System.out.println("session created ok!");

      session = sqlMapper.openSession();
      // first variant
      //DocTypeDTO docType = (DocTypeDTO) session.selectOne("test.dataModel.DocsMapper.selectDocType",11);
      //System.out.println("1 -> " + docType);

      // second (modern) variant
      DocsMapper mapper = session.getMapper(DocsMapper.class);
      //DocTypeDTO docType2 = mapper.selectDocType(11, true);
      //System.out.println("2 -> " + docType2);

      ArrayList<DocTypeDTO> list = mapper.selectAllDocTypes(true);
      System.out.println("3 -> " + list);
     }
    catch (IOException e) {e.printStackTrace();}
    finally {if (session != null) {session.close();}}

   }

 }