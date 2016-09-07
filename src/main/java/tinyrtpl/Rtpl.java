/**
 * Created by keshen on 2016/9/2.
 */

package tinyrtpl;

import com.googlecode.aviator.AviatorEvaluator;
import java.lang.Character;
import java.math.BigDecimal;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.apache.commons.lang3.StringEscapeUtils;
// 指向当前数据的ref用"this."开头，否则是全局变量

public class Rtpl {
    private static String tplFileBaseDir;
    private Data scope = new Data();
    private String tpl;
    private String value;

    public Rtpl(Data data, String src, String dir) {
        Rtpl.tplFileBaseDir = dir;
        this.scope.put("this", data);
        this.tpl = src;
        this.value = Rtpl.process(this.scope, this.tpl, 0);
        System.out.println(this.value);
    }

    public Rtpl(Data data, String src, String dir, boolean file) {
        Rtpl.tplFileBaseDir = dir;
        new Rtpl(data, file ? readFile(Rtpl.getFilePath(src)) : src, dir);
    }

    public Rtpl(Data data, String src) {
        if(Rtpl.tplFileBaseDir != null) new Rtpl(data, src, Rtpl.tplFileBaseDir);
    }

    private static String getFilePath(String tplName) {
        return Rtpl.tplFileBaseDir + "/" + (tplName.endsWith(".html") ? tplName : (tplName + ".html"));
    }

    private static int typeOfFrag(String frag) {
        if (frag.startsWith("if ")) return 1;//"if";
        if (frag.startsWith("else if ")) return 2;//"elseif";
        if (frag.startsWith("else")) return 3;//"else";
        if (frag.startsWith("/if")) return 4;//"endif";
        if (frag.startsWith("each ")) return 5;//"each";
        if (frag.startsWith("/each")) return 6;//"endeach";
        if (frag.startsWith("set ")) return 7;//"set";
        if (frag.startsWith("include ")) return 8;//"include";
        return 9;//"get";
    }

    private static String process(Data data, String tpl, int begin) {
        int fragBegin = tpl.indexOf("{{", begin);
        if (fragBegin < 0) return tpl.substring(begin);
        int fragEnd = tpl.indexOf("}}", fragBegin + 2);
        if (fragEnd < 0) return tpl.substring(begin);
        String curFrag = tpl.substring(fragBegin + 2, fragEnd);
        int curFType = Rtpl.typeOfFrag(curFrag);

        // "{{if ...}}..."
        if (curFType == 1) {
            int blockEnd = tpl.indexOf("{{/if}}", fragEnd + 2);
            if (blockEnd < 0) {
                return tpl.substring(begin);
            }
            return tpl.substring(begin, fragBegin) + Rtpl.processIf(data, Rtpl.getBranchesOfIf(tpl.substring(fragBegin, blockEnd))) + Rtpl.process(data, tpl, blockEnd + 7);
        }

        // "{{each ...}}..."
        if (curFType == 5) {
            int blockEnd = tpl.indexOf("{{/each}}", fragEnd + 2);
            if (blockEnd < 0) {
                return tpl.substring(begin);
            }
            String[] paras = Rtpl.getParasOfEach(curFrag);
            return tpl.substring(begin, fragBegin) + Rtpl.processEach(data, tpl.substring(fragEnd + 2, blockEnd), paras[0], paras[1], paras[2]) + Rtpl.process(data, tpl, blockEnd + 9);
        }

        // "{{set ...}}"
        if (curFType == 7) {
            String[] paras = Rtpl.getParasOfSet(curFrag);
            return tpl.substring(begin, fragBegin) + Rtpl.processSet(data, paras[0], paras[1]) + Rtpl.process(data, tpl, fragEnd + 2);
        }

        // "{{include ...}}"
        if (curFType == 8) {
            String[] paras = Rtpl.getParasOfInclude(curFrag);
            return tpl.substring(begin, fragBegin) + Rtpl.processInclude(data, paras[0], paras[1]) + Rtpl.process(data, tpl, fragEnd + 2);
        }

        // "{{...}}"
        if (curFType == 9) {
            return tpl.substring(begin, fragBegin) + Rtpl.processGet(data, curFrag.trim()) + Rtpl.process(data, tpl, fragEnd + 2);
        }

        return tpl.substring(begin);
    }

