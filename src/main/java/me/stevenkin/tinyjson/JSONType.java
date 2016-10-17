package me.stevenkin.tinyjson;

/**
 * Created by wjg.
 */
public enum JSONType {
    BEGIN_OBJECT,
    END_OBEJCT,
    JSON_STRING,
    JSON_INT,
    JSON_DOUBLE,
    JSON_BOOLEAN,
    JSON_NULL,
    BEGIN_ARRAY,
    END_ARRAY,
    COLON,
    COMMA,
    END
}
