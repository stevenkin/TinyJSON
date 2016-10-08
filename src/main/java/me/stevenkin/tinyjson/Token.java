package me.stevenkin.tinyjson;

/**
 * Created by wjg.
 */
public class Token {
    private String content;
    private JSONType type;

    public Token() {
    }

    public Token(String content, JSONType type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public JSONType getType() {
        return type;
    }

    public void setType(JSONType type) {
        this.type = type;
    }
}
