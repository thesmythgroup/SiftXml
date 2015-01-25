package org.tsg.siftxml;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SiftXml {

    public static <T> T parse(InputStream in, Class<T> cls) throws IOException, XmlPullParserException {
        T result = null;
        try {
            result = new SiftXml().sift(in, cls);
        } finally {
            in.close();
        }
        return result;
    }

    private Index mIndex = new Index();
    private Tracker mTracker = new Tracker();

    private XmlPullParser mParser;

    public SiftXml() {
        mParser = new KXmlParser();
    }

    public SiftXml setFeature(String feature, boolean value) throws XmlPullParserException {
        mParser.setFeature(feature, true);
        return this;
    }

    public <T> T sift(InputStream in, Class<T> cls) throws IOException, XmlPullParserException {
        mParser.setInput(in, null);

        if (cls.isArray()) {
            mIndex.create(cls.getComponentType());
        } else {
            mIndex.create(cls);
        }

        List<T> results = new ArrayList<T>();
        LinkedList<Object> queue = new LinkedList<Object>();
        Object current = null;

        Accumulator acc = new Accumulator();

        Index.Entry entry;
        String tag = "";

        while (mParser.next() != XmlPullParser.END_DOCUMENT) {
            switch (mParser.getEventType()) {
                case XmlPullParser.START_TAG:
                    tag = mTracker.track(mParser.getDepth(), mParser.getName());
                    entry = mIndex.match(tag);
                    if (entry.cls != null) {
                        Object newCurrent = getNewInstance(entry.cls, mParser);
                        queue.push(newCurrent);

                        if (queue.size() == 1) {
                            results.add((T) newCurrent);
                        } else if (entry.field != null) { // the class just instantiated is a field on mCurrent
                            try {
                                setField(entry.field, current, newCurrent);
                            } catch (IllegalAccessException e) {
                                throw new XmlPullParserException(e.getMessage(), mParser, e);
                            }
                        }

                        current = newCurrent;
                    }
                    break;
                case XmlPullParser.END_TAG:
                    tag = mTracker.toString();
                    entry = mIndex.match(tag);
                    if (entry.isMember()) {
                        String x = acc.read(tag);
                        Field field = entry.field;
                        try {
                            setField(field, current, makeType(field, x));
                        } catch (IllegalAccessException e) {
                            throw new XmlPullParserException(e.getMessage(), mParser, e);
                        }
                    } else if (entry.isNested() || entry.isRoot()) {
                        queue.pop();
                        current = queue.peek();
                    } else if (entry.isEmpty()) {
                        acc.discard(tag);
                    }
                    mTracker.trim(mParser.getDepth() - 1);
                    break;
                case XmlPullParser.TEXT:
                    tag = mTracker.toString();
                    if (mIndex.contains(tag)) {
                        acc.add(tag, mParser.getText());
                    }
                    break;
                default:
                    throw new XmlPullParserException("Internal error, unhandled event type: " + mParser.getEventType());
            }
        }

        // finalize result
        Object result = null;
        if (cls.isArray()) {
            result = results.toArray((Object[]) Array.newInstance(cls.getComponentType(), results.size()));
        } else if (results.size() != 0) {
            result = results.get(0);
        }
        return (T) result;
    }

    /**
     * Create new instance of field type, passing string value to constructor.
     */
    protected Object makeType(Field field, String value) throws XmlPullParserException {
        Object obj = null;

        Class fieldType = field.getType();
        if (fieldType.isArray()) {
            fieldType = fieldType.getComponentType();
        }

        try {
            Constructor ct = fieldType.getConstructor(new Class[]{String.class});
            obj = ct.newInstance(new Object[]{value.trim()});
        } catch (InstantiationException e) {
            throw new XmlPullParserException(e.getMessage(), mParser, e);
        } catch (IllegalAccessException e) {
            throw new XmlPullParserException(e.getMessage(), mParser, e);
        } catch (InvocationTargetException e) {
            throw new XmlPullParserException(e.getMessage(), mParser, e);
        } catch (NoSuchMethodException e) {
            throw new XmlPullParserException(e.getMessage(), mParser, e);
        }

        return obj;
    }

    /**
     * Given object, set attribute values on object members.
     */
    protected void setObjectAttributes(Object object, XmlPullParser parser) throws XmlPullParserException {
        if (parser.getAttributeCount() == 0) {
            return;
        }

        for (int i = 0; i < parser.getAttributeCount(); i++) {
            String key = parser.getAttributeName(i);
            // TODO(d) still need qualified name code here? Did with sax parser depending on android API version.

            Index.Entry obj = mIndex.match(mTracker.toString() + " > " + key + ",attr");
            if (!obj.isEmpty()) {
                Field field = obj.field;
                try {
                    field.set(object, parser.getAttributeValue(i));
                } catch (Exception e) {
                    throw new XmlPullParserException(e.getMessage(), parser, e);
                }
            }
        }
    }


    /**
     * Set field on object to value. If field is an array, it will be
     * initialized if null or extended to accommodate new value.
     */
    protected void setField(Field field, Object object, Object value) throws IllegalAccessException {
        Class fieldCls = field.getType();

        if (fieldCls.isArray()) {
            Object arr = field.get(object);
            int len = 1;

            if (arr == null) {
                arr = Array.newInstance(fieldCls.getComponentType(), len);
            } else {
                len = Array.getLength(arr);
                Object tmp = Array.newInstance(fieldCls.getComponentType(), len + 1);
                System.arraycopy(arr, 0, tmp, 0, len);
                arr = tmp;
                len++;
            }

            Array.set(arr, len - 1, value);
            value = arr;
        }

        field.set(object, value);
    }

    /**
     * Get new instance of class, setting any attributes if available. If is
     * array class, will get component type and return that instead.
     */
    protected Object getNewInstance(Class cls, XmlPullParser parser) throws XmlPullParserException {
        Object object = null;

        if (cls.isArray()) {
            cls = cls.getComponentType();
        }

        try {
            object = cls.newInstance();
            setObjectAttributes(object, parser);
        } catch (InstantiationException e) {
            throw new XmlPullParserException(String.format("Failed to instantiate class. Note, inner classes must be declared static. %s", e.getMessage()), parser, e);
        } catch (IllegalAccessException e) {
            throw new XmlPullParserException(e.getMessage(), parser, e);
        }

        return object;
    }

}