    private static LinkedHashMap<String, String> getBranchesOfIf(String frag) {
        LinkedHashMap<String, String> branches = new LinkedHashMap<String, String>();
        //TODO
        return branches;
    }

    private static String processIf(Data data, LinkedHashMap<String, String> branches) {
        Iterator it = branches.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            if (Rtpl._if(data, (String) e.getKey())) {
                return Rtpl.process(data, branches.get(e.getKey()), 0);
            }
        }
        return "";
    }

    private static boolean _if(Data data, String exp) {
        return Rtpl._ex(data, exp).toBoolean();
    }

    private static String[] getParasOfEach(String frag) {
        String[] pieces = frag.split(" ");
        String[] paras = new String[3];
        int count = 0;
        for (String p : pieces) {
            if ("".equals(p)) continue;
            if (count == 1) paras[0] = p;
            else if (count == 3) paras[1] = p;
            else if (count == 4) paras[2] = p;
            count++;
        }
        return paras;
    }

    private static String processEach(Data data, String tpl, String ref, String valueName, String indexName) {
        StringBuilder sb = new StringBuilder("");
        if (valueName == null || "".equals(valueName.trim())) valueName = "$value";
        if (indexName == null || "".equals(indexName.trim())) indexName = "$index";
        Data c = Rtpl._get(data, ref, false);
        if (c.getType() == 5) { // map
            Map m = (HashMap) c.val();
            Iterator it = m.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                data.put(indexName, e.getKey());
                data.put(valueName, e.getValue());
                sb.append(Rtpl.process(data, tpl, 0));
            }
        } else if (c.getType() == 6) { // array
            Object[] a = (Object[]) c.val();
            for (int i = 0, len = a.length; i < len; i++) {
                data.put(indexName, i);
                data.put(valueName, a[i]);
                sb.append(Rtpl.process(data, tpl, 0));
            }
        }
        return sb.toString();
    }

    private static String[] getParasOfSet(String frag) {
        String[] pieces = frag.split(" ");
        String[] paras = new String[2];
        int count = 0;
        for (String p : pieces) {
            if ("".equals(p)) continue;
            if (count == 1) paras[0] = p;
            else if (count == 2) paras[1] = p;
            count++;
        }
        return paras;
    }

    private static void _set(Data data, String ref, Object value) {
        data.put(ref, value);
    }

    private static String processSet(Data data, String ref, String exp) {
        Rtpl._set(data, ref, Rtpl._ex(data, exp));
        return "";
    }

    public static class TokenList {
        private int type; // 0: list, 1: op, 2: data
        private Object data;
        private ArrayList<TokenList> list;

        public TokenList() {
            this.type = 0;
            this.list = new ArrayList<TokenList>();
        }

        public TokenList(int t, Object v) {
            this.type = t;
            if(t != 0) this.data = v;
            else this.list = new ArrayList<TokenList>();
        }

        public TokenList add(int t, Object v) {
            if(this.type != 0) {
                int ot = this.type;
                Object od = this.data;
                this.type = 0;
                this.data = null;
                this.list = new ArrayList<TokenList>();
                this.add(ot, od).add(t, v);
            } else {
                if(this.list == null) this.list = new ArrayList<TokenList>();
                this.list.add(new TokenList(t, v));
            }
            return this;
        }

        public TokenList add(TokenList tl) {
            if(this.type != 0) {
                this.add(tl.type, tl.data);
            } else {
                this.list.add(tl);
            }
            return this;
        }

        public TokenList get(int index) {
            if(this.type != 0) return null;
            return this.list.get(index);
        }

        public TokenList set(int index, int type, Object v) {
            if(this.type != 0) return this;
            this.list.set(index, type == 0 ? (TokenList) v : new TokenList(type, v));
            return this;
        }

        public TokenList concat(TokenList tl) {
            if(tl.type != 0) {
                return this.add(tl);
            }
            if(this.type != 0) {
                int ot = this.type;
                Object od = this.data;
                this.type = 0;
                this.data = null;
                this.list = new ArrayList<TokenList>();
                this.add(ot, od);
            }
            this.list.addAll(tl.list);
            return this;
        }

        public int size() {
            return (this.type == 0) ? this.list.size() : 1;
        }

        @Override
        public String toString() {
            if(this.type == 0) {
                StringBuilder sb = new StringBuilder("");
                for(TokenList tl : this.list) {
                    sb.append(tl.toString());
                }
                return sb.toString();
            }
            return this.data.toString();
        }
    }

    // 得到exp的TokenList
    private static TokenList getTokensOfExp(Data data, String exp) {
        if(exp == null || "".equals(exp)) return null;

        // ...(...)...
        int bracketBegin = exp.indexOf('(');
        if(bracketBegin >= 0) {
            int leftBracketCount = 1;
            for(int i = bracketBegin + 1, len = exp.length(); i < len; i++) {
                char c = exp.charAt(i);
                if(c == ')') {
                    leftBracketCount--;
                    if(leftBracketCount == 0) {
                        return Rtpl.getTokensOfExp(data, exp.substring(0, bracketBegin))
                                .add(Rtpl.getTokensOfExp(data, exp.substring(bracketBegin, i)))
                                .concat(Rtpl.getTokensOfExp(data, exp.substring(i + 1)));
                    }
                } else if(c == '(') {
                    leftBracketCount++;
                }
            }
            if(leftBracketCount > 0) return null;
        }

        // exp里只剩operator、reference、constant
        // 1: op, 2: ref, 3: con
        TokenList r = new TokenList();
        char[] charArr = exp.toCharArray();
        int len = charArr.length;
        boolean nowIsRef = false;
        int nowRefBegin = -1;
        boolean nowIsStr = false;
        int nowStrBegin = -1;
        for(int i = 0; i < len; i++) {
            char c = charArr[i];
            if(nowIsStr) {
                if(c == '"' && !(i - 1 >= nowStrBegin && charArr[i - 1] == '\\')) {
                    r.add(new TokenList(2, new Data(exp.substring(nowStrBegin, i + 1), 4))); // add a data
                    nowIsStr = false;
                    nowIsRef = false;
                }
                continue;
            }
            if((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '$' || c == '_' || c == '.'
                    || (c >= '0' && c <= '9')) { // do with letter
                if(nowIsRef) continue;
                nowIsRef = true;
                nowRefBegin = i;
            } else if("+-*/%".indexOf(c) >= 0) { // do with +-*/%
                if(nowIsRef) {
                    r.add(2, Rtpl._get(data, exp.substring(nowRefBegin, i), false)); // add a data
                    nowIsRef = false;
                }
                r.add(1, Character.toString(c)); // add an op
            } else if(c == '>' || c == '<') {
                if(nowIsRef) {
                    r.add(2, Rtpl._get(data, exp.substring(nowRefBegin, i), false)); // add a data
                    nowIsRef = false;
                }
                if(i < len - 1 && charArr[i + 1] == '=') { // do with >=, <=
                    r.add(1, Character.toString(c) + "="); // add an op
                    i++;
                } else { // do with >, <
                    r.add(1, Character.toString(c)); // add an op
                }
            } else if("=&|".indexOf(c) >= 0) {
                if(nowIsRef) {
                    r.add(2, Rtpl._get(data, exp.substring(nowRefBegin, i), false)); // add a data
                    nowIsRef = false;
                }
                if(i < len - 1 && charArr[i + 1] == c) { // do with ==, &&, ||
                    r.add(1, Character.toString(c) + Character.toString(c)); // add an op
                    i++;
                    continue;
                }
                return null;
            } else if(c == '"') { // do with "
                nowIsStr = true;
                nowStrBegin = i;
            } else { // do with whitespace
                if(nowIsRef) {
                    r.add(2, Rtpl._get(data, exp.substring(nowRefBegin, i), false)); // add a data
                    nowIsRef = false;
                }
            }
            continue;
        }

        return r;
    }

    //TODO: 表达式求值
    private static Data calDataExp(TokenList exp) {
        //TODO
        return null;
    }

    // 不断把其中的简单列表求值、替换
    private static Data evalTokenList(TokenList tl) {
        if(tl.type == 1) return null;
        if(tl.type == 2) return (Data)(tl.data);
        for(int i = 0, len = tl.size(); i < len; i++) {
            if(tl.get(i).type == 0)
                tl.set(i, 2, evalTokenList(tl.get(i)));
        }

        boolean useAviator = true;
        if(useAviator) {
            String exp = tl.toString();
            System.out.println("exp: " + exp);
            return new Data(AviatorEvaluator.execute(exp), 4);
        } else {
            return Rtpl.calDataExp(tl);
        }
    }

    private static Data _ex(Data data, String exp) {
        if(exp == null || "".equals(exp)) return null;
        int offset = 0;

        // [...]
        int squareBracketBegin = exp.indexOf('[');
        int squareBracketEnd = exp.indexOf(']');
        if(squareBracketBegin < 0) {
            if(squareBracketEnd >= 0) return null;
        } else if(squareBracketBegin == 0) {
            return null;
        } else{
            if(squareBracketEnd <= squareBracketBegin + 1) return null;
        }
        if(squareBracketBegin > 0) {
            return Rtpl._ex(data, exp.substring(0, squareBracketBegin) + "." + Rtpl._ex(data, exp.substring(squareBracketBegin + 1, squareBracketEnd)).toString() + exp.substring(squareBracketEnd + 1));
        }

        // ... ? ... : ...
        int questionPos = exp.indexOf('?');
        if(questionPos == 0) return null;
        if(questionPos > 0) {
            int colonPos = exp.indexOf(':');
            if(colonPos <= questionPos + 1) return null;
            return Data.op(Rtpl._ex(data, exp.substring(0, questionPos)),
                    Rtpl._ex(data, exp.substring(questionPos + 1, colonPos)),
                    Rtpl._ex(data, exp.substring(colonPos + 1)));
        }

        return Rtpl.evalTokenList(Rtpl.getTokensOfExp(data, exp));
    }

    private static Data _get(Data odata, String oref, boolean wrap) {
        String ref = oref.trim();

        // const boolean
        if("true".equals(ref) || "false".equals(ref))
            return new Data("true".equals(ref), 1);

        char ch = ref.charAt(0);
        if(ch >= '0' && ch <= '9') { // const number
            try {
                int vi = Integer.parseInt(ref);
                return new Data(vi, 2);
            } catch (NumberFormatException ex) {
                try {
                    double vd = Double.parseDouble(ref);
                    return new Data(vd, 3);
                } catch (NumberFormatException ex1) {
                    try {
                        float vf = Float.parseFloat(ref);
                        return new Data(new BigDecimal(String.valueOf(vf)).doubleValue(), 3);
                    } catch (NumberFormatException ex2) {
                        return null;
                    }
                }
            }
        } else if(ch == '"') { // const string
            int lastQuotationPos = ref.lastIndexOf('"');
            if(lastQuotationPos <= 0) return null;
            return new Data(ref.substring(1, lastQuotationPos), 4);
        }

        Data data;
        if (wrap) {
            data = new Data();
            data.put("this", (ref == null || "".equals(ref)) ? odata : odata.get(ref)); // 不支持自定义变量渗透进子作用域传递
        } else data = (ref == null || "".equals(ref)) ? odata : odata.get(ref);
        return data;
    }

    private static String[] getParasOfInclude(String frag) {
        return Rtpl.getParasOfSet(frag);
    }

    private static String processInclude(Data odata, String name, String ref) {
        Data data = Rtpl._get(odata, ref, true);
        return Rtpl.process(data, Rtpl.readFile(Rtpl.getFilePath(name)), 0);
    }

    private static String processGet(Data data, String oref) {
        if(data == null || oref == null) return null;
        String ref = oref.trim();
        if("".equals(ref)) return null;
        if(ref.charAt(0) == '#') return Rtpl._get(data, ref.substring(1), false).toString();
        return StringEscapeUtils.escapeHtml4(Rtpl._get(data, ref, false).toString());
    }

    private static String readFile(String fileName) {
        if (fileName == null) return "";
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder("");
        try {
            String curLine;
            br = new BufferedReader(new FileReader(fileName));
            while ((curLine = br.readLine()) != null) {
                sb.append(curLine + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return sb.toString();
    }
}
