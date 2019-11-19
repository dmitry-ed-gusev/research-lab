/*
 * Copyright © 2018 Merck Sharp & Dohme Corp., a subsidiary of Merck & Co., Inc.
 * All rights reserved.
 */

package com.msd.bdp.di.tool;

import com.msd.bdp.ditoolcore.DIToolUtilities;
import com.msd.bdp.ditoolcore.dbfacade.DbType;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/***/
public class DIToolUtilitiesTest {

    @Mock
    private ResultSetMetaData resultSetMetaMock;
    @Mock
    private ResultSet resultSetMock;
    
    @Rule
    public ExpectedException expected = ExpectedException.none();
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this); // init mocks
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testConvertResultSetToCSV() throws SQLException {
        // prepare mocks
        Mockito.when(this.resultSetMetaMock.getColumnCount()).thenReturn(2);
        Mockito.when(this.resultSetMock.getMetaData()).thenReturn(this.resultSetMetaMock);
        Mockito.when(this.resultSetMock.getString(1))
                .thenReturn("value1").thenReturn("value11");
        Mockito.when(this.resultSetMock.getString(2))
                .thenReturn("value2").thenReturn("value22");
        Mockito.when(resultSetMock.next())
                .thenReturn(true).thenReturn(true).thenReturn(false);

        // test
        assertEquals("Should be equals!", "value1,value2", DIToolUtilities.convertResultSetToCSV(this.resultSetMock));
    }

    @Test(expected = NullPointerException.class)
    public void testConvertResultSetToCSVNullRS() throws SQLException {
        DIToolUtilities.convertResultSetToCSV(null);
    }

    @Test
    public void testConvertResultSetToCSVEmptyRS() throws SQLException {
        // prepare mocks
        Mockito.when(this.resultSetMetaMock.getColumnCount()).thenReturn(0);
        Mockito.when(this.resultSetMock.getMetaData()).thenReturn(this.resultSetMetaMock);

        // test
        assertEquals("Should be empty!", "", DIToolUtilities.convertResultSetToCSV(this.resultSetMock));
    }

    @Test
    public void testGetListsDifference() {
        List<String> list1 = Arrays.asList("value2", "value3", "value2");
        List<String> list2 = Arrays.asList("value1", "value2");

        assertEquals("Lists should be equal!", Arrays.asList("value3", "value2"), DIToolUtilities.getListsDifference(list1, list2));
    }

    @Test
    public void testGetListsDifferenceEmptyResult() {
        List<String> list1 = Arrays.asList("value2", "value1");
        List<String> list2 = Arrays.asList("value1", "value2");

        assertEquals("Result should be empty!", Collections.EMPTY_LIST, DIToolUtilities.getListsDifference(list1, list2));
    }

    @Test(expected = NullPointerException.class)
    public void testGetListsDifferenceNullLeftList() {
        DIToolUtilities.getListsDifference(null, Arrays.asList(""));
    }

    @Test(expected = NullPointerException.class)
    public void testGetListsDifferenceNullRightList() {
        DIToolUtilities.getListsDifference(Arrays.asList(""), null);
    }

    @Test
    public void testGetListsDifferenceEmptyLists() {
        assertEquals("Result should be empty!", Collections.EMPTY_LIST,
                DIToolUtilities.getListsDifference(Collections.EMPTY_LIST, Collections.EMPTY_LIST));
    }

    @Test(expected = IllegalArgumentException.class)
    // just expect exception
    public void testGetDatabaseTypeNullUrl() {
        DIToolUtilities.getDatabaseType(null);
    }

    @Test
    // expect exception with certain message
    public void testGetDatabaseTypeInvalidUrl1() {
        String invalidUri = "invalid uri";
        // expected exception (in private called method)
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage(String.format("[%s]", invalidUri));
        // test
        DIToolUtilities.getDatabaseType(invalidUri);
    }

    @Test
    public void testGetDatabaseTypeInvalidUrl2() {
        String invalidUri = "jdbc:some_db";
        // expected exception (in tested method itself)
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Unsupported database!");
        // test
        DIToolUtilities.getDatabaseType(invalidUri);
    }

    @Test
    public void testGetDatabaseTypeOracle() {
        assertEquals("Should return Oracle type!", DbType.ORACLE, DIToolUtilities.getDatabaseType("jdbc:OraCLe"));
    }

    @Test
    public void testGetDatabaseTypeSqlServer() {
        assertEquals("Should return SqlServer type!", DbType.SQLSERVER, DIToolUtilities.getDatabaseType("jdbc:sqlServeR"));
    }

    @Test
    public void testGetDatabaseTypeTeradata() {
        assertEquals("Should return Teradata type!", DbType.TERADATA, DIToolUtilities.getDatabaseType("jdbc:TERAdATa"));
    }

    @Test
    public void testGetDatabaseTypeHive() {
        assertEquals("Should return Hive type!", DbType.HIVE, DIToolUtilities.getDatabaseType("jdbc:hivE2"));
    }

    @Test
    public void testGetDatabaseTypeHanna() {
        assertEquals("Should return Hana (SAP) type!", DbType.HANA, DIToolUtilities.getDatabaseType("jdbc:SAP"));
    }

    @Test
    public void testBackQuote() {

        // test data with expected results
        List<Pair<String, String>> testData = new ArrayList<Pair<String, String>>() {{
            add(new ImmutablePair<>("", "``"));
            add(new ImmutablePair<>("   ", "`   `"));
            add(new ImmutablePair<>("   xxx    ", "`   xxx    `"));
            add(new ImmutablePair<>(null, "`null`"));
            add(new ImmutablePair<>("zzz", "`zzz`"));
            add(new ImmutablePair<>("`", "```"));
            add(new ImmutablePair<>("\"", "`\"`"));
            add(new ImmutablePair<>("'", "`'`"));
        }};

        // tests itself
        testData.forEach(pair -> assertEquals("Should be equals!",
                pair.getRight(), DIToolUtilities.backQuote(pair.getLeft())));

    }

    @Test
    public void testSingleQuote() {

        // test data with expected results
        List<Pair<String, String>> testData = new ArrayList<Pair<String, String>>() {{
            add(new ImmutablePair<>("", "''"));
            add(new ImmutablePair<>("   ", "'   '"));
            add(new ImmutablePair<>("   xxx    ", "'   xxx    '"));
            add(new ImmutablePair<>(null, "'null'"));
            add(new ImmutablePair<>("zzz", "'zzz'"));
            add(new ImmutablePair<>("`", "'`'"));
            add(new ImmutablePair<>("\"", "'\"'"));
            add(new ImmutablePair<>("'", "'''"));
        }};

        // tests itself
        testData.forEach(pair -> assertEquals("Should be equals!",
                pair.getRight(), DIToolUtilities.singleQuote(pair.getLeft())));

    }

    @Test
    public void testDoubleQuote() {

        // test data with expected results
        List<Pair<String, String>> testData = new ArrayList<Pair<String, String>>() {{
            add(new ImmutablePair<>("", "\"\""));
            add(new ImmutablePair<>("   ", "\"   \""));
            add(new ImmutablePair<>("   xxx    ", "\"   xxx    \""));
            add(new ImmutablePair<>(null, "\"null\""));
            add(new ImmutablePair<>("zzz", "\"zzz\""));
            add(new ImmutablePair<>("`", "\"`\""));
            add(new ImmutablePair<>("\"", "\"\"\""));
            add(new ImmutablePair<>("'", "\"'\""));
        }};

        // tests itself
        testData.forEach(pair -> assertEquals("Should be equals!",
                pair.getRight(), DIToolUtilities.doubleQuote(pair.getLeft())));

    }

    @Test
    public void testIsInListNullValue() {
        assertFalse("Shouldn't be found!", DIToolUtilities.isInList(null, new ArrayList<>()));
    }

    @Test(expected = NullPointerException.class)
    public void testIsInListNullInList() {
        DIToolUtilities.isInList("value", new ArrayList<String>() {{
            add(null);
        }});
    }

    @Test(expected = NullPointerException.class)
    public void testIsInListNullList() {
        DIToolUtilities.isInList("value", null);
    }

    @Test
    public void testIsInList() {

        // test data
        List<String> testData = new ArrayList<String>() {{
            add("");
            add("  ");
            add("   xxx");
            add("zzz   ");
            add("   ccc ");
            add("value");
            add("   dd ff  gg    ");
            add(" VVVVv   ");
            add("  ff  Ghh   JJJJJJ ");
            add("  iIi     ");
            add("  gg tt YYYY");
            add("VALUE");
        }};

        // tests itself
        testData.forEach(item -> assertTrue("Should be found!", DIToolUtilities.isInList(item.toLowerCase(), testData)));
    }

}
