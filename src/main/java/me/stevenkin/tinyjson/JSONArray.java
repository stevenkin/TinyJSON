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

    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        boolean isFirst = true;
        for(Object value : this.list){
            if(isFirst){
                isFirst = !isFirst;
            }else{
                stringBuilder.append(",");
            }
            if(value instanceof String){
                stringBuilder.append('"');
                stringBuilder.append(escapeString((String)value));
                stringBuilder.append('"');
            }else{
                stringBuilder.append(value.toString());
            }
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    private String escapeString(String string){
        char[] chars = string.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i<chars.length;i++){
            char c = chars[i];
            switch (c){
                case '\b':
                    stringBuilder.append("\\b");
                    break;
                case '\t':
                    stringBuilder.append("\\t");
                    break;
                case '\n':
                    stringBuilder.append("\\n");
                    break;
                case '\f':
                    stringBuilder.append("\\f");
                    break;
                case '\r':
                    stringBuilder.append("\\r");
                    break;
                default:
                    if ((c >= '\u0080' && c < '\u00a0') || (c >= '\u2000' && c < '\u2100')) {
                        stringBuilder.append("\\u").append(Integer.toHexString(c));
                    }else{
                        stringBuilder.append(c);
                    }
            }
        }
        return stringBuilder.toString();
    }


}
