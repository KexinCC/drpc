package org.xiaoheshan.serialize.impl;

import lombok.extern.slf4j.Slf4j;
import org.xiaoheshan.exception.SerializeException;
import org.xiaoheshan.serialize.Serializer;

import java.io.*;

@Slf4j
public class JdkSerializer implements Serializer {

    @Override
    public byte[] serialize(Object object) {
        if (object == null) {
            return null;
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos);
        ) {
            oos.writeObject(object);
            if (log.isDebugEnabled()) {
                log.debug("序列化对象[{}]成功", object.getClass().getName());
            }
            System.out.println(baos.toByteArray().length);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("序列化对象[{}]时出现异常", object);
            throw new SerializeException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if (bytes == null || clazz == null) {
            return null;
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bais);
        ) {
            if (log.isDebugEnabled()) {
                log.debug("反序列化对象[{}]成功", clazz.getName());
            }
            return (T)ois.readObject();
        } catch (IOException e) {
            log.error("反序列化对象[{}]时出现异常", clazz.getName());
            throw new SerializeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
