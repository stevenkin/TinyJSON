package me.stevenkin.tinyjson;

/**
 * Created by wjg.
 */
public class JSONTokenerTest {
    public static void main(String[] args){
        JSONTokener tokener = new JSONTokener("{     \"test\":   \"te   st\\ntest\",\"test1\":    12.4567}");
        Token token = null;
        token = tokener.nextToken();
        while(token.getType()!=JSONType.END){
            System.out.println(token.getContent());
            token = tokener.nextToken();
        }
    }
}
