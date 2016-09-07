package tinyrtpl; /**
 * Created by keshen on 2016/9/2.
 */

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
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

        Assert.assertEquals(6, Data.typeOf(intValues));
        Assert.assertEquals(6, Data.typeOf(arrayListValue0));
        Assert.assertEquals(6, Data.typeOf(arrayListValue1));

        Assert.assertEquals(7, Data.typeOf(new Data()));
        Assert.assertEquals(7, Data.typeOf(new Data(mapValue0, 5)));
        Assert.assertEquals(7, Data.typeOf(new Data(arrayListValue0, 6)));
    }

    @Test
    public void testToString() {

    }
}