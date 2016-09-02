/**
 * Created by keshen on 2016/9/2.
 */

import tinyrtpl.Rtpl;
import tinyrtpl.Data;

public class test {
    public static void main(String[] args) {
        System.out.println(Data.typeOf("1"));
        System.out.println(Data.typeOf(2));
        System.out.println(Data.typeOf(2.1));
        System.out.println(Data.typeOf((float)2.1));
        int[] arr = {1, 2};
        System.out.println(Data.typeOf(arr));
        Data data = new Data();
        System.out.println(Data.typeOf(data));
        data.put(1, "2");
        data.put("2", 3);
        data.put(2, "3");
        System.out.println(data.get(2).val());
    }
}
