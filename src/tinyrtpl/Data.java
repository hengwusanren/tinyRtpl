/**
 * Created by keshen on 2016/9/2.
 */

package tinyrtpl;

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
    private static HashMap<String, Integer> classTypes = new HashMap<String, Integer>(){
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
    public int getType() {
        return this.type;
    }
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
    public void val(Object obj, int type) {
        if(type >= 0 && type <= 6) {
            this.type = type;
        }
        switch (type) {
            case 0:
                return;
            case 1:
                this.vboolean = (Boolean) obj;
                break;
            case 2:
                this.vint = (Integer) obj;
                break;
            case 3:
                this.vfloat = (Float) obj;
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
    public void val(Object obj) {
        int type = Data.typeOf(obj);
        this.val(obj, type);
    }
    public Data(Object obj, int type) {
        this.val(obj, type);
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
                str = Boolean.toString((Boolean) obj);
                break;
            case 2:
                str = Integer.toString((Integer) obj);
                break;
            case 3:
                str = Float.toString((Float) obj);
                break;
            case 4:
                str = (String)obj;
                break;
            default:
                str = "";
        }
        return str;
    }
    @Override
    public String toString() {
        return Data.stringOf(this.val(), this.type);
    }
    public double toNumber() {
        return 0;
    }
    public boolean toBoolean() {
        boolean b = false;
        switch (type) {
            case 1:
                b = this.vboolean;
                break;
            case 2:
                b = (this.vint != 0);
                break;
            case 3:
                b = (this.vfloat != 0.0f && this.vfloat != 0.0);
                break;
            case 4:
                b = !(this.vstring == null || "".equals(this.vstring));
                break;
            case 5:
                b = (this.vmap != null);
                break;
            case 6:
                b = (this.varray != null);
                break;
            default:
        }
        return b;
    }
    public void put(Object key, Object value) {
        if(this.type != 5 && this.type != 6) return;
        int kType = this.typeOf(key);
        if(kType < 1 || kType > 4) return;
        String keyStr = Data.stringOf(key, kType);
        if(this.type == 5) {
            if(kType == 4) {
                int dotPos = keyStr.indexOf('.');
                if(dotPos > 0) {
                    Data.dataOf(this.vmap.get(keyStr.substring(0, dotPos))).put(keyStr.substring(dotPos + 1), value);
                    return;
                }
            }
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
            if(kType == 4) {
                int dotPos = keyStr.indexOf('.');
                if(dotPos > 0) return Data.dataOf(this.vmap.get(keyStr.substring(0, dotPos))).get(keyStr.substring(dotPos + 1));
            }
            return Data.dataOf(this.vmap.get(keyStr));
        } else {
            int index = Integer.parseInt(keyStr);
            int len = this.varray.length;
            if(index >= len || index < 0) return null;
            return Data.dataOf(this.varray[index]);
        }
    }
    public static Data op(char c, Data a, Data b) {
        Data data = null;
        switch (c) {
        case '+':
            data = new Data(a.toString() + b.toString(), 4);
            break;
        case '-':
            break;
        case '*':
            break;
        case '/':
            break;
        case '!':
            break;
        case '&': // "&&"
            break;
        case '|': // "||"
            break;
        default:
        }
        return data;
    }
}
