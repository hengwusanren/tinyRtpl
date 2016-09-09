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
import java.io.File;
import java.io.IOException;
import org.apache.commons.lang3.StringEscapeUtils;
// 指向当前数据的ref用"this."开头，否则是全局变量

public class Rtpl {
    private static String tplFileBaseDir;
    private Data scope;
    private String tpl;
    private String value;

    public Rtpl() {
        this.scope = new Data();
    }

    public Rtpl(Data data, String filePath) {
        this.scope = new Data();
        if(filePath == null) return;
        File file = new File(filePath);
        String name = file.getName();
        if(name.indexOf('.') < 0) {
            filePath = filePath + ".html";
            file = new File(filePath);
            name = name + ".html";
        }
        String path = file.getParent();
        Rtpl.tplFileBaseDir = path;
        this.make(data, readFile(Rtpl.getFilePath(name)));
    }

    /**
     * Make a html string from given data and source template string.
     *
     * @param data: data to render
     * @param src:  view template to render
     * @return a html string
     */
    public String make(Data data, String src) {
        this.scope.put("this", data);
        this.tpl = src;
        this.value = Rtpl.process(this.scope, this.tpl, 0);
        return this.value;
    }

    /**
     * Compile given data and a template file into a html string.
     * @param data: data to render
     * @param filePath: view template file to render
     * @return a html string
     */
    public static String compile(Data data, String filePath) {
        return new Rtpl(data, filePath).value;
    }

    /**
     * Compile given data and source template string.
     *
     * @param data:     data to render
     * @param src:      view template to render
     * @param isString: a flag indicating that the input is a string instead of a filename
     * @return a html string
     */
    public static String compile(Data data, String src, boolean isString) {
        return new Rtpl().make(data, src);
    }

    @Override
    public String toString() {
        return this.value;
    }

    /**
     * Get the full name of the template file, with the dir path known above.
     * @param tplName: name of the template file
     * @return full name
     */
    private static String getFilePath(String tplName) {
        return (Rtpl.tplFileBaseDir == null ? "." : Rtpl.tplFileBaseDir) + "/" + (tplName.endsWith(".html") ? tplName : (tplName + ".html"));
    }

    /**
     * Get the type of a frag.
     * @param frag: a string in {{}}
     * @return frag type represented by an integer
     */
    private static int typeOfFrag(String frag) {
        if (frag.startsWith("if ")) return 1; // "if";
        if (frag.startsWith("else if ")) return 2; // "elseif";
        if (frag.startsWith("else")) return 3; // "else";
        if (frag.startsWith("/if")) return 4; // "endif";
        if (frag.startsWith("each ")) return 5; // "each";
        if (frag.startsWith("/each")) return 6; // "endeach";
        if (frag.startsWith("set ")) return 7; // "set";
        if (frag.startsWith("include ")) return 8; // "include";
        return 9; // "get";
    }

    /**
     * Find the occurrence of the target pattern, from the beginning position in a source string,
     * where p1 and p2 which will appear in pair and should be skipped. Sometimes p2 equals the pattern.
     * @param s: the string scope
     * @param beginPos: begin position of the indexing task
     * @param ending: the pattern to find
     * @param p1: the pair beginning
     * @param p2: the pair ending
     * @return the occurrence of the ending pattern
     */
    private static int indexOfEndingOverPairs(String s, int beginPos, String ending, String p1, String p2) {
        int endingOccur = s.indexOf(ending, beginPos);
        if (endingOccur < 0) return -1;

        int p1Count = 0;
        int p2Count = 0;
        int p1Len = p1.length();
        int p2Len = p2.length();
        int i;

        while (true) {
            for (i = beginPos; i < endingOccur; i++) {
                if (s.indexOf(p1, i) == i) {
                    p1Count++;
                    i += p1Len - 1;
                    continue;
                }
                if (s.indexOf(p2, i) == i) {
                    p2Count++;
                    if (p2Count == p1Count) {
                        break;
                    }
                    i += p2Len - 1;
                    continue;
                }
            }
            if (p1Count == p2Count) return endingOccur;
            int nextEndingOccur = s.indexOf(ending, endingOccur + 1);
            if (nextEndingOccur < 0) return endingOccur;
            beginPos = endingOccur;
            endingOccur = nextEndingOccur;
        }
    }

