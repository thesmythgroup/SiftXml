package com.codesmyth.siftxml;

import org.junit.Before;
import org.junit.Test;

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
        assertThat(mIndex.keySet(), hasItems("User", "User > id,attr", "User > name", "User > config", "User > config > foo,attr"));
    }

    @Xml("user")
    public static class UserAnnotated {
        @Xml("id,attr")
        public int mId;

        @Xml("name")
        public String mName;
    }

    public static class User {
        @Xml(",attr")
        public int id;
        public String name;

        @Xml("config")
        public Config config;
    }

    @Xml("config")
    public static class Config {
        @Xml("foo,attr")
        public String foo;

        @Xml("bar,attr")
        public int bar;
    }
}
