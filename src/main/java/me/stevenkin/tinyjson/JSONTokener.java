package me.stevenkin.tinyjson;

/**
 * Created by wjg.
 */
public class JSONTokener {
    private char[] source;
    private int index;

    public JSONTokener(String source) {
        this.index = 0;
        this.source = source.toCharArray();
    }

    public void next(){
        this.index++;
    }

    public String next(int limit){
        StringBuilder stringBuilder = new StringBuilder(limit);
        int n = this.index;
        this.index+=limit;
        if(this.index>=this.source.length)
            throw new JSONException("json parser syntax error");
        for(int i = n; i<this.index; i++){
            stringBuilder.append(this.source[i]);
        }
        return stringBuilder.toString();
    }

    public void back(){
        this.index--;
    }

    public void back(int limit){
        this.index-=limit;
    }

    private Token optJSONString(){
        next();
        StringBuilder stringBuilder = new StringBuilder();
        final int ESCAPE = 1;
        final int PLAIN = 0;
        int status = PLAIN;
        while(true){
            if(this.index>=this.source.length)
                throw new JSONException("json parser syntax error");
            char c = this.source[this.index];
            if(c=='\b'||c=='\t'||c=='\n'||c=='\f'||c=='\r')
                throw new JSONException("json parser syntax error");
            if(status==ESCAPE){
                switch(c){
                    case '\\':
                        stringBuilder.append('\\');
                        break;
                    case '\'':
                        stringBuilder.append('\'');
                        break;
                    case '\"':
                        stringBuilder.append('\"');
                        break;
                    case 'b':
                        stringBuilder.append('\b');
                        break;
                    case 't':
                        stringBuilder.append('\t');
                        break;
                    case 'n':
                        stringBuilder.append('\n');
                        break;
                    case 'f':
                        stringBuilder.append('\f');
                        break;
                    case 'r':
                        stringBuilder.append('\r');
                        break;
                    case 'u':
                        stringBuilder.append(optUnicode());
                        back();
                        break;
                    default:
                        throw new JSONException("json parser syntax error");
                }
                status = PLAIN;
            }else{
                if(c=='\\')
                    status = ESCAPE;
                else{
                    if(c=='"') {
                        next();
                        return new Token(stringBuilder.toString(), JSONType.JSON_STRING);
                    }
                    stringBuilder.append(c);
                }
            }
            next();
        }
    }

    private char optUnicode(){
        next();
        if(this.index>=this.source.length)
            throw new JSONException("json parser syntax error");
        String unicode = next(4);
        try {
            int charInt = Integer.parseInt(unicode, 16);
            return (char)charInt;
        }catch (Exception e){
            throw new JSONException("json parser syntax error",e);
        }
    }

    private Token optJSONBoolean(char c){
        if(c=='t'){
            String str = next(4);
            if("true".equals(str)){
                return new Token("true",JSONType.JSON_BOOLEAN);
            }else{
                throw new JSONException("json parser syntax error");
            }
        } else if(c=='f'){
            String str = next(5);
            if("false".equals(str)){
                return new Token("false",JSONType.JSON_BOOLEAN);
            }else{
                throw new JSONException("json parser syntax error");
            }
        }else{
            throw new JSONException("json parser syntax error");
        }
    }

    private Token optJSONNull(){
        String str = next(4);
        if("null".equals(str)){
            return new Token("null",JSONType.JSON_NULL);
        }else{
            throw new JSONException("json parser syntax error");
        }
    }

    private Token optJSONNumber(){
        StringBuilder stringBuilder = new StringBuilder();
        outer:while(this.index<this.source.length){
            char c = this.source[this.index];
            switch(c){
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case '.':
                    stringBuilder.append(c);
                    break;
                case ',':
                case ' ':
                case '\b':
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ']':
                case '}':
                    break outer;
                default:
                    throw new JSONException("json parser syntax error");
            }
            next();
        }
        String numberStr = stringBuilder.toString();
        int i = numberStr.indexOf(".");
        Number a = null;
        if(i<0){
            a = Integer.parseInt(numberStr);
        }else{
            a = Double.parseDouble(numberStr);
        }
        return new Token(a.toString(),JSONType.JSON_NUMBER);
    }

    public Token nextToken(){
        while(true){
            if(this.index>=this.source.length)
                return new Token("",JSONType.END);
            char c = this.source[index];
            switch(c){
                case ' ':
                case '\b':
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                    next();
                    break;
                case '"':
                    return optJSONString();
                case 'b':
                    return optJSONBoolean('b');
                case 'f':
                    return optJSONBoolean('f');
                case 'n':
                    return optJSONNull();
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case '+':
                case '-':
                case '.':
                    return optJSONNumber();
                case '{':
                    next();
                    return new Token("{",JSONType.BEGIN_OBJECT);
                case '}':
                    next();
                    return new Token("}",JSONType.END_OBEJCT);
                case '[':
                    next();
                    return new Token("[",JSONType.BEGIN_ARRAY);
                case ']':
                    next();
                    return new Token("]",JSONType.END_ARRAY);
                case ':':
                    next();
                    return new Token(":",JSONType.COLON);
                case ',':
                    next();
                    return new Token(",",JSONType.COMMA);
            }
        }
    }
}
