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
		Assert.assertTrue("Daniel Skinner".equals(user.name));
		Assert.assertTrue("daniel@dasa.cc".equals(user.email));
	}
	
	@Test
	public void childParse() {
		User user = SiftXml.parse(User.class, String.format("<root><child>%s</child></root>", User.XML));
		Assert.assertTrue("Daniel Skinner".equals(user.name));
		Assert.assertTrue("daniel@dasa.cc".equals(user.email));
	}
	
	@XmlElement(name="user")
	public static class User {
		public static final String XML = "<user><name>Daniel Skinner</name><email>daniel@dasa.cc</email></user>";
		
		public String name;
		public String email;
	}

}