package me.stevenkin.tinyjson;

import java.util.*;

/**
 * Created by wjg.
 */
public class JSONObject {
    private static final int EXCEPT_BEGIN = 0;
    private static final int EXCEPT_STRING = 1;
    private static final int EXCEPT_COLON = 2;
    private static final int EXCEPT_VALUE = 5;
    private static final int EXCEPT_COMMA = 3;
    private static final int EXCEPT_END = 6;
    private static final int END = 4;

    private JSONTokener tokener;

    private Map<String,Object> map;

    private Set<Integer> status;

    public JSONObject(JSONTokener tokener){
        this.tokener = tokener;
        this.map = new HashMap<>();
        this.status = new HashSet<>();
        this.status.add(EXCEPT_BEGIN);
        this.buildJSONObject();
    }

    public JSONObject(String source){
        this(new JSONTokener(source));
    }

    public void buildJSONObject(){
        Token token = null;
        JSONType type = null;
        String key = "";
        while(!((this.status.contains(END))&&this.status.size()==1)){
            token = this.tokener.nextToken();
            type = token.getType();
            if((this.status.contains(EXCEPT_VALUE))&&this.status.size()==1){
                Object value = null;
                switch(type){
                    case BEGIN_OBJECT:
                        this.tokener.back();
                        value = new JSONObject(this.tokener);
                        this.map.put(key,value);
                        break;
                    case BEGIN_ARRAY:
                        this.tokener.back();
                        value = new JSONArray(this.tokener);
                        this.map.put(key,value);
                        break;
                    case JSON_STRING:
                        this.map.put(key,token.getContent());
                        break;
                    case JSON_INT:
                        this.map.put(key,Integer.parseInt(token.getContent()));
                        break;
                    case JSON_DOUBLE:
                        this.map.put(key,Double.parseDouble(token.getContent()));
                        break;
                    case JSON_BOOLEAN:
                        this.map.put(key,Boolean.valueOf(token.getContent()));
                        break;
                    case JSON_NULL:
                        this.map.put(key,new Null());
                        break;
                    default:
                        throw new JSONException("build jsonobject error");
                }
                this.status.clear();
                this.status.add(EXCEPT_COMMA);
                this.status.add(EXCEPT_END);
                continue;
            }
            switch(type){
                case BEGIN_OBJECT:
                    checkStatus(EXCEPT_BEGIN,EXCEPT_STRING,EXCEPT_END);
                    break;
                case JSON_STRING:
                    checkStatus(EXCEPT_STRING,EXCEPT_COLON);
                    key = token.getContent();
                    break;
                case COLON:
                    checkStatus(EXCEPT_COLON,EXCEPT_VALUE);
                    break;
                case COMMA:
                    checkStatus(EXCEPT_COMMA,EXCEPT_STRING);
                    break;
                case END_OBEJCT:
                    checkStatus(EXCEPT_END,END);
                    break;
                default:
                    throw new JSONException("build jsonobject error");
            }
        }
        if(!((this.status.contains(END))&&this.status.size()==1)){
            throw new JSONException("build jsonobject error");
        }
    }

    private void checkStatus(int statue, Integer... changeStatus){
        if(this.status.contains(statue)){
            this.status.clear();
            this.status.addAll(Arrays.asList(changeStatus));
        }else{
            throw new JSONException("build jsonobject error");
        }
    }

    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        Iterator<Map.Entry<String,Object>> iterator = this.map.entrySet().iterator();
        boolean isFirst = true;
        while(iterator.hasNext()){
            if(isFirst){
                isFirst = !isFirst;
            }else{
                stringBuilder.append(",");
            }
            Map.Entry<String,Object> entry = iterator.next();
            stringBuilder.append('"');
            stringBuilder.append(escapeString(entry.getKey()));
            stringBuilder.append('"');
            stringBuilder.append(':');
            Object value = entry.getValue();
            if(value instanceof String){
                stringBuilder.append('"');
                stringBuilder.append(escapeString((String)value));
                stringBuilder.append('"');
            }else{
                stringBuilder.append(value.toString());
            }
        }
        stringBuilder.append("}");
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