    /**
     * Process the data and template string forward, from a certain position.
     * @param data: data to render
     * @param tpl: template string to render
     * @param begin: begin position
     * @return a html string
     */
    private static String process(Data data, String tpl, int begin) {
        int fragBegin = tpl.indexOf("{{", begin);
        if (fragBegin < 0) return tpl.substring(begin);
        int fragEnd = tpl.indexOf("}}", fragBegin + 2);
        if (fragEnd < 0) return tpl.substring(begin);

        String curFrag = tpl.substring(fragBegin + 2, fragEnd); // get the current frag
        int curFType = Rtpl.typeOfFrag(curFrag); // identify the type of this frag

        // "{{if ...}}..."
        if (curFType == 1) {
            int blockEnd = Rtpl.indexOfEndingOverPairs(tpl, fragEnd + 2, "{{/if}}", "{{if", "{{/if}}");
            if (blockEnd < 0) {
                return tpl.substring(fragBegin);
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

    /**
     * Parse the conditions and blocks from a string.
     * @param blocks: the string to parse
     * @return a LinkedHashMap, where the key is a condition and the value is a block
     */
    private static LinkedHashMap<String, String> getBranchesOfIf(String blocks) {
        LinkedHashMap<String, String> branches = new LinkedHashMap<String, String>();
        String condition;
        String block;
        int p = 0;
        int fragBegin = blocks.indexOf("{{", p);
        if (fragBegin < 0) return branches;
        int fragEnd = blocks.indexOf("}}", fragBegin + 2);
        if (fragEnd < 0) return branches;
        condition = blocks.substring(fragBegin + 5, fragEnd).trim(); // {{if condition}}

        int findBegin = fragEnd + 2;
        while (true) {
            int elseBegin = Rtpl.indexOfEndingOverPairs(blocks, findBegin, "{{else", "{{if", "{{/if}}");
            if (elseBegin < 0) break;
            int elseEnd = blocks.indexOf("}}", elseBegin + 6);
            if (elseEnd < 0) break;
            block = blocks.substring(findBegin, elseBegin);
            branches.put(condition, block);
            condition = blocks.substring(elseBegin + (blocks.indexOf("{{else if", elseBegin) == elseBegin ? 9 : 6), elseEnd).trim();
            findBegin = elseEnd + 2;
        }
        block = blocks.substring(findBegin);
        branches.put(condition, block);

        return branches;
    }

    /**
     * Process an IF string.
     * @param data: the data scope
     * @param branches: the <condition, block> map
     * @return the (processed) branch
     */
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

    /**
     * Return the boolean result of an expression.
     * @param data: the data scope
     * @param exp: the expression to evaluate
     * @return a boolean value
     */
    private static boolean _if(Data data, String exp) {
        if (exp == null || "".equals(exp.trim())) return true;
        return Rtpl._ex(data, exp).toBoolean();
    }

    /**
     * Parse parameters of an EACH fragment.
     * @param frag: an EACH fragment
     * @return a string array
     */
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

    /**
     * Process an EACH string.
     * @param data: the data scope
     * @param tpl: the template string
     * @param ref: the target object to iterate
     * @param valueName: name of value variable
     * @param indexName: name of index variable
     * @return the processed string
     */
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
            ArrayList<Object> a = (ArrayList<Object>) c.val();
            for (int i = 0, len = a.size(); i < len; i++) {
                data.put(indexName, i);
                data.put(valueName, a.get(i));
                sb.append(Rtpl.process(data, tpl, 0));
            }
        }
        return sb.toString();
    }

    /**
     * Parse parameters from a SET fragment.
     * @param frag: a SET frag
     * @return a string array
     */
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

    /**
     * Assign a value to a reference.
     * @param data: the data scope
     * @param ref: the reference
     * @param value: the value
     */
    private static void _set(Data data, String ref, Object value) {
        data.put(ref, value);
    }

    /**
     * Process a SET string, namely to assign a value to a reference.
     * @param data: the data scope
     * @param ref: the reference
     * @param exp: the expression string
     * @return the processed string
     */
    private static String processSet(Data data, String ref, String exp) {
        Rtpl._set(data, ref, Rtpl._ex(data, exp));
        return "";
    }

    /**
     * Class of token list.
     */
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
            if (t != 0) this.data = v;
            else this.list = new ArrayList<TokenList>();
        }

