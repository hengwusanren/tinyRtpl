/**
 * Created by keshen on 2016/9/7.
 */

package tinyrtpl;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.HashMap;
import tinyrtpl.Mock.*;
import static tinyrtpl.Mock.*;

public class TestRtpl extends TestCase {
    @Test
    public void testProcessIf() {
        //TODO
    }

    @Test
    public void testProcessEach() {
        //TODO
    }

    @Test
    public void testProcessInclude() {
        //TODO
    }

    @Test
    public void testProcessGet() {
        Assert.assertEquals("{{}}", Rtpl.compile(data0, tpl0_0, false));
        Assert.assertEquals("0", Rtpl.compile(data0, tpl0_1, false));
        Assert.assertEquals("ke", Rtpl.compile(data0, tpl0_2, false));
        Assert.assertEquals("false", Rtpl.compile(data0, tpl0_3, false));
        Assert.assertEquals("4.1", Rtpl.compile(data0, tpl0_4, false));
        Assert.assertEquals("4.1", Rtpl.compile(data0, tpl0_5, false));
        Assert.assertEquals("3.0", Rtpl.compile(data0, tpl0_6, false));
        Assert.assertEquals("0.0", Rtpl.compile(data0, tpl0_7, false));
        Assert.assertEquals("this.0", Rtpl.compile(data0, tpl0_8, false));
        Assert.assertEquals("true", Rtpl.compile(data0, tpl0_9, false));
        Assert.assertEquals("shen ke", Rtpl.compile(data0, tpl0_10, false));
        Assert.assertEquals("{{if !this.2}}{{this.2}}{{else}}shen {{this.shen}}", Rtpl.compile(data0, tpl0_11, false));
    }

    @Test
    public void testRtpl0() {
        String rtpl0 = Rtpl.compile(new Data(mapValueForTpl, 5), "D:\\programs\\tinyRtpl\\data\\test-list");
        System.out.println(rtpl0);
    }
}