package org.xiaoheshan.enumeration;

import lombok.Getter;

@Getter
public enum ResponseType {
    SUCCESS((byte) 1 ,"成功"),
    HEART_BEAT((byte) 2, "心跳"),
    FAIL((byte) 3, "失败");

    private final byte code;
    private final String desc;

    ResponseType(byte code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
