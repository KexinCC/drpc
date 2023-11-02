package org.xiaoheshan.serialize;

import org.xiaoheshan.serialize.impl.JdkSerializer;
import org.xiaoheshan.serialize.impl.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

public class SerializeFactory {

    private final static Map<String, SerializerWrapper> SERIALIZE_CACHE = new HashMap<>();
    private final static Map<Byte, SerializerWrapper> SERIALIZE_CACHE_CODE = new HashMap<>();

    static {
        SerializerWrapper jdk = new SerializerWrapper((byte) 1, "jdk", new JdkSerializer());
        SerializerWrapper json = new SerializerWrapper((byte) 2, "json", new JsonSerializer());
        SERIALIZE_CACHE.put("jdk", jdk);
        SERIALIZE_CACHE.put("json", json);

        SERIALIZE_CACHE_CODE.put((byte) 1, jdk);
        SERIALIZE_CACHE_CODE.put((byte) 2, json);

    }

    /**
     * 使用工厂方法获取序列化器
     * @param serializeType 序列化类型
     * @return 序列化器
     */
    public static SerializerWrapper getSerializer(String serializeType) {
        return SERIALIZE_CACHE.get(serializeType);
    }
    public static SerializerWrapper getSerializer(byte serializeCode) {
        return SERIALIZE_CACHE_CODE.get(serializeCode);
    }



}
