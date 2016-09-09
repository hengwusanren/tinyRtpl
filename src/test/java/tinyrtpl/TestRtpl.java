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
        //TODO
    }

    @Test
    public void testRtpl0() {
        String rtpl0 = Rtpl.compile(new Data(mapValueForTpl, 5), "D:\\programs\\tinyRtpl\\data\\test-list");
        System.out.println(rtpl0);
    }
}