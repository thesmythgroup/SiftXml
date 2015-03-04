package com.codesmyth.siftxml;

import com.codesmyth.siftxml.Model.RssItem;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class SiftXmlBench {

  @Benchmark
  public User parseUser() throws IOException, XmlPullParserException {
    InputStream in = new ByteArrayInputStream(User.XML.getBytes());
    return SiftXml.sift(in, User.class);
  }

  @Benchmark
  public RssItem[] parserRssItem() throws IOException, XmlPullParserException {
    return SiftXml.sift(new ByteArrayInputStream(RssItem.XML.getBytes()), RssItem[].class);
  }

  @Xml("user")
  public static class User {
    public static final String XML = "<user><name>John Doe</name><email>john.doe@email.com</email></user>";
    public String name;
    public String email;
  }
}
