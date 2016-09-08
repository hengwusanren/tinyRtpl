/**
 * Created by keshen on 2016/9/2.
 */

package tinyrtpl;

import junit.framework.TestCase;
import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.Arrays;

import java.util.ArrayList;
import java.util.HashMap;

import static tinyrtpl.Mock.*;

public class TestData extends TestCase {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        System.out.println("beforeClass...");
    }

    @Test
    public void testTypeOf() {
        Assert.assertEquals(1, Data.typeOf(true));
        Assert.assertEquals(1, Data.typeOf(false));
        for(int i : intValues) {
            Assert.assertEquals(2, Data.typeOf(i));
        }
        for(float i : floatValues) {
            Assert.assertEquals(3, Data.typeOf(i));
        }
        for(double i : doubleValues) {
            Assert.assertEquals(3, Data.typeOf(i));
        }
        for(String i : stringValues) {
            Assert.assertEquals(4, Data.typeOf(i));
        }
        Assert.assertEquals(5, Data.typeOf(mapValue0));
        Assert.assertEquals(5, Data.typeOf(mapValue1));

        Assert.assertEquals(-1, Data.typeOf(intValues));
        Assert.assertEquals(6, Data.typeOf(Arrays.asList(intValues)));
        Assert.assertEquals(6, Data.typeOf(arrayListValue0));
        Assert.assertEquals(6, Data.typeOf(arrayListValue1));

        Assert.assertEquals(7, Data.typeOf(new Data()));
        Assert.assertEquals(7, Data.typeOf(new Data(mapValue0, 5)));
        Assert.assertEquals(7, Data.typeOf(new Data(arrayListValue0, 6)));
    }

    @Test
    public void testToString() {
        for(boolean i : booleanValues)
            Assert.assertEquals(Boolean.toString(i), new Data(i, 1).toString());
        for(int i : intValues)
            Assert.assertEquals(Integer.toString(i), new Data(i, 2).toString());
        for(double i : doubleValues)
            Assert.assertEquals(Double.toString(i), new Data(i, 3).toString());
        for(String i : stringValues)
            Assert.assertEquals(i, new Data(i, 4).toString());
        Assert.assertEquals("", new Data(mapValue0, 5).toString());
        Assert.assertEquals("", new Data(mapValue1, 5).toString());
        Assert.assertEquals("", new Data(mapValueForTpl, 5).toString());
        Assert.assertEquals("", new Data(arrayListValue0, 6).toString());
        Assert.assertEquals("", new Data(arrayListValue1, 6).toString());
        Assert.assertEquals("", new Data(Arrays.asList(objectArr), 6).toString());

        Assert.assertEquals("", new Data("", 4).toString());
        Assert.assertEquals("", new Data("", 4).toString());
        Assert.assertEquals("\"", new Data("\"", 4).toString());

        // string is "
        // 123 abc\
        // "
        Assert.assertEquals("string is \"\n123 abc\\\r\"", new Data("string is \"\n123 abc\\\r\"", 4).toString());
        Assert.assertEquals("true", new Data(true, 1).toString());
        Assert.assertEquals("null", new Data(null).toString());
        Assert.assertEquals("0", new Data(0, 2).toString());
    }

    @Test
    public void testToJsonString() {
        for(boolean i : booleanValues)
            Assert.assertEquals(Boolean.toString(i), new Data(i, 1).toJsonString());
        for(int i : intValues)
            Assert.assertEquals(Integer.toString(i), new Data(i, 2).toJsonString());
        for(double i : doubleValues)
            Assert.assertEquals(Double.toString(i), new Data(i, 3).toJsonString());
        for(String i : stringValues)
            Assert.assertEquals("\"" + StringEscapeUtils.escapeJson(i) + "\"", new Data(i, 4).toJsonString());
        Assert.assertEquals("{\"1\":false,\"2\":0,\"3\":\"\",\"true\":0,\"-1.2\":\"\\\"\\n\\r\\n123 qwe.wer.14\\\" --=$\\\"\",\"\\\" \\n\\r\":null}",
                new Data(mapValue0, 5).toJsonString());
        Assert.assertEquals("{}", new Data(mapValue2, 5).toJsonString());
        Assert.assertEquals("[]", new Data(Arrays.asList(objectArr), 6).toJsonString());
        Assert.assertEquals("[0,0.0,-1.0,2.1,12,-16]", new Data(Arrays.asList(0, 0.0f, -1.0f, 2.1f, 12, -16), 6).toJsonString());

        Assert.assertEquals("\"\"", new Data("", 4).toJsonString());
        Assert.assertEquals("\"\\\"\"", new Data("\"", 4).toJsonString());

        // string is "
        // 123 abc\
        // "
        Assert.assertEquals("\"string is \\\"\\n123 abc\\\\\\r\\\"\"", new Data("string is \"\n123 abc\\\r\"", 4).toJsonString());
        Assert.assertEquals("true", new Data(true, 1).toJsonString());
        Assert.assertEquals("\"true\"", new Data("true", 4).toJsonString());
        Assert.assertEquals("null", new Data(null).toJsonString());
        Assert.assertEquals("0", new Data(0, 2).toJsonString());
        Assert.assertEquals("\"0\"", new Data("0", 4).toJsonString());
    }

    @Test
    public void testEquals() {
        HashMap<Object, Object> map = new HashMap<>();
        Data data = new Data();
        Assert.assertEquals(new Data(map, 5), data);
        map.put("1", false);
        data.put("1", false);
        Assert.assertEquals(new Data(map, 5), data);
        map.put("2", 0);
        data.put("2", 0);
        Assert.assertEquals(new Data(map, 5), data);
        map.put(true, 0);
        data.put(true, 0);
        Assert.assertEquals(new Data(map, 5), data);
        map.put(3, "");
        data.put(3, "");
        Assert.assertEquals(new Data(map, 5), data);
        map.put(-1.2, "\"\n\r\n123 qwe.wer.14\" --=$\"");
        data.put(-1.2, "\"\n\r\n123 qwe.wer.14\" --=$\"");
        Assert.assertEquals(new Data(map, 5), data);
        map.put("\" \n\r", null);
        data.put("\" \n\r", null);
        Assert.assertEquals(new Data(map, 5), data);
        map.put("array", arrayListValue0);
        data.put("array", arrayListValue0);
        Assert.assertEquals(new Data(map, 5), data);
//        System.out.println(data.toString());
//        System.out.println(data.toJsonString());

        ArrayList<Object> array = new ArrayList<>();
        data = new Data(new ArrayList<>(), 6);
        Assert.assertEquals(new Data(array, 6), data);
        array.add(1);
        data.add(1);
        Assert.assertEquals(new Data(array, 6), data);
        array.add(false);
        data.add(false);
        Assert.assertEquals(new Data(array, 6), data);
        array.add(0.0);
        data.add(0.0);
        Assert.assertEquals(new Data(array, 6), data);
        array.add(0.0f);
        data.add(0.0f);
        Assert.assertEquals(new Data(array, 6), data);
        array.add(" ");
        data.add(" ");
        Assert.assertEquals(new Data(array, 6), data);
        array.add(mapValue0);
        data.add(mapValue0);
        Assert.assertEquals(new Data(array, 6), data);
    }
}