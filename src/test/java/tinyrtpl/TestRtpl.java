package tinyrtpl;

/**
 * Created by keshen on 2016/9/7.
 */
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.HashMap;
import tinyrtpl.Mock.*;
import static tinyrtpl.Mock.*;

public class TestRtpl extends TestCase {
    public static Rtpl rtpl0 = new Rtpl(new Data(mapValueForTpl, 5), Rtpl.readFile("test-list"), "./data/");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        //TODO
    }

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
        System.out.println(rtpl0.toString());
    }
}