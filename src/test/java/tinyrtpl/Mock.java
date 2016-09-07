package tinyrtpl;

/**
 * Created by keshen on 2016/9/7.
 */

import java.util.ArrayList;
import java.util.HashMap;

public class Mock {
    public static boolean[] booleanValues = {false, true};
    public static int[] intValues = {0, -1, 2, -9, 100, -2147483648, 2147483647};
    public static long[] longValues = {0, -1, 2, -9, 100, -2147483648, 2147483647};
    public static float[] floatValues = {0, 0.0f, -1.0f, 2.1f, 12, -16};
    public static double[] doubleValues = {0, 0.0f, -1.0, 2.1f, 12, -16, 100.1415, 000.123};
    public static String[] stringValues = {"", "0", "true", "0.0", "123", "-0.1", "0.1415", "\"", "\" \"", "\n"};
    public static HashMap<Object, Object> mapValue0 = new HashMap<Object, Object>(){
        {
            put("1", false);
            put("2", 0);
            put(true, 0);
            put(3, "");
            put(-1.2, "\"\n\r\n123 qwe.wer.14\" --=$\"");
        }
    };
    public static HashMap<Object, Object> mapValue1 = new HashMap<Object, Object>(){
        {
            put("1", false);
            put("2", -0.13);
            put(false, 0);
            put(true, 0.0);
            put(3, "");
            put(-1.2, mapValue0);
        }
    };
    public static HashMap<Object, Object> mapValueForTpl = new HashMap<Object, Object>(){
        {
            put("number", 0);
            put("list", new ArrayList<Object>(){
                {
                    add(new HashMap<Object, Object>(){
                        {
                            put("user", new HashMap<Object, Object>(){
                                {
                                    put("name", "lily");
                                    put("age", 10);
                                }
                            });
                        }
                    });
                    add(new HashMap<Object, Object>(){
                        {
                            put("pet", "shiba");
                        }
                    });
                }
            });
        }
    };
    public static ArrayList<Object> arrayListValue0 = new ArrayList<Object>(){
        {
            add(1);
            add(false);
            add(0.0);
            add(0.0f);
            add(" ");
            add(mapValue0);
        }
    };
    public static ArrayList<String> arrayListValue1 = new ArrayList<String>(){
        {
            add("1");
            add("false");
            add("0.0");
            add(" ");
            add("");
        }
    };
}