        public TokenList add(int t, Object v) {
            if (this.type != 0) {
                int ot = this.type;
                Object od = this.data;
                this.type = 0;
                this.data = null;
                this.list = new ArrayList<TokenList>();
                this.add(ot, od).add(t, v);
            } else {
                if (this.list == null) this.list = new ArrayList<TokenList>();
                this.list.add(new TokenList(t, v));
            }
            return this;
        }

        public TokenList add(TokenList tl) {
            if (this.type != 0) {
                this.add(tl.type, tl.data);
            } else {
                this.list.add(tl);
            }
            return this;
        }

        public TokenList get(int index) {
            if (this.type != 0) return null;
            return this.list.get(index);
        }

        public TokenList set(int index, int type, Object v) {
            if (this.type != 0) return this;
            this.list.set(index, type == 0 ? (TokenList) v : new TokenList(type, v));
            return this;
        }

        public TokenList concat(TokenList tl) {
            if (tl.type != 0) {
                return this.add(tl);
            }
            if (this.type != 0) {
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
            if (this.type == 0) {
                StringBuilder sb = new StringBuilder("");
                for (TokenList tl : this.list) {
                    sb.append(tl.toString());
                }
                return sb.toString();
            } else if (this.type == 1) {
                return this.data.toString();
            } else {
                return ((Data) this.data).toJsonString();
            }
        }
    }

    /**
     * Translate a string to token list.
     * @param data: the data scope
     * @param oexp: the string (to evaluate)
     * @return a token list
     */
    private static TokenList getTokensOfExp(Data data, String oexp) {
        if (oexp == null || "".equals(oexp)) return null;
        String exp = oexp + " ";

        // ...(...)...
        int bracketBegin = exp.indexOf('(');
        if (bracketBegin >= 0) {
            int leftBracketCount = 1;
            for (int i = bracketBegin + 1, len = exp.length(); i < len; i++) {
                char c = exp.charAt(i);
                if (c == ')') {
                    leftBracketCount--;
                    if (leftBracketCount == 0) {
                        return Rtpl.getTokensOfExp(data, exp.substring(0, bracketBegin))
                                .add(Rtpl.getTokensOfExp(data, exp.substring(bracketBegin + 1, i)))
                                .concat(Rtpl.getTokensOfExp(data, exp.substring(i + 1)));
                    }
                } else if (c == '(') {
                    leftBracketCount++;
                }
            }
            if (leftBracketCount > 0) return null;
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
        for (int i = 0; i < len; i++) {
            char c = charArr[i];
            if (nowIsStr) {
                if (c == '"' && !(i - 1 >= nowStrBegin && charArr[i - 1] == '\\')) {
                    r.add(new TokenList(2, new Data(exp.substring(nowStrBegin + 1, i), 4))); // add a data
                    nowIsStr = false;
                    nowIsRef = false;
                }
                continue;
            }
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '$' || c == '_' || c == '.'
                    || (c >= '0' && c <= '9')) { // do with letter
                if (nowIsRef) continue;
                nowIsRef = true;
                nowRefBegin = i;
            } else if ("+-*/%!".indexOf(c) >= 0) { // do with +-*/%
                if (nowIsRef) {
                    r.add(2, Rtpl._get(data, exp.substring(nowRefBegin, i), false)); // add a data
                    nowIsRef = false;
                }
                r.add(1, Character.toString(c)); // add an op
            } else if (c == '>' || c == '<') {
                if (nowIsRef) {
                    r.add(2, Rtpl._get(data, exp.substring(nowRefBegin, i), false)); // add a data
                    nowIsRef = false;
                }
                if (i < len - 1 && charArr[i + 1] == '=') { // do with >=, <=
                    r.add(1, Character.toString(c) + "="); // add an op
                    i++;
                } else { // do with >, <
                    r.add(1, Character.toString(c)); // add an op
                }
            } else if ("=&|".indexOf(c) >= 0) {
                if (nowIsRef) {
                    r.add(2, Rtpl._get(data, exp.substring(nowRefBegin, i), false)); // add a data
                    nowIsRef = false;
                }
                if (i < len - 1 && charArr[i + 1] == c) { // do with ==, &&, ||
                    r.add(1, Character.toString(c) + Character.toString(c)); // add an op
                    i++;
                    continue;
                }
                return null;
            } else if (c == '"') { // do with "
                nowIsStr = true;
                nowStrBegin = i;
            } else { // do with whitespace
                if (nowIsRef) {
                    r.add(2, Rtpl._get(data, exp.substring(nowRefBegin, i), false)); // add a data
                    nowIsRef = false;
                }
            }
            continue;
        }

        return r;
    }

