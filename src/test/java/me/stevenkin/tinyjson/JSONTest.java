package me.stevenkin.tinyjson;

/**
 * Created by wjg.
 */
public class JSONTest {
    public static void main(String[] args){
        String json = "{\"key3\":{}}";
        //String json = "[   \n\n\"str\\ning\",10,10.2]";
        System.out.println(new JSONObject(json));
        //System.out.println(new JSONArray(json));
    }
}
