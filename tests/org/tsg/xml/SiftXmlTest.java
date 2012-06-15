package org.tsg.xml;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.tsg.xml.SiftXml;
import org.tsg.xml.SiftXml.XmlElement;

public class SiftXmlTest {

	@Before
	public void setUp() {}

	@After
	public void tearDown() {}

	@Test
	public void rootParse() {
		User user = SiftXml.parse(User.class, User.XML);
		Assert.assertTrue("John Doe".equals(user.name));
		Assert.assertTrue("john@doe.com".equals(user.email));
	}

	@Test
	public void childParse() {
		User user = SiftXml.parse(User.class, String.format("<root><child>%s</child></root>", User.XML));
		Assert.assertTrue("John Doe".equals(user.name));
		Assert.assertTrue("john@doe.com".equals(user.email));
	}

	@Test
	public void anyChildMember() {
		Numbers numbers = SiftXml.parse(Numbers.class, Numbers.XML);

		Assert.assertEquals(5, numbers.ints.length);
		Assert.assertTrue(0 == numbers.ints[0]);
		Assert.assertTrue(1 == numbers.ints[1]);
		Assert.assertTrue(2 == numbers.ints[2]);
		Assert.assertTrue(3 == numbers.ints[3]);
		Assert.assertTrue(4 == numbers.ints[4]);
	}

	@Test
	public void namespace() {
		User2 user = SiftXml.parse(User2.class, User2.XML);
		Assert.assertTrue("John Doe".equals(user.name));
		Assert.assertTrue("john@doe.com".equals(user.email));
	}

	@Test
	public void headtail() {
		Item item = SiftXml.parse(Item.class, Item.XML);
		Assert.assertEquals("abcd", item.desc);
	}

	@XmlElement(name = "user")
	public static class User {
		public static final String XML = "<user><name>John Doe</name><email>john@doe.com</email></user>";

		public String name;
		public String email;
	}

	@XmlElement(name = "root")
	public static class Numbers {
		public static final String XML = "<root><junk><shoe><int>0</int><int>1</int><stub><int>2</int></stub><int>3</int></shoe></junk><extra><int>4</int></extra></root>";

		@XmlElement(name = "int", anyChild = true)
		public Integer[] ints;
	}

	@XmlElement(name = "user")
	public static class User2 {
		public static final String XML = "<root xmlns:sec=\"http://www.w3.org/1999/xhtml\"><sec:user><name>John Doe</name><email>john@doe.com</email></sec:user></root>";

		public String name;
		public String email;
	}

	@XmlElement(name = "item")
	public static class Item {
		public static final String XML = "<root><item><desc>abc<junk/>d</desc></item></root>";

		public String desc;
	}
}