    /**
     * Translate a token list to a Data expression and evaluate it.
     *
     * @param expTl: tokens
     * @return a Data instance
     */
    private static Data calDataExp(TokenList expTl) {
        //TODO
        return null;
    }

    /**
     * Return a Data instance evaluated from tokens.
     * @param tl: tokens
     * @return a Data instance
     */
    private static Data evalTokenList(TokenList tl) {
        if (tl.type == 1) return null;
        if (tl.type == 2) return (Data) (tl.data);
        for (int i = 0, len = tl.size(); i < len; i++) {
            if (tl.get(i).type == 0)
                tl.set(i, 2, evalTokenList(tl.get(i)));
        }
        if (tl.size() == 1) {
            tl = tl.get(0);
            return evalTokenList(tl);
        }

        boolean useAviator = true;
        if (useAviator) {//TODO
            String exp = tl.toString();
            Object result = AviatorEvaluator.execute(exp);
            if (result instanceof Long) result = Long.toString((long) result);
            if (result instanceof Boolean) {
                return new Data(result, 1);
            } else if (result instanceof Integer) {
                return new Data(result, 2);
            } else if (result instanceof Float || result instanceof Double) {
                return new Data(result, 3);
            } else if (result instanceof String) {
                String sr = (String) result;
                if (sr.equals("true")) return new Data(true, 1);
                if (sr.equals("false")) return new Data(false, 1);

                try {
                    int vi = Integer.parseInt(sr);
                    return new Data(vi, 2);
                } catch (NumberFormatException ex) {
                    try {
                        double vd = Double.parseDouble(sr);
                        return new Data(vd, 3);
                    } catch (NumberFormatException ex1) {
                        try {
                            float vf = Float.parseFloat(sr);
                            return new Data(new BigDecimal(String.valueOf(vf)).doubleValue(), 3);
                        } catch (NumberFormatException ex2) {
                            return new Data(sr, 4);
                        }
                    }
                }
            } else {
                return new Data(result.toString(), 4);
            }
        } else {
            return Rtpl.calDataExp(tl);
        }
    }

