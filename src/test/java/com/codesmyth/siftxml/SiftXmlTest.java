package com.codesmyth.siftxml;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

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
  public void reply() throws IOException, XmlPullParserException {
    Session session = (new SiftXml()).parse(newInputStream(Session.XML), Session.class);
    assertNotNull(session);
    assertEquals("user name", "D", session.user.name);
    assertEquals("client name", "ABC Company", session.user.client);

    assertNotNull("documents", session.docs);
    assertEquals("document length", 3, session.docs.length);
    assertEquals("1414565", session.docs[0].jobId);
    assertEquals("Passport", session.docs[0].desc);
    assertEquals("Pending", session.docs[0].status);
  }

  @Xml("Reply")
  public static class Session {
    public static final String XML = "<Reply><SessionUser Name=\"D\" Client=\"ABC Company\" UserType=\"Applicant\"/><DocumentsNeeded><Document><JobID>1414565</JobID><Description>Passport</Description><Status>Pending</Status></Document><Document><JobID>1414566</JobID><Description>Drivers License</Description><Status>Pending</Status></Document><Document><JobID>1414567</JobID><Description>Social Security Card</Description><Status>Pending</Status></Document></DocumentsNeeded></Reply>";

    @Xml("DocumentsNeeded > Document")
    public Document[] docs;

    @Xml("SessionUser")
    public SessionUser user;

    public String qrcode = "";
  }

  @Xml("SessionUser")
  public static class SessionUser {
    @Xml("Name,attr")
    public String name;

    @Xml("UserType,attr")
    public String type;

    @Xml("Client,attr")
    public String client;
  }

  @Xml("DocumentsNeeded > Document")
  public static class Document {
    @Xml("JobID")
    public String jobId;

    @Xml("Description")
    public String desc;

    @Xml("Status")
    public String status;

    @Xml("TimeRemaining")
    public String timeRemaining;
  }

  @Test
  public void rssItem() throws IOException, XmlPullParserException {
    Model.RssItem[] items = SiftXml.sift(newInputStream(Model.RssItem.XML), Model.RssItem[].class);
    assertNotNull("items", items);
    assertEquals("items length", 5, items.length);
    for (Model.RssItem item : items) {
      assertNotNull(item.title);
      // TODO(d) expand checks
    }
  }

  @Test
  public void rootParse() throws IOException, XmlPullParserException {
    User user = SiftXml.sift(newInputStream(User.XML), User.class);
    assertTrue("John Doe".equals(user.name));
    assertTrue("john@doe.com".equals(user.email));
  }

  @Test
  public void arrayParse() throws IOException, XmlPullParserException {
    Numbers numbers = SiftXml.sift(newInputStream(Numbers.XML), Numbers.class);

    assertEquals(5, numbers.ints.length);
    assertTrue(0 == numbers.ints[0]);
    assertTrue(1 == numbers.ints[1]);
    assertTrue(2 == numbers.ints[2]);
    assertTrue(3 == numbers.ints[3]);
    assertTrue(4 == numbers.ints[4]);
  }

  @Test
  public void namespace() throws IOException, XmlPullParserException {
    User2 user = SiftXml.sift(newInputStream(User2.XML), User2.class);
    assertTrue("John Doe".equals(user.name));
    assertTrue("john@doe.com".equals(user.email));
  }

  @Test
  public void headtail() throws IOException, XmlPullParserException {
    Item item = SiftXml.sift(newInputStream(Item.XML), Item.class);
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
    public static final String XML = "<root a=\"b\"><extra><int>0</int><int>1</int><int>2</int><int>3</int><int>4</int></extra></root>";

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

  @Test
  public void body() throws IOException, XmlPullParserException {
    Body body = SiftXml.sift(newInputStream(Body.XML), Body.class);
//    assertEquals("hello text", "World &lt;&gt;&apos;&quot; &#x767d;&#40300;翔", body.hello);
    assertEquals("hello lang", "en", body.lang);
  }

  @Xml("body")
  public static class Body {

    @Xml("hello")
    public String hello;

    @Xml("hello > lang,attr")
    public String lang;

    public static final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n" +
        "  \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
        "<body xmlns:foo=\"ns1\" xmlns=\"ns2\" xmlns:tag=\"ns3\">\n" +
        "  <hello lang=\"en\">World &lt;&gt;&apos;&quot; &#x767d;&#40300;翔</hello>\n" +
        "  <query>&何; &is-it;</query>\n" +
        "  <goodbye />\n" +
        "  <outer foo:attr=\"value\" xmlns:tag=\"ns4\">\n" +
        "    <inner/>\n" +
        "  </outer>\n" +
        "  <tag:name>\n" +
        "    <![CDATA[Some text here.]]>\n" +
        "  </tag:name>\n" +
        "</body><!-- missing final newline -->";
  }
}