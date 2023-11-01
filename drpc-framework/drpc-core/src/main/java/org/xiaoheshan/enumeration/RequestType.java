package org.xiaoheshan.enumeration;

import lombok.Getter;

/**
 * 标记请求类型
 */
@Getter
public enum RequestType {

    REQUEST((byte) 1, "普通请求"), HEART_HEAT((byte) 2, "心跳检测");

    public byte ID;
    public String TYPE;

    RequestType(byte id, String type) {
        this.ID = id;
        this.TYPE = type;
    }
}
