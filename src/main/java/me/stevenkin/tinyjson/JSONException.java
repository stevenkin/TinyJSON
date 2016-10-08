package me.stevenkin.tinyjson;

/**
 * Created by wjg.
 */
public class JSONException extends RuntimeException {
    public JSONException(String message){
        super(message);
    }

    public JSONException(String message, Throwable throwable){
        super(message, throwable);
    }
}
