package org.tsg.siftxml;

import org.junit.Assert;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

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

        assertEquals("John Doe", user.name);
        assertEquals("john@doe.com", user.email);
    }

    @Test
    public void arrays() throws IOException, XmlPullParserException {
        String xmlString = "<root><user><name>John Doe</name><email>john@doe.com</email></user><user><name>Jane Roe</name><email>jane@roe.com</email></user></root>";
        User[] users = SiftXml.sift(newInputStream(xmlString), User[].class);
        Root root = SiftXml.sift(newInputStream(xmlString), Root.class);

        assertEquals(2, users.length);
        assertEquals("John Doe", users[0].name);
        assertEquals("john@doe.com", users[0].email);
        assertEquals("Jane Roe", users[1].name);
        assertEquals("jane@roe.com", users[1].email);

        assertEquals(2, root.names.length);
        assertEquals(2, root.emails.length);
    }

    @Test
    public void xmlElement() throws IOException, XmlPullParserException {
        String xmlString = "<root><user><name>John Doe</name><email>john@doe.com</email></user></root>";
        User2 user = SiftXml.sift(newInputStream(xmlString), User2.class);

        assertEquals("John Doe", user.mName);
        assertEquals("john@doe.com", user.mEmail);
    }

    @Test
    public void xmlAttribute() throws IOException, XmlPullParserException {
        String xmlString = "<root><user uid=\"a1b2c3\"><name>John Doe</name><email>john@doe.com</email></user></root>";
        User3 user = SiftXml.sift(newInputStream(xmlString), User3.class);

        assertEquals("John Doe", user.name);
        assertEquals("john@doe.com", user.email);
        assertEquals("a1b2c3", user.uid);
    }

    @Test
    public void innerClasses() throws IOException, XmlPullParserException {
        String xmlString = "<root><user><name>John Doe</name><email>john@doe.com</email></user><user><name>Jane Roe</name><email>jane@roe.com</email></user></root>";
        Root2 root2 = SiftXml.sift(newInputStream(xmlString), Root2.class);

        Assert.assertNotNull(root2.users);
        assertEquals(2, root2.users.length);
        assertEquals("John Doe", root2.users[0].name);
        assertEquals("john@doe.com", root2.users[0].email);
        assertEquals("Jane Roe", root2.users[1].name);
        assertEquals("jane@roe.com", root2.users[1].email);
    }

    @Test
    public void methods() throws IOException, XmlPullParserException {
        String xmlString = "<root><user uid=\"a1b2c3\"><name>John Doe</name><email>john@doe.com</email></user></root>";
        UserWithMethods user = SiftXml.sift(newInputStream(xmlString), UserWithMethods.class);

        assertEquals("John Doe", user.getName());
        assertEquals("john@doe.com", user.getEmail());
        assertEquals("a1b2c3", user.getUid());
    }

	/* classes used for testing */

    @Xml("root > user")
    public static class User {
        public String name;
        public String email;
    }

    @Xml("root > user")
    public static class UserWithMethods {
        private String mUid;
        private String mName;
        private String mEmail;

        public String getUid() {
            return mUid;
        }

        @Xml("uid,attr")
        public void setUid(String uid) {
            mUid = uid;
        }

        public String getName() {
            return mName;
        }

        @Xml("name")
        public void setName(String name) {
            mName = name;
        }

        public String getEmail() {
            return mEmail;
        }

        @Xml("email")
        public void setEmail(String email) {
            mEmail = email;
        }
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