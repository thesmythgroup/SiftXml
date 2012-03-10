package org.tsg.xml;

import junit.framework.Assert;

import org.junit.Test;
import org.tsg.xml.SiftXml.XmlAttribute;
import org.tsg.xml.SiftXml.XmlElement;

/**
 * Sanity check on examples provided in readme to check typos.
 * 
 * @author Daniel Skinner <daniel@dasa.cc>
 *
 */
public class ReadmeTest {
	
	@Test
	public void basicExample() {
		String xmlString = "<root><user><name>John Doe</name><email>john@doe.com</email></user></root>";
		User user = SiftXml.parse(User.class, xmlString);
		
		Assert.assertEquals("John Doe", user.name);
		Assert.assertEquals("john@doe.com", user.email);
	}
	
	@Test
	public void nestedNodes() {
		String xmlString = "<root><junk><user><kittens><puddle><name>John Doe</name></puddle></kittens><why><email>john@doe.com</email></why></user></junk></root>";
		UserAnyChild user = SiftXml.parse(UserAnyChild.class, xmlString);
		
		Assert.assertEquals("John Doe", user.name);
		Assert.assertEquals("john@doe.com", user.email);
	}
	
	@Test
	public void arrays() {
		String xmlString = "<root><user><name>John Doe</name><email>john@doe.com</email></user><user><name>Jane Roe</name><email>jane@roe.com</email></user></root>";
		User[] users = SiftXml.parse(User[].class, xmlString);
	    Root root = SiftXml.parse(Root.class, xmlString);
	    
	    Assert.assertEquals(2, users.length);
	    Assert.assertEquals("John Doe", users[0].name);
		Assert.assertEquals("john@doe.com", users[0].email);
		Assert.assertEquals("Jane Roe", users[1].name);
		Assert.assertEquals("jane@roe.com", users[1].email);
		
		Assert.assertEquals(2, root.names.length);
		Assert.assertEquals(2, root.emails.length);
	}
	
	@Test
	public void xmlElement() {
		String xmlString = "<root><user><name>John Doe</name><email>john@doe.com</email></user></root>";
		User2 user = SiftXml.parse(User2.class, xmlString);
		
		Assert.assertEquals("John Doe", user.mName);
		Assert.assertEquals("john@doe.com", user.mEmail);
	}
	
	@Test
	public void xmlAttribute() {
		String xmlString = "<root><user uid=\"a1b2c3\"><name>John Doe</name><email>john@doe.com</email></user></root>";
		User3 user = SiftXml.parse(User3.class, xmlString);
		
		Assert.assertEquals("John Doe", user.name);
		Assert.assertEquals("john@doe.com", user.email);
		Assert.assertEquals("a1b2c3", user.uid);
	}
	
	@Test
	public void innerClasses() {
		String xmlString = "<root><user><name>John Doe</name><email>john@doe.com</email></user><user><name>Jane Roe</name><email>jane@roe.com</email></user></root>";
		Root2 root2 = SiftXml.parse(Root2.class, xmlString);
		
		Assert.assertNotNull(root2.users);
		Assert.assertEquals(2, root2.users.length);
		Assert.assertEquals("John Doe", root2.users[0].name);
		Assert.assertEquals("john@doe.com", root2.users[0].email);
		Assert.assertEquals("Jane Roe", root2.users[1].name);
		Assert.assertEquals("jane@roe.com", root2.users[1].email);
	}
	
	/* classes used for testing */
	
	@XmlElement(name="user")
    public static class User {
        public String name;
        public String email;
    }
	
	@XmlElement(name="user")
    public static class UserAnyChild {
		
		@XmlElement(anyChild=true)
        public String name;
		
		@XmlElement(anyChild=true)
        public String email;
    }
	
	@XmlElement(name="root")
    public static class Root {
    
        @XmlElement(name="name", anyChild=true)
        public String[] names;
        
        @XmlElement(name="email", anyChild=true)
        public String[] emails;
    }
	
	@XmlElement(name="user")
    public static class User2 {
        
        @XmlElement(name="name")
        public String mName;
        
        @XmlElement(name="email")
        public String mEmail;
    }
	
	@XmlElement(name="user")
    public static class User3 {
        
    	@XmlAttribute(name="uid")
    	public String uid;
    	
        public String name;
        public String email;
    }
	
	@XmlElement(name="root")
    public static class Root2 {
        
        @XmlElement(name="user")
        public User[] users;
    }
}