    /**
     * Return a data evaluated from an expression.
     * @param data: the data scope
     * @param exp: the expression
     * @return a Data instance
     */
    private static Data _ex(Data data, String exp) {
        if (exp == null || "".equals(exp)) return null;
        int offset = 0;

        // [...]
        int squareBracketBegin = exp.indexOf('[');
        int squareBracketEnd = exp.indexOf(']');
        if (squareBracketBegin < 0) {
            if (squareBracketEnd >= 0) return null;
        } else if (squareBracketBegin == 0) {
            return null;
        } else {
            if (squareBracketEnd <= squareBracketBegin + 1) return null;
        }
        if (squareBracketBegin > 0) {
            return Rtpl._ex(data, exp.substring(0, squareBracketBegin) + "." + Rtpl._ex(data, exp.substring(squareBracketBegin + 1, squareBracketEnd)).toString() + exp.substring(squareBracketEnd + 1));
        }

        // ... ? ... : ...
        int questionPos = exp.indexOf('?');
        if (questionPos == 0) return null;
        if (questionPos > 0) {
            int colonPos = exp.indexOf(':');
            if (colonPos <= questionPos + 1) return null;
            return Data.op(Rtpl._ex(data, exp.substring(0, questionPos)),
                    Rtpl._ex(data, exp.substring(questionPos + 1, colonPos)),
                    Rtpl._ex(data, exp.substring(colonPos + 1)));
        }

        return Rtpl.evalTokenList(Rtpl.getTokensOfExp(data, exp));
    }

    /**
     * Return the referred data.
     * @param odata: the data scope
     * @param oref: the reference
     * @param wrap: if need to wrap the referred data with "this"
     * @return a Data instance
     */
    private static Data _get(Data odata, String oref, boolean wrap) {
        String ref = oref.trim();

        // const boolean
        if ("true".equals(ref) || "false".equals(ref))
            return new Data("true".equals(ref), 1);

        char ch = ref.charAt(0);
        if (ch >= '0' && ch <= '9') { // const number
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
        } else if (ch == '"') { // const string
            int lastQuotationPos = ref.lastIndexOf('"');
            if (lastQuotationPos <= 0) return null;
            return new Data(ref.substring(1, lastQuotationPos), 4);
        }

        Data data;
        if (wrap) {
            data = new Data();
            data.put("this", (ref == null || "".equals(ref)) ? odata : odata.get(ref));

            // 支持自定义变量渗透进子作用域传递
            Map m = (HashMap) odata.val();
            Iterator it = m.entrySet().iterator();
            String kStr;
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                kStr = (String) e.getKey();
                if ("this".equals(kStr)) continue;
                data.put(kStr, e.getValue());
            }
        } else {
            data = (ref == null || "".equals(ref)) ? null : odata.get(ref);
        }
        return data;
    }

    /**
     * Parse parameters from an INCLUDE fragment.
     * @param frag: an INCLUDE frag
     * @return a string array
     */
    private static String[] getParasOfInclude(String frag) {
        String[] pieces = frag.split(" ");
        String[] paras = new String[2];
        int count = 0;
        for (String p : pieces) {
            if ("".equals(p)) continue;
            if (count == 1) paras[0] = p;
            else if (count == 3) paras[1] = p;
            count++;
        }

        if (paras[0].charAt(0) == '\'' || paras[0].charAt(0) == '"')
            paras[0] = paras[0].substring(1, paras[0].length() - 1);
        return paras;
    }

    /**
     * Process a INCLUDE string.
     * @param odata: the data scope
     * @param name: name of the template to include
     * @param ref: the reference of the data which become the scope of the template to include
     * @return a processed string
     */
    private static String processInclude(Data odata, String name, String ref) {
        Data data = Rtpl._get(odata, ref, true);
        return Rtpl.process(data, Rtpl.readFile(Rtpl.getFilePath(name)), 0);
    }

    /**
     * Process a GET string.
     * @param data: the data scope
     * @param oref: the reference
     * @return the ultimate value in string format
     */
    private static String processGet(Data data, String oref) {
        if (data == null || oref == null) return null;
        String ref = oref.trim();
        if ("".equals(ref)) return "{{}}";
        Data r;
        if (ref.charAt(0) == '#') {
            r = Rtpl._get(data, ref.substring(1), false);
            if (r == null || r.isNull()) return "{{" + oref + "}}";
            return StringEscapeUtils.escapeHtml4(r.toString());//String());
        } else {
            r = Rtpl._get(data, ref, false);
            if (r == null || r.isNull()) return "{{" + oref + "}}";
            return r.toString();//String();
        }
    }

    /**
     * Read a template file to a string.
     * @param fileName: name of the template file
     * @return a string
     */
    public static String readFile(String fileName) {
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
