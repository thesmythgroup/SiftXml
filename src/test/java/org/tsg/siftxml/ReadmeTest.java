package org.tsg.siftxml;

import org.junit.Assert;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Sanity check on examples provided in readme.
 */
public class ReadmeTest {

    public InputStream newInputStream(String x) {
        return new ByteArrayInputStream(x.getBytes());
    }

    @Test
    public void basicExample() throws IOException, XmlPullParserException {
        String xmlString = "<root><user><name>John Doe</name><email>john@doe.com</email></user></root>";
        User user = SiftXml.sift(newInputStream(xmlString), User.class);

        Assert.assertEquals("John Doe", user.name);
        Assert.assertEquals("john@doe.com", user.email);
    }

    @Test
    public void arrays() throws IOException, XmlPullParserException {
        String xmlString = "<root><user><name>John Doe</name><email>john@doe.com</email></user><user><name>Jane Roe</name><email>jane@roe.com</email></user></root>";
        User[] users = SiftXml.sift(newInputStream(xmlString), User[].class);
        Root root = SiftXml.sift(newInputStream(xmlString), Root.class);

        Assert.assertEquals(2, users.length);
        Assert.assertEquals("John Doe", users[0].name);
        Assert.assertEquals("john@doe.com", users[0].email);
        Assert.assertEquals("Jane Roe", users[1].name);
        Assert.assertEquals("jane@roe.com", users[1].email);

        Assert.assertEquals(2, root.names.length);
        Assert.assertEquals(2, root.emails.length);
    }

    @Test
    public void xmlElement() throws IOException, XmlPullParserException {
        String xmlString = "<root><user><name>John Doe</name><email>john@doe.com</email></user></root>";
        User2 user = SiftXml.sift(newInputStream(xmlString), User2.class);

        Assert.assertEquals("John Doe", user.mName);
        Assert.assertEquals("john@doe.com", user.mEmail);
    }

    @Test
    public void xmlAttribute() throws IOException, XmlPullParserException {
        String xmlString = "<root><user uid=\"a1b2c3\"><name>John Doe</name><email>john@doe.com</email></user></root>";
        User3 user = SiftXml.sift(newInputStream(xmlString), User3.class);

        Assert.assertEquals("John Doe", user.name);
        Assert.assertEquals("john@doe.com", user.email);
        Assert.assertEquals("a1b2c3", user.uid);
    }

    @Test
    public void innerClasses() throws IOException, XmlPullParserException {
        String xmlString = "<root><user><name>John Doe</name><email>john@doe.com</email></user><user><name>Jane Roe</name><email>jane@roe.com</email></user></root>";
        Root2 root2 = SiftXml.sift(newInputStream(xmlString), Root2.class);

        Assert.assertNotNull(root2.users);
        Assert.assertEquals(2, root2.users.length);
        Assert.assertEquals("John Doe", root2.users[0].name);
        Assert.assertEquals("john@doe.com", root2.users[0].email);
        Assert.assertEquals("Jane Roe", root2.users[1].name);
        Assert.assertEquals("jane@roe.com", root2.users[1].email);
    }

	/* classes used for testing */

    @Xml("root > user")
    public static class User {
        public String name;
        public String email;
    }

@Xml("root")
public static class Root {

    @Xml("user > name")
    public String[] names;

    @Xml("user > email")
    public String[] emails;
}

    @Xml("root > user")
    public static class User2 {

        @Xml("name")
        public String mName;

        @Xml("email")
        public String mEmail;
    }

    @Xml("root > user")
    public static class User3 {

        @Xml("uid,attr")
        public String uid;

        public String name;
        public String email;
    }

    @Xml("root")
    public static class Root2 {

        @Xml("user")
        public User[] users;
    }
}