package org.tsg.siftxml;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class IndexTest {

    Index mIndex;

    @Before
    public void setUp() {
        mIndex = new Index();
    }

    @Test
    public void getClassHint() {
        assertEquals("UserAnnotated.class", "user", mIndex.getClassHint(UserAnnotated.class));
        assertEquals("User.class", "User", mIndex.getClassHint(User.class));
    }

    @Test
    public void isAnnotated() {
        assertEquals("UserAnnotated.class", true, mIndex.isAnnotated(UserAnnotated.class));
        assertEquals("User.class", false, mIndex.isAnnotated(User.class));
    }

    @Test
    public void create() {
        mIndex.create(UserAnnotated.class);
        assertThat(mIndex.keySet(), hasItems("user", "user > id,attr", "user > name"));

        mIndex.clear();

        mIndex.create(User.class);
        assertThat(mIndex.keySet(), hasItems("User", "User > id,attr", "User > name"));
    }

    @Xml("user")
    class UserAnnotated {
        @Xml("id,attr")
        public int mId;

        @Xml("name")
        public String mName;
    }

    class User {
        @Xml(",attr")
        public int id;
        public String name;
    }

}
