/**
 * Created by keshen on 2016/9/2.
 */

package tinyrtpl;

import java.util.*;
import java.math.BigDecimal;

import org.apache.commons.lang3.StringEscapeUtils;

public class Data {
    private int type = -1; // 0: null, 1: Boolean, 2: Number, 3: Float, 4: String, 5: Map, 6: Array, 7: Data(converting)
    private boolean vboolean;
    private int vint;
    private double vfloat;
    private String vstring;
    private HashMap<String, Data> vmap;
    private ArrayList<Data> varray;
    private static String className = "tinyrtpl.Data";
    private static HashMap<String, Integer> opTypes = new HashMap<String, Integer>() {
        {
            put("+", 1);
            put("-", 2);
            put("*", 3);
            put("/", 4);
            put("%", 5);
            put("<", 6);
            put(">", 7);
            put("<=", 8);
            put(">=", 9);
            put("==", 10);
            put("===", 11);
            put("!", 12);
            put("&&", 13);
            put("||", 14);
            put("?", 15);
        }
    };

    /**
     * Return the type of this Data instance.
     *
     * @return the data type represented by an integer
     */
    public int getType() {
        return this.type;
    }

    public Data() {
        this.type = 5;
        this.vmap = new HashMap<String, Data>();
    }

    /**
     * Return the data of this Data instance.
     * @return an object, referring to a certain field of this instance.
     */
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

