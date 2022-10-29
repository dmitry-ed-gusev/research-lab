package ru.yandex.qatools.xmlsearch;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.*;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;

import ru.yandex.qatools.xmlsearch.beans.Yandexsearch;

public class CommonXmlSearchTest {
    URL searchResultUrl = CommonXmlSearchTest.class.getResource("/searchresults.xml");

    //@Test
    public void testSimpleRequest() throws JAXBException, IOException {
        int resultSize = from(searchResultUrl).getResponse().getResults().getGrouping().getGroup().size();
        assertThat(resultSize, is(10));
    }

    public static Yandexsearch from(URL url) throws JAXBException, IOException {
        return unmarshal(Yandexsearch.class, url);
    }

    public static <T> T unmarshal(Class<T> clazz, URL xml) throws JAXBException, IOException {
        JAXBContext jc = JAXBContext.newInstance(clazz);
        Unmarshaller u = jc.createUnmarshaller();
        // U can use IOUtils.toInputStream(xml) if add to deps commons-io and parse String
        // Also, it can be Node, String, etc
        return u.unmarshal(new StreamSource(xml.openStream()), clazz).getValue();
    }
}