package org.tsg.xml;

import java.lang.reflect.Field;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.tsg.xml.SiftXml.XmlAttribute;
import org.tsg.xml.SiftXml.XmlElement;
import org.tsg.xml.SiftXml.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class XmlHandlerTest {

	private XmlHandler mHandler;
	
	@Before
	public void setUp() {
		mHandler = new XmlHandler();
	}
	
	@After
	public void tearDown() {}
	
	@Test
	public void isAnnotated() {
		Assert.assertTrue(mHandler.isAnnotated(ClsIsAnnotated.class));
		Assert.assertTrue(!mHandler.isAnnotated(ClsNotAnnotated.class));
	}
	
	@Test
	public void allowAnyChild() throws NoSuchFieldException, SecurityException {
		Field a = ClsIsAnnotated.class.getField("fieldA");
		Field b = ClsIsAnnotated.class.getField("fieldB");
		
		Assert.assertTrue(mHandler.allowAnyChild(a));
		Assert.assertTrue(!mHandler.allowAnyChild(b));
		Assert.assertTrue(mHandler.allowAnyChild(ClsAnyChildTrue.class));
		Assert.assertTrue(!mHandler.allowAnyChild(ClsAnyChildFalse.class));
		Assert.assertTrue(!mHandler.allowAnyChild(ClsNotAnnotated.class));
	}
	
	@Test
	public void getClassHint() {
		Assert.assertEquals("cls", mHandler.getClassHint(ClsIsAnnotated.class));
		Assert.assertEquals("ClsNotAnnotated", mHandler.getClassHint(ClsNotAnnotated.class));
	}
	
	@Test
	public void getFieldHints() {
		Map<String, Field> fieldHints = mHandler.getFieldHints(ClsIsAnnotated.class);
		
		Assert.assertTrue(fieldHints.containsKey("fieldA"));
		Assert.assertTrue(fieldHints.containsKey("b"));
		Assert.assertTrue(fieldHints.containsKey("fieldC"));
		Assert.assertTrue(fieldHints.containsKey("fieldF"));
		Assert.assertSame(4, fieldHints.size());
	}
	
	@Test
	public void getAttrHints() {
		Map<String, Field> attrHints = mHandler.getAttrHints(ClsIsAnnotated.class);
		
		Assert.assertTrue(attrHints.containsKey("d"));
		Assert.assertTrue(attrHints.containsKey("e"));
		Assert.assertSame(2, attrHints.size());
	}
	
	@Test
	public void setObjectAttributes() {
		AttributesImpl attributes = new AttributesImpl();
		attributes.addAttribute("", "d", "d", "", "attr d");
		attributes.addAttribute("", "e", "e", "", "attr e");
		// TODO test prefixes
		
		ClsIsAnnotated object = new ClsIsAnnotated();
		mHandler.setObjectAttributes(object, attributes);
		
		Assert.assertNull(object.fieldA);
		Assert.assertNull(object.fieldB);
		Assert.assertNull(object.fieldC);
		Assert.assertSame("attr d", object.attrD);
		Assert.assertSame("attr e", object.attrE);
	}
	
	@Test
	public void setField() throws NoSuchFieldException, SecurityException {
		ClsIsAnnotated object = new ClsIsAnnotated();
		
		Field a = ClsIsAnnotated.class.getField("fieldA");
		Field b = ClsIsAnnotated.class.getField("fieldB");
		Field c = ClsIsAnnotated.class.getField("fieldC");
		Field f = ClsIsAnnotated.class.getField("fieldF");
		
		mHandler.setField(a, object, "field a");
		mHandler.setField(b, object, "field b");
		mHandler.setField(c, object, "field c");
		
		mHandler.setField(f, object, "field f0");
		mHandler.setField(f, object, "field f1");
		mHandler.setField(f, object, "field f2");
		
		Assert.assertEquals("field a", object.fieldA);
		Assert.assertEquals("field b", object.fieldB);
		Assert.assertEquals("field c", object.fieldC);
		
		Assert.assertNotNull(object.fieldF);
		Assert.assertEquals(3, object.fieldF.length);
		
		Assert.assertEquals("field f0", object.fieldF[0]);
		Assert.assertEquals("field f1", object.fieldF[1]);
		Assert.assertEquals("field f2", object.fieldF[2]);
	}
	
	@Test
	public void getNewInstance() throws SAXException {
		// instantiate XmlHandler members
		mHandler.startDocument();
		
		//
		Attributes attributes = new AttributesImpl();
		
		Object object1 = mHandler.getNewInstance(ClsIsAnnotated.class, attributes);
		
		Assert.assertNotNull(object1);
		Assert.assertEquals(object1.getClass(), ClsIsAnnotated.class);
		
		Object object2 = mHandler.getNewInstance(String[].class, attributes);
		
		Assert.assertNotNull(object2);
		// getNewInstance will get an array's component type for object
		// instantiation, so check against that.
		Assert.assertEquals(object2.getClass(), String.class);
		
		// TODO inspect XmlHandler members
	}
	
	@Ignore("not ready yet")
	@Test
	public void objectIndex() throws SAXException {
		mHandler.startDocument();
	}
	
	
	/* Classes for Testing */
	
	@XmlElement(name="cls")
	public static class ClsIsAnnotated {
		
		@XmlElement(anyChild=true)
		public String fieldA;
		
		@XmlElement(name="b", anyChild=false)
		public String fieldB;
		
		public String fieldC;
		
		@XmlAttribute(name="d")
		public String attrD;
		
		@XmlAttribute(name="e")
		public String attrE;
		
		public String[] fieldF;
	}
	
	class ClsNotAnnotated {}
	
	@XmlElement(name="cls", anyChild=true)
	class ClsAnyChildTrue {}
	
	@XmlElement(name="cls", anyChild=false)
	class ClsAnyChildFalse {}
}
