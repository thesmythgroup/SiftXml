Overview
========
SiftXml is lightweight parser for converting xml into java objects in an
elegant fashion. Regardless of the number of nested nodes, it allows
you to sift out only the information you're interested in without having to
design complex models.

Performance
===========
Performance has not yet been measured, though it has seen use in
multiple projects on Android with success. Tests are planned for the future.

Using SiftXml
=============
Parsing is done by calling SiftXml.parse(...) with the needed signature. You
can provide an XMLReader object for use, such as TagSoup for parsing malformed
xml/html.

Basic Example
-------------
Some xml::

    <root>
        <user>
            <name>John Doe</name>
            <email>john@doe.com</email>
        </user>
    </root>

A model class::

    @XmlElement(name="user")
    public class User {
        public String name;
        public String email;
    }

Parsing the result::

    User user = SiftXml.parse(User.class, xmlString);


Nested Nodes
------------
Some xml::

    <root>
        <junk>
            <user>
                <kittens>
                    <puddle>
                        <name>John Doe</name>
                    </puddle>
                </kittens>
                <why>
                    <email>john@doe.com</email>
                </why>
            </user>
        </junk>
    </root>

A model class::

    @XmlElement(name="user")
    public class User {
    
    	@XmlElement(anyChild=true)
        public String name;
        
        @XmlElement(anyChild=true)
        public String email;
    }

And parsing the result::

    User user = SiftXml.parse(User.class, xmlString);


Arrays
------
Some xml::

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

Model classes that could be used::

    @XmlElement(name="user")
    public class User {
        public String name;
        public String email;
    }
    
    @XmlElement(name="root")
    public class Root {
    
        @XmlElement(name="name")
        public String[] names;
        
        @XmlElement(name="email")
        public String[] emails;
    }

Parsing the result::

    User[] users = SiftXml.parse(User[].class, xmlString);
    Root root = SiftXml.parse(Root.class, xmlString);


XmlElement
----------
This annotation can be used on a class or its members. The 'name' argument
provides a hint to the parser for what node to match against. If this
annotation is not present, then the class' simple name or field's name is used
for the hint.

Using the examples above, an altered model could look like the following::

    @XmlElement(name="user")
    public class User2 {
        
        @XmlElement(name="name")
        public String mName;
        
        @XmlElement(name="email")
        public String mEmail;
    }


XmlAttribute
------------
This annotation is for grabbing the value of a node's attribute. A related
class is required for the match.

Some xml::

    <root>
        <user uid="a1b2c3">
            <name>John Doe</name>
            <email>john@doe.com</email>
        </user>
    </root>

A model class::

    @XmlElement(name="user")
    public class User3 {
        
    	@XmlAttribute(name="uid")
    	public String uid;
    	
        public String name;
        public String email;
    }

Inner Classes
-------------
Inner classes should be declared 'public static' so they are available to the
parser.

Some xml::

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

Model classes::

    @XmlElement(name="root")
    public class Root2 {
        
        @XmlElement(name="user")
        public User[] users;
        
        @XmlElement(name="user")
        public static class User {
            public String name;
            public String email;
        }
    }

Note that both the Root.users member the the User class are both annotated with
XmlElement. Currently this is required but might be avoidable in the future.

Type Coercion
-------------
The type of a field is used when setting the value. Any class with a
constructor that accepts a single String argument can be used. As an example,
if a node's text was 1234 and a model's member was of type Integer,
Integer.parseInt would be used when setting the member's value.

