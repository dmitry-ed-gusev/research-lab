package jlib.utils.string;

import jlib.utils.string.StrUtilities;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * @version 1.1 (DATE: 01.03.11)
*/

public class StrUtilitiesTest
 {

  @Test
  public void testGetFixedLenghtName()
   {
    assertEquals("00012", StrUtilities.getFixedLengthName(5, '0', "12"));
    assertEquals("12",    StrUtilities.getFixedLengthName(1, 'x', "12"));
    assertEquals(null,    StrUtilities.getFixedLengthName(-1, '0', "12"));
    assertEquals(null,    StrUtilities.getFixedLengthName(10, '0', ""));
    assertEquals(null,    StrUtilities.getFixedLengthName(10, '0', -1));
    assertEquals(null,    StrUtilities.getFixedLengthName(10, '0', 0));
    assertEquals("0000001234",    StrUtilities.getFixedLengthName(10, '0', 1234));
   }

 }