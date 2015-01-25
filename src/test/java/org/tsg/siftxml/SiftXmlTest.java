package org.tsg.siftxml;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SiftXmlTest {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    public InputStream newInputStream(String x) {
        return new ByteArrayInputStream(x.getBytes());
    }

    @Test
    public void rssItem() throws IOException, XmlPullParserException {
        Model.RssItem[] items = SiftXml.parse(Model.RssItem[].class, newInputStream(Model.RssItem.XML));
        assertNotNull("items", items);
        assertEquals("items length", 5, items.length);
        for (Model.RssItem item : items) {
            assertNotNull(item.title);
            // TODO(d) expand checks
        }
    }

    @Test
    public void rootParse() throws IOException, XmlPullParserException {
        User user = SiftXml.parse(User.class, newInputStream(User.XML));
        assertTrue("John Doe".equals(user.name));
        assertTrue("john@doe.com".equals(user.email));
    }

    @Test
    public void arrayParse() throws IOException, XmlPullParserException {
        Numbers numbers = SiftXml.parse(Numbers.class, newInputStream(Numbers.XML));

        assertEquals(5, numbers.ints.length);
        assertTrue(0 == numbers.ints[0]);
        assertTrue(1 == numbers.ints[1]);
        assertTrue(2 == numbers.ints[2]);
        assertTrue(3 == numbers.ints[3]);
        assertTrue(4 == numbers.ints[4]);
    }

    @Test
    public void namespace() throws IOException, XmlPullParserException {
        User2 user = SiftXml.parse(User2.class, newInputStream(User2.XML));
        assertTrue("John Doe".equals(user.name));
        assertTrue("john@doe.com".equals(user.email));
    }

    @Test
    public void headtail() throws IOException, XmlPullParserException {
        Item item = SiftXml.parse(Item.class, newInputStream(Item.XML));
        assertEquals("abcd", item.desc);
    }

    @Xml("user")
    public static class User {
        public static final String XML = "<user><name>John Doe</name><email>john@doe.com</email></user>";

        public String name;
        public String email;
    }

    @Xml("root > extra")
    public static class Numbers {
        public static final String XML = "<root><extra><int>0</int><int>1</int><int>2</int><int>3</int><int>4</int></extra></root>";

        @Xml("int")
        public Integer[] ints;
    }

    @Xml("root > sec:user")
    public static class User2 {
        public static final String XML = "<root xmlns:sec=\"http://www.w3.org/1999/xhtml\"><sec:user><name>John Doe</name><email>john@doe.com</email></sec:user></root>";

        public String name;
        public String email;
    }

    @Xml("root > item")
    public static class Item {
        public static final String XML = "<root><item><desc>abc<junk/>d</desc></item></root>";

        public String desc;
    }
}