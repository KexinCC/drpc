package org.xiaoheshan.serialize;

import lombok.extern.slf4j.Slf4j;
import org.xiaoheshan.serialize.impl.HessianSerializer;
import org.xiaoheshan.serialize.impl.JdkSerializer;
import org.xiaoheshan.serialize.impl.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SerializeFactory {

    private final static Map<String, SerializerWrapper> SERIALIZE_CACHE = new HashMap<>();
    private final static Map<Byte, SerializerWrapper> SERIALIZE_CACHE_CODE = new HashMap<>();

    static {
        SerializerWrapper jdk = new SerializerWrapper((byte) 1, "jdk", new JdkSerializer());
        SerializerWrapper hessian = new SerializerWrapper((byte) 2, "hessian", new HessianSerializer());
        SerializerWrapper json = new SerializerWrapper((byte) 3, "json", new JsonSerializer());


        SERIALIZE_CACHE.put("jdk", jdk);
        SERIALIZE_CACHE.put("hessian", hessian);
        SERIALIZE_CACHE.put("json", json);


        SERIALIZE_CACHE_CODE.put((byte) 1, jdk);
        SERIALIZE_CACHE_CODE.put((byte) 2, hessian);
        SERIALIZE_CACHE_CODE.put((byte) 3, json);


    }

    /**
     * 使用工厂方法获取序列化器
     * @param serializeType 序列化类型
     * @return 序列化器
     */
    public static SerializerWrapper getSerializer(String serializeType) {
        SerializerWrapper serializerWrapper = SERIALIZE_CACHE.get(serializeType);

        if (serializerWrapper == null) {
            if (log.isDebugEnabled())
                log.debug("序列化类型[{}]不存在,使用默认序列化类型[jdk]", serializeType);
            return SERIALIZE_CACHE.get("jdk");
        }

        return serializerWrapper;
    }

    public static SerializerWrapper getSerializer(byte serializeCode) {
        SerializerWrapper serializerWrapper = SERIALIZE_CACHE_CODE.get(serializeCode);

        if (serializerWrapper == null) {
            if (log.isDebugEnabled())
                log.debug("序列化类型[{}]不存在,使用默认序列化类型[jdk]", serializeCode);

            return SERIALIZE_CACHE_CODE.get((byte) 1);
        }

        return serializerWrapper;
    }



}
