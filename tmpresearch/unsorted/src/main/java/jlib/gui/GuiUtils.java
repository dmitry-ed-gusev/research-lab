package jlib.gui;

import java.awt.*;

/**
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 08.04.2009)
*/

public class GuiUtils
 {
  /**
   * Метод центрует окно (класс Window), ссылку на которое он получает в качестве параметра, по
   * центру экрана.
   * @param w Window окно для центровки (не должно быть null).
  */
  public static void center(Window w)
   {
    // Если ссылка нулевая - ничего не делаем
    if (w != null)
     {
      Dimension us = w.getSize( );
      Dimension screen = Toolkit.getDefaultToolkit().getScreenSize( );
      int newX = (screen.width - us.width) / 2;
      int newY = (screen.height- us.height)/ 2;
      w.setLocation(newX, newY);
     }
   }

 }