    /**
     * Convert the types of keys and values in a common HashMap to <String, Data>.
     * @param map: a common HashMap<Object, Object>
     * @return a HashMap<String, Data>
     */
    private static HashMap<String, Data> dataMapOf(HashMap<Object, Object> map) {
        if(map == null) return null;
        HashMap<String, Data> dmap = new HashMap<>();
        Iterator it = map.entrySet().iterator();
        int vType = -1;
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            vType = Data.typeOf(e.getValue());
            dmap.put(e.getKey().toString(), new Data(e.getValue(), vType));
        }
        return dmap;
    }

    /**
     * Convert the types of elements in a common List to Data.
     * @param list: a common List of Object
     * @return an ArrayList of Data
     */
    private static ArrayList<Data> dataArrayOf(List<Object> list) {
        if(list == null) return null;
        ArrayList<Data> darray = new ArrayList<>();
        if(list.size() == 0) return darray;
        int vType = -1;
        for(Object obj : list) {
            vType = Data.typeOf(obj);
            darray.add(new Data(obj, vType));
        }
        return darray;
    }

    /**
     * Set the value to a type-specific object.
     * @param obj: an Object
     * @param type: an integer
     */
    public void val(Object obj, int type) {
        if (type >= 0 && type <= 6) {
            this.type = type;
        }
        switch (type) {
            case 0:
                return;
            case 1:
                this.vboolean = (boolean) obj;
                break;
            case 2:
                this.vint = (int) obj;
                break;
            case 3:
                if (obj instanceof Float) {
                    BigDecimal b = new BigDecimal(String.valueOf((float) obj));
                    this.vfloat = b.doubleValue();
                } else {
                    this.vfloat = (double) obj;
                }
                break;
            case 4:
                this.vstring = (String) obj;
                break;
            case 5:
                this.vmap = Data.dataMapOf((HashMap<Object, Object>) obj);
                break;
            case 6:
                if(obj instanceof List) this.varray = Data.dataArrayOf((List<Object>) obj);
                break;
            case 7:
                this.val(((Data) obj).val());
                break;
            default:
        }
    }

    /**
     * Set the value to an object whose type can be inferred.
     * @param obj: an Object
     */
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

    /**
     * Return the type of a given Object.
     * @param obj: an Object
     * @return the inferred type represented by an integer
     */
    public static int typeOf(Object obj) {
        if (obj == null) return 0;
        if(obj instanceof Boolean) return 1;
        if(obj instanceof Integer) return 2;
        if(obj instanceof Float) return 3;
        if(obj instanceof Double) return 3;
        if(obj instanceof String) return 4;
        if(obj instanceof HashMap) return 5;
        if(obj instanceof List) return 6;
//        if(obj.getClass().isArray()) return 6;
//        if(Data.className.equals(obj.getClass().getName())) return 7;
        if(obj instanceof Data) return 7;
        return -1;
    }

    /**
     * Check if this instance has no data.
     *
     * @return true or false
     */
    public boolean isNull() {
        return this.type == 0;
    }

    /**
     * Return a Data instance of a given Object.
     * @param obj: an Object that can be a Data or anything else
     * @return a Data instance
     */
    public static Data dataOf(Object obj) {
//        if(Data.className.equals(obj.getClass().getName())) return (Data) obj; // obj is already a Data.
        if(obj instanceof Data) return (Data) obj; // obj is already a Data.
        return new Data(obj);
    }

    /**
     * Return the string format of a given Object.
     * @param obj: a type-specific Object
     * @param type: type pf the Object
     * @return string of the Object
     */
    private static String stringOf(Object obj, int type) {
        if(obj == null || type == 0) return "";
        String str;
        switch (type) {
            case 1:
                str = Boolean.toString((boolean) obj);
                break;
            case 2:
                str = Integer.toString((int) obj);
                break;
            case 3:
                double d;
                if (obj instanceof Float) {
                    BigDecimal b = new BigDecimal(String.valueOf((float) obj));
                    d = b.doubleValue();
                } else {
                    d = (double) obj;
                }
                str = Double.toString(d);
                break;
            case 4:
                str = (String) obj;
                break;
            case 5:
                str = "map";
                break;
            case 6:
                str = "array";
                break;
            default:
                str = "";
        }
        return str;
    }

    /**
     * Return the json string format of a given Object.
     * @return json string of the Object
     */
    public String toJsonString() {
        switch (this.type) {
            case 0:
                return "null";
            case 1:
            case 2:
            case 3:
                return this.toString();
            case 4:
                return "\"" + StringEscapeUtils.escapeJson(this.toString()) + "\"";
            case 5:
                if(this.vmap == null) return "null";
                StringBuilder sb = new StringBuilder("{");
                Iterator it = this.vmap.entrySet().iterator();
                boolean notEmpty = false;
                while (it.hasNext()) {
                    notEmpty = true;
                    Map.Entry e = (Map.Entry) it.next();
                    sb.append("\"" + StringEscapeUtils.escapeJson((String) e.getKey()) + "\":" + ((Data) e.getValue()).toJsonString()  + ",");
                }
                if(notEmpty) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                sb.append("}");
                return sb.toString();
            case 6:
                if(this.varray == null) return "null";
                StringBuilder sb1 = new StringBuilder("[");
                boolean notEmpty1 = false;
                for(Data dt : this.varray) {
                    notEmpty1 = true;
                    sb1.append(dt.toJsonString() + ",");
                }
                if(notEmpty1) {
                    sb1.deleteCharAt(sb1.length() - 1);
                }
                sb1.append("]");
                return sb1.toString();
            default:
                return "";
        }
    }

    @Override
    public String toString() {
        return Data.stringOf(this.val(), this.type);
    }

    /**
     * Return the number value of this instance.
     * @return number value in the Object
     */
    public Object toNumber() {
        if (this.type > 4 || this.type < 1) return 0;
        switch (this.type) {
            case 1:
                return (this.vboolean ? 1 : 0); // int
            case 2:
                return this.vint; // int
            case 3:
                return this.vfloat; // double
            case 4:
                try {
                    int vi = Integer.parseInt(this.vstring);
                    return vi;
                } catch (NumberFormatException ex) {
                    try {
                        double vd = Double.parseDouble(this.vstring);
                        return vd;
                    } catch (NumberFormatException ex1) {
                        try {
                            float vf = Float.parseFloat(this.vstring);
                            return new BigDecimal(String.valueOf(vf)).doubleValue();
                        } catch (NumberFormatException ex2) {
                            return 0;
                        }
                    }
                }
        }
        return 0;
    }

    /**
     * Return the boolean value of this instance.
     * @return boolean value in the Object
     */
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

    /**
     * Compare this instance with another
     * @param d: a Data instance
     * @return equal or not
     */
    private boolean equalTo(Data d) {
        if(d == null) return false;
        if(this.type != d.type) return false;
        switch (this.type) {
            case 1:
                return this.vboolean == d.vboolean;
            case 2:
                return this.vint == d.vint;
            case 3:
                return this.vfloat == d.vfloat;
            case 4:
                return this.vstring.equals(d.vstring);
            default:
                return this.val() == d.val();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof Data)) return false;
        Data d = (Data) obj;
        if(this.type != d.type) return false;
        switch (this.type) {
            case 1:
                return this.vboolean == d.vboolean;
            case 2:
                return this.vint == d.vint;
            case 3:
                return this.vfloat == d.vfloat;
            case 4:
                return this.vstring.equals(d.vstring);
            case 5:
                if(this.vmap == null) return d.vmap == null;
                if(d.vmap == null) return false;
                Iterator it = this.vmap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry e = (Map.Entry) it.next();
                    Object k = e.getKey();
                    if(!d.vmap.containsKey(k)) return false;
                    if(e.getValue() == null) {
                        if(d.vmap.get(k) == null) continue;
                        return false;
                    }
                    if(!e.getValue().equals(d.vmap.get(k))) return false;
                }
                return this.vmap.size() == d.vmap.size();
            case 6:
                if(this.varray == null) return d.varray == null;
                if(d.varray == null) return false;
                int len1 = this.varray.size(),
                    len2 = d.varray.size();
                if(len1 != len2) return false;
                for(int i = 0; i < len1; i++) {
                    if(!this.varray.get(i).equals(d.varray.get(i))) return false;
                }
                return true;
            default:
                return this.val() == d.val();
        }
    }

    /**
     * Compare with another instance.
     * @param d: a Data instance
     * @return true or false
     */
    private boolean compareTo(Data d) {
        if(d != null && d.type < this.type) return d.compareTo(this);
        switch (this.type) {
            case 1:
                if(d == null) return !this.vboolean;
                return (d.toBoolean() == this.vboolean);
            case 2:
                if(d == null) return this.vint == 0;
                switch (d.type) {
                    case 2:
                        return this.vint == d.vint;
                    case 3:
                        return d.vfloat == (double)this.vint;
                    case 4:
                        try {
                            int id = Integer.parseInt(d.vstring);
                            return this.vint == id;
                        } catch (NumberFormatException ex) {
                            if("".equals(d.vstring)) return this.vint == 0;
                            return false;
                        }
                    case 5:
                        return this.vint == 0 && d.vmap == null;
                    case 6:
                        return this.vint == 0 && d.varray == null;
                }
            case 3:
                if(d == null) return this.vfloat == 0;
                switch (d.type) {
                    case 3:
                        return this.vfloat == d.vfloat;
                    case 4:
                        Object nd = d.toNumber();
                        if(nd instanceof Integer || nd instanceof Double)
                            return this.vfloat == (double)nd;
                        return false;
                    case 5:
                        return this.vfloat == 0 && d.vmap == null;
                    case 6:
                        return this.vfloat == 0 && d.varray == null;
                }
            case 4:
                if(d == null) return "".equals(this.vstring);
                switch (d.type) {
                    case 4:
                        return this.vstring.equals(d.vstring);
                    case 5:
                        return "".equals(this.vstring) && d.vmap == null;
                    case 6:
                        return "".equals(this.vstring) && d.varray == null;
                }
            case 5:
                if(d == null) return this.vmap == null;
                switch (d.type) {
                    case 5:
                        if(this.vmap == null) return d.vmap == null;
                        if(d.vmap == null) return false;
                        if(this.vmap.size() == 0 && d.vmap.size() == 0) return true;
                        return this.vmap == d.vmap; //TODO: deep compare
                    case 6:
                        if(this.vmap == null) return d.varray == null;
                        return false;
                }
            case 6:
                if(d == null) return this.varray == null;
                switch (d.type) {
                    case 6:
                        if(this.varray == null) return d.varray == null;
                        if(d.varray == null) return false;
                        if(this.varray.size() == 0 && d.varray.size() == 0) return true;
                        return this.varray == d.varray; //TODO: deep compare
                }
        }
        return false;
    }

    /**
     * Put a key-value pair into the data if this is a map;
     * or set a index-specific element if this is an array.
     * @param key: key
     * @param value: value
     */
    public void put(Object key, Object value) {
        if (this.type != 5 && this.type != 6) return;
        int kType = Data.typeOf(key);
        if (kType < 1 || kType > 4) return;
        String keyStr = Data.stringOf(key, kType);
        if (this.type == 5) {
            if (kType == 4) {
                int dotPos = keyStr.indexOf('.');
                if (dotPos > 0) {
                    Data.dataOf(this.vmap.get(keyStr.substring(0, dotPos))).put(keyStr.substring(dotPos + 1), value);
                    return;
                }
            }
            this.vmap.put(keyStr, Data.dataOf(value));
        } else {
            int index = Integer.parseInt(keyStr);
            int len = this.varray.size();
            if (index >= len || index < 0) return;
            this.varray.set(index, Data.dataOf(value));
        }
    }

    /**
     * Add an element to the data if this is an array.
     * @param value: data of a new element
     */
    public void add(Object value) {
        if(this.type != 6) return;
        this.varray.add(Data.dataOf(value));
    }

    /**
     * Get an element(value) from the array(map) of this instance.
     * @param key: a certain key or index
     * @return the element or value
     */
    public Data get(Object key) {
        if (this.type != 5 && this.type != 6) return null;
        int kType = Data.typeOf(key);
        if (kType < 1 || kType > 4) return null;
        String keyStr = Data.stringOf(key, kType);
        if (this.type == 5) {
            if (kType == 4) {
                int dotPos = keyStr.indexOf('.');
                if (dotPos > 0)
                    return Data.dataOf(this.vmap.get(keyStr.substring(0, dotPos))).get(keyStr.substring(dotPos + 1));
            }
            return Data.dataOf(this.vmap.get(keyStr));
        } else {
            int index = Integer.parseInt(keyStr);
            int len = this.varray.size();
            if (index >= len || index < 0) return null;
            return Data.dataOf(this.varray.get(index));
        }
    }

    /**
     * Binary calculation.
     * @param fStr: the binary operation
     * @param a: data1
     * @param b: data2
     * @return the result
     */
    public static Data op(String fStr, Data a, Data b) {
        if (!opTypes.containsKey(fStr)) return null;
        int f = opTypes.get(fStr);
        Data data = null;
        Object va, vb;
        switch (f) {
            case 1: // +
                if (a.type != 2 && a.type != 3 && b.type != 2 && b.type != 3) {
                    data = new Data(a.toString() + b.toString(), 4);
                    break;
                }
                va = a.toNumber();
                vb = b.toNumber();
                if (a.type == 2 && b.type == 2) {
                    data = new Data((int) va + (int) vb, 2);
                } else {
                    data = new Data((double) va + (double) vb, 3);
                }
                break;
            case 2: // -
                if (a.type != 2 && a.type != 3 && b.type != 2 && b.type != 3) {
                    return null;
                }
                va = a.toNumber();
                vb = b.toNumber();
                if (a.type == 2 && b.type == 2) {
                    data = new Data((int) va - (int) vb, 2);
                } else {
                    data = new Data((double) va - (double) vb, 3);
                }
                break;
            case 3: // *
                if (a.type != 2 && a.type != 3 && b.type != 2 && b.type != 3) {
                    return null;
                }
                va = a.toNumber();
                vb = b.toNumber();
                if (a.type == 2 && b.type == 2) {
                    data = new Data((int) va * (int) vb, 2);
                } else {
                    data = new Data((double) va * (double) vb, 3);
                }
                break;
            case 4: // /
                va = a.toNumber();
                if (va instanceof Integer && (int) va == 0) return new Data(0, 2);
                if (va instanceof Double && (double) va == 0) return new Data(0, 3);
                vb = b.toNumber();
                if (vb instanceof Integer && (int) vb == 0) return null;
                if (vb instanceof Double && (double) vb == 0) return null;
                data = new Data((double) va / (double) vb, 3);
                break;
            case 5: // %
                if (a.type != 2 && b.type != 2) {
                    return null;
                }
                va = a.toNumber();
                vb = b.toNumber();
                if ((int)vb == 0) {
                    data = new Data(0, 2);
                } else {
                    data = new Data((int)va % (int)vb, 2);
                }
                break;
            case 6: // <
                if (a.type != 2 && a.type != 3 && b.type != 2 && b.type != 3) {
                    return null;
                }
                va = a.toNumber();
                vb = b.toNumber();
                if (a.type == 2 && b.type == 2) {
                    data = new Data((int) va < (int) vb, 1);
                } else {
                    data = new Data((double) va < (double) vb, 1);
                }
                break;
            case 7: // >
                if (a.type != 2 && a.type != 3 && b.type != 2 && b.type != 3) {
                    return null;
                }
                va = a.toNumber();
                vb = b.toNumber();
                if (a.type == 2 && b.type == 2) {
                    data = new Data((int) va > (int) vb, 1);
                } else {
                    data = new Data((double) va > (double) vb, 1);
                }
                break;
            case 8: // <=
                if (a.type != 2 && a.type != 3 && b.type != 2 && b.type != 3) {
                    return null;
                }
                va = a.toNumber();
                vb = b.toNumber();
                if (a.type == 2 && b.type == 2) {
                    data = new Data((int) va <= (int) vb, 1);
                } else {
                    data = new Data((double) va <= (double) vb, 1);
                }
                break;
            case 9: // >=
                if (a.type != 2 && a.type != 3 && b.type != 2 && b.type != 3) {
                    return null;
                }
                va = a.toNumber();
                vb = b.toNumber();
                if (a.type == 2 && b.type == 2) {
                    data = new Data((int) va >= (int) vb, 1);
                } else {
                    data = new Data((double) va >= (double) vb, 1);
                }
                break;
            case 10: // ==
                boolean comparison1 = false;
                if(a == null && b == null) {
                    comparison1 = true;
                } else {
                    comparison1 = (a != null) ? a.compareTo(b) : b.compareTo(a);
                }
                data = new Data(comparison1, 1);
                break;
            case 11: // ===
                boolean comparison2 = false;
                if(a == null && b == null) {
                    comparison2 = true;
                } else {
                    comparison2 = (a != null) ? a.equalTo(b) : b.equalTo(a);
                }
                data = new Data(comparison2, 1);
                break;
            case 12: // !
                data = new Data(!a.toBoolean(), 1);
                break;
            case 13: // &&
                data = new Data(a.toBoolean() && b.toBoolean(), 1);
                break;
            case 14: // ||
                data = new Data(a.toBoolean() || b.toBoolean(), 1);
                break;
            default:
        }
        return data;
    }

    /**
     * Ternary conditional calculation.
     * @param a: condition expression
     * @param b: data1
     * @param c: data2
     * @return the result
     */
    public static Data op(Data a, Data b, Data c) {
        return (a.toBoolean() ? b : c);
    }
}
