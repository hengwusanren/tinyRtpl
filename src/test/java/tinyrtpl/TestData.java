package tinyrtpl; /**
 * Created by keshen on 2016/9/2.
 */

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class TestData extends TestCase {
    @Test
    public void testTypeOf() {
        Assert.assertEquals(1, Data.typeOf(true));
        Assert.assertEquals(1, Data.typeOf(false));
        Assert.assertEquals(2, Data.typeOf(0));
        Assert.assertEquals(2, Data.typeOf(-1));
        Assert.assertEquals(2, Data.typeOf(2));
        Assert.assertEquals(3, Data.typeOf(0.0));
        Assert.assertEquals(3, Data.typeOf(-0.0));
        Assert.assertEquals(3, Data.typeOf(-0.1));
        Assert.assertEquals(3, Data.typeOf(-0.1f));
        Assert.assertEquals(4, Data.typeOf(""));
        Assert.assertEquals(4, Data.typeOf("1"));
        Assert.assertEquals(4, Data.typeOf("\n"));
        Assert.assertEquals(4, Data.typeOf("\""));
        HashMap<Object, Object> map = new HashMap<Object, Object>();
        map.put("1", false);
        map.put("2", 0);
        map.put(false, 0);
        map.put(3, "");
        map.put(-1.2, new HashMap<>());
        Assert.assertEquals(5, Data.typeOf(map));
        long[] arr = new long[1];
        arr[0] = 0;
        Assert.assertEquals(6, Data.typeOf(arr));
        Assert.assertEquals(7, Data.typeOf(new Data()));
        Assert.assertEquals(7, Data.typeOf(new Data(map, 5)));
        Assert.assertEquals(7, Data.typeOf(new Data(arr, 6)));
        ArrayList<Object> arrlist = new ArrayList<>();
        arrlist.add(true);
        arrlist.add(0);
        arrlist.add(1.2f);
        arrlist.add(-0.12);
        arrlist.add("");
        arrlist.add(map);
        Assert.assertEquals(7, Data.typeOf(new Data(arrlist, 6)));
    }
}