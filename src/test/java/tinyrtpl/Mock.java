/**
 * Created by keshen on 2016/9/7.
 */

package tinyrtpl;

import java.util.ArrayList;
import java.util.HashMap;

public class Mock {
    public static Object[] objectArr = {};
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
            put("\" \n\r", null);
        }
    };
    public static HashMap<Object, Object> mapValue2 = new HashMap<Object, Object>();
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
                                    put("name", "Lily");
                                    put("age", 10);
                                }
                            });
                        }
                    });
                    add(new HashMap<Object, Object>(){
                        {
                            put("pet", "Shiba");
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

    public static Data data0 = new Data() {
        {
            put(0, "a");
            put(1, null);
            put("2", true);
            put("3.0", 0);
            put(4.1, 0.1f);
            put(false, -143342134);
            put("shen", "ke");
        }
    };
    public static String tpl0_0 = "{{}}";
    public static String tpl0_1 = "{{0}}";
    public static String tpl0_2 = "{{this.shen}}";
    public static String tpl0_3 = "{{false}}";
    public static String tpl0_4 = "{{4.1}}";
    public static String tpl0_5 = "{{\"4.1\"}}";
    public static String tpl0_6 = "{{3.0}}";
    public static String tpl0_7 = "{{0.0}}";
    public static String tpl0_8 = "{{if this.0}}this.0{{/if}}";
    public static String tpl0_9 = "{{if this.2}}{{this.2}}{{else}}shen {{this.shen}}{{/if}}";
    public static String tpl0_10 = "{{if !this.2}}{{this.2}}{{else}}shen {{this.shen}}{{/if}}";
    public static String tpl0_11 = "{{if !this.2}}{{this.2}}{{else}}shen {{this.shen}}";
}
