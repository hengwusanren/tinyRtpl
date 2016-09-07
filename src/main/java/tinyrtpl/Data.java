/**
 * Created by keshen on 2016/9/2.
 */

package tinyrtpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.math.BigDecimal;

public class Data {
    private int type = -1; // 0: null, 1: Boolean, 2: Number, 3: Float, 4: String, 5: Map, 6: Array, 7: Data(converting)
    private boolean vboolean;
    private int vint;
    private double vfloat;
    private String vstring;
    private HashMap<String, Object> vmap;
    private ArrayList<Object> varray;
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
                this.vmap = (HashMap<String, Object>) obj;
                break;
            case 6:
                if(obj instanceof ArrayList) this.varray = (ArrayList<Object>) obj;
                else this.varray = new ArrayList<Object>(Arrays.asList(obj));
                break;
            case 7:
                this.val(((Data) obj).val());
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
        if (obj == null) return 0;
        if(obj instanceof Boolean) return 1;
        if(obj instanceof Integer) return 2;
        if(obj instanceof Float) return 3;
        if(obj instanceof Double) return 3;
        if(obj instanceof String) return 4;
        if(obj instanceof HashMap) return 5;
        if(obj.getClass().isArray()) return 6;
        if(Data.className.equals(obj.getClass().getName())) return 7;
        return -1;
    }

    public static Data dataOf(Object obj) {
        return new Data(obj);
    }

    private static String stringOf(Object obj, int type) {
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
            default:
                str = "";
        }
        return str;
    }

    @Override
    public String toString() {
        return Data.stringOf(this.val(), this.type);
    }

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

    public void put(Object key, Object value) {
        if (this.type != 5 && this.type != 6) return;
        int kType = this.typeOf(key);
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

    public Data get(Object key) {
        if (this.type != 5 && this.type != 6) return null;
        int kType = this.typeOf(key);
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

    public static Data op(Data a, Data b, Data c) {
        return (a.toBoolean() ? b : c);
    }
}
