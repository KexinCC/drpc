package org.xiaoheshan.serialize.impl;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.xiaoheshan.serialize.Serializer;
import org.xiaoheshan.transport.message.RequestPayload;

import java.util.Arrays;

@Slf4j
public class JsonSerializer implements Serializer {
    @Override
    public byte[] serialize(Object object) {
        if (object == null) {
            return null;
        }

        byte[] jsonBytes = JSON.toJSONBytes(object);
        if (log.isDebugEnabled()) {
            log.debug("使用Json序列化完成[{}]", object.getClass().getName());
        }

        return jsonBytes;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if (bytes == null || clazz == null) {
            return null;
        }

        T t = JSON.parseObject(bytes, clazz);

        if (log.isDebugEnabled()) {
            log.debug("使用Json反序列化完成[{}]", clazz.getName());
        }

        return t;
    }


    public static void main(String[] args) {
        Serializer serializer = new JsonSerializer();
        RequestPayload requestPayload = new RequestPayload();
        requestPayload.setInterfaceName("org.xiaoheshan.HelloDrpc");
        requestPayload.setMethodName("sayHi");
        byte[] serialize = serializer.serialize(requestPayload);
        RequestPayload deserialize = serializer.deserialize(serialize, RequestPayload.class);

        System.out.println(Arrays.toString(serialize));
        System.out.println(deserialize.toString());
    }
}
