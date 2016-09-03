/**
 * Created by keshen on 2016/9/2.
 */

import tinyrtpl.Rtpl;
import tinyrtpl.Data;

public class test {
    public static void main(String[] args) {
        System.out.print(Data.typeOf("1"));
        System.out.print(Data.typeOf(2));
        System.out.print(Data.typeOf(2.1));
        System.out.print(Data.typeOf((float)2.1));
        int[] arr = {1, 2};
        System.out.print(Data.typeOf(arr));
        Data data = new Data();
        System.out.print(Data.typeOf(data));
        data.put(1, "2");
        data.put("2", 3);
        data.put(2, "3");
        System.out.print(data.get(2).val());
    }
}