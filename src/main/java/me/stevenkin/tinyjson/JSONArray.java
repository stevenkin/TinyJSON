package me.stevenkin.tinyjson;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by wjg.
 */
public class JSONArray {
    private static final int EXCEPT_BEGIN = 0;
    private static final int EXCEPT_VALUE = 1;
    private static final int EXCEPT_COMMA = 2;
    private static final int END = 3;

    private JSONTokener tokener;

    private int status;

    private List<Object> list;

    public JSONArray(String source){
        this(new JSONTokener(source));
    }

    public JSONArray(JSONTokener tokener){
        this.tokener = tokener;
        this.status = EXCEPT_BEGIN;
        this.list = new ArrayList<>();
        buildArray();
    }

    private void buildArray(){
        Token token = null;
        JSONType type = null;
        while (this.status!=END){
            token = this.tokener.nextToken();
            type = token.getType();
            if(this.status==EXCEPT_VALUE){
                switch(type){
                    case BEGIN_OBJECT:
                        this.tokener.back();
                        this.list.add(new JSONObject(this.tokener));
                        break;
                    case BEGIN_ARRAY:
                        this.tokener.back();
                        this.list.add(new JSONArray(this.tokener));
                        break;
                    case JSON_STRING:
                        this.list.add(token.getContent());
                        break;
                    case JSON_INT:
                        this.list.add(Integer.parseInt(token.getContent()));
                        break;
                    case JSON_DOUBLE:
                        this.list.add(Double.parseDouble(token.getContent()));
                        break;
                    case JSON_BOOLEAN:
                        this.list.add(Boolean.valueOf(token.getContent()));
                        break;
                    case JSON_NULL:
                        this.list.add(new Null());
                        break;
                    default:
                        throw new JSONException("build jsonarray error");
                }
                this.status = EXCEPT_COMMA;
                continue;
            }
            switch(type){
                case BEGIN_ARRAY:
                    checkStatus(EXCEPT_BEGIN,EXCEPT_VALUE);
                    break;
                case COMMA:
                    checkStatus(EXCEPT_COMMA,EXCEPT_VALUE);
                    break;
                case END_ARRAY:
                    checkStatus(EXCEPT_COMMA,END);
                    break;
                default:
                    throw new JSONException("build jsonarray error");
            }
        }
    }

    private void checkStatus(int statue, int changeStatus){
        if(statue==this.status){
            this.status = changeStatus;
        }else{
            throw new JSONException("build jsonarray error");
        }
    }




}
