# SiftXml

SiftXml is a lightweight xml to java mapper parsed by kXML2 and using a minimal DSL inspired by the Go stdlib
encoding/xml package. Regardless of the number of nested nodes, it allows you to sift out only the information you're
interested in without having to design complex models.

# Usage

Mapping is done by calling `new SiftXml().parse(InputStream, Class);`.

Additional options can be configured before calling `.parse()` as so:

```
new SiftXml()
    .setFeature(XmlPullParser.FEATURE_PROCESS_DOCDECL, true)
    .setFeature(XmlPullParser.FEATURE_VALIDATION, true)
    .parse(InputStream, Class);
```

## Gradle

```
repositories {
    ivy {
        url 'https://raw.github.com/thesmythgroup/SiftXml/master/repo/'
    }
}

dependencies {
    compile "org.tsg.siftxml:siftxml:0.1-SNAPSHOT"
}
```

## Basic

### Xml

```
<root>
    <user>
        <name>John Doe</name>
        <email>john@doe.com</email>
    </user>
</root>
```

### Java

```
@Xml("root > user")
public class User {
    public String name;
    public String email;
}
```

### Parse

```
User user = new SiftXml().parse(in, User.class);
```

## Arrays

### Xml

```
<root>
    <user>
        <name>John Doe</name>
        <email>john@doe.com</email>
    </user>
    <user>
        <name>Jane Roe</name>
        <email>jane@roe.com</email>
    </user>
</root>
```

### Java

```
@Xml("root")
public class Root {

    @Xml("user > name")
    public String[] names;

    @Xml("user > email")
    public String[] emails;
}
```

### Parse

```
Root root = new SiftXml().parse(in, Root.class);
// the User class from the basic example above also works
User[] users = new SiftXml()parse(in, User[].class);
```

## Inner Classes

Inner classes should be declared `static` so they are made available to the mapper.

## Type Coercion

The type of a field is used when setting the value. Any class with a constructor that accepts a single String argument
can be used. As an example, if a node's text was 1234 and a model's member was of type Integer, Integer.parseInt would
be used when setting the member's value.
