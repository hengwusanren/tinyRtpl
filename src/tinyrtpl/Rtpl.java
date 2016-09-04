/**
 * Created by keshen on 2016/9/2.
 */

package tinyrtpl;

import tinyrtpl.Data;
import java.util.HashMap;
import java.util.Stack;

public class Rtpl {
    private int type; // type of the 1st line; 0: static, 1: if, 2: loop, 3: assign;
    private Data scope;
    private String tpl;
    private Stack<Data> stack;
    private static HashMap<String, String> tokens = new HashMap<String, String>(){
        {
            put("{", "{{");
            put("}", "}}");
        }
    };
    private String value;
    public Rtpl() {
        //TODO
    }
    private void preprocess() {
        //TODO: 保证每行不外乎几种类型
    }
    private static int typeOf(String tpl) {
        return 0;
    }
    public Rtpl(Data data, String tpl) {
        this.scope = data;
        this.tpl = tpl;
        this.type = Rtpl.typeOf(tpl);
        this.value = this.compile();
    }
    public String compile() {
        switch(this.type) {
        case 0:
        case 1:
        case 2:
        case 3:
        default:
            return this.tpl;
        }
    }
    public static String compile(Data data, String tpl) {
        return new Rtpl(data, tpl).compile();
    }
}
