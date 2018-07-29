package jlib.gui;

import java.awt.*;

/**
 * @author Gusev Dmitry (019gus)
 * @version 1.0 (DATE: 08.04.2009)
*/

public class GuiUtils
 {
  /**
   * ����� �������� ���� (����� Window), ������ �� ������� �� �������� � �������� ���������, ��
   * ������ ������.
   * @param w Window ���� ��� ��������� (�� ������ ���� null).
  */
  public static void center(Window w)
   {
    // ���� ������ ������� - ������ �� ������
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