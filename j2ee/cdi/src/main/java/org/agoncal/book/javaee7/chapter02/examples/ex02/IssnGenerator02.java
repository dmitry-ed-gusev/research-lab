package org.agoncal.book.javaee7.chapter02.examples.ex02;

import java.util.Random;

/**
 * @author Antonio Goncalves
 *         APress Book - Beginning Java EE 7 with Glassfish 4
 *         http://www.apress.com/
 *         http://www.antoniogoncalves.org
 *         --
 */
public class IssnGenerator02 implements NumberGenerator02 {

  // ======================================
  // =          Business methods          =
  // ======================================

  public String generateNumber() {
    return "8-" + Math.abs(new Random().nextInt());
  }
}