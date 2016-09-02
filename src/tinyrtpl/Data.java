/**
 * Created by keshen on 2016/9/2.
 */

package tinyrtpl;

import java.util.ArrayList;
import java.util.HashMap;

public class Data {
    private int type = -1; // 0: null, 1: Boolean, 2: Number, 3: Float, 4: String, 5: Map, 6: Array, 7: Data(converting)
    private boolean vboolean;
    private int vint;
    private float vfloat;
    private String vstring;
    private HashMap<String, Object> vmap;
    private Object[] varray;
    private static String className = "tinyrtpl.Data";
    private static HashMap<String ,Integer> classTypes = new HashMap<String ,Integer>(){
        {
            put("java.lang.Boolean", 1);
            put("java.lang.Integer", 2);
            put("java.lang.Float", 3);
            put("java.lang.Double", 3);
            put("java.lang.String", 4);
            put("java.util.HashMap", 5);
            put(Data.className, 7);
        }
    };
    public Data() {
        this.type = 5;
        this.vmap = new HashMap<String, Object>();
    }
    public Object val() {
        switch (this.type) {
            case 0:
                return null;
            case 1:
                return this.vboolean;
            case 2:
                return this.vint;
            case 3:
                return this.vfloat;
            case 4:
                return this.vstring;
            case 5:
                return this.vmap;
            case 6:
                return this.varray;
            default:
                return null;
        }
    }
    public void val(Object obj) {
        int type = Data.typeOf(obj);
        if(type >= 0 && type <= 6) {
            this.type = type;
        }
        switch (type) {
            case 0:
                return;
            case 1:
                this.vboolean = (boolean)obj;
                break;
            case 2:
                this.vint = (int)obj;
                break;
            case 3:
                this.vfloat = (float)obj;
                break;
            case 4:
                this.vstring = (String)obj;
                break;
            case 5:
                this.vmap = (HashMap<String, Object>)obj;
                break;
            case 6:
                this.varray = (Object[])obj;
                break;
            case 7:
                this.val(((Data)obj).val());
                break;
            default:
        }
    }
    public Data(Object obj) {
        this.val(obj);
    }
    public static int typeOf(Object obj) {
        if(obj == null) return 0;
        String t = obj.getClass().getName();
        if(Data.classTypes.containsKey(t)) return Data.classTypes.get(t);
        if(t.charAt(0) == '[') return 6;
        return -1;
    }
    public static Data dataOf(Object obj) {
        return new Data(obj);
    }
    private static String stringOf(Object obj, int type) {
        String str;
        switch (type) {
            case 1:
                str = Boolean.toString((boolean)obj);
                break;
            case 2:
                str = Integer.toString((int)obj);
                break;
            case 3:
                str = Float.toString((float)obj);
                break;
            case 4:
                str = (String)obj;
                break;
            default:
                str = "";
        }
        return str;
    }
    public void put(Object key, Object value) {
        if(this.type != 5 && this.type != 6) return;
        int kType = this.typeOf(key);
        int vType = this.typeOf(value);
        if(kType < 1 || kType > 4) return;
        String keyStr = Data.stringOf(key, kType);
        if(this.type == 5) {
            this.vmap.put(keyStr, Data.dataOf(value));
        } else {
            int index = Integer.parseInt(keyStr);
            int len = this.varray.length;
            if(index >= len || index < 0) return;
            this.varray[index] = Data.dataOf(value);
        }
    }
    public Data get(Object key) {
        if(this.type != 5 && this.type != 6) return null;
        int kType = this.typeOf(key);
        if(kType < 1 || kType > 4) return null;
        String keyStr = Data.stringOf(key, kType);
        if(this.type == 5) {
            return Data.dataOf(this.vmap.get(keyStr));
        } else {
            int index = Integer.parseInt(keyStr);
            int len = this.varray.length;
            if(index >= len || index < 0) return null;
            return Data.dataOf(this.varray[index]);
        }
    }
}
