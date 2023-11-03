package org.xiaoheshan.serialize.impl;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import lombok.extern.slf4j.Slf4j;
import org.xiaoheshan.serialize.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
public class HessianSerializer implements Serializer {

    @Override
    public byte[] serialize(Object object) {
        if (object == null) {
            return null;
        }

        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ) {
            Hessian2Output hessian2Output = new Hessian2Output(baos);
            hessian2Output.writeObject(object);
            hessian2Output.flush();
            if (log.isDebugEnabled()) {
                log.debug("使用Hessian序列化完成[{}]", object.getClass().getName());
            }
            return baos.toByteArray();

        } catch (IOException e) {
            log.error("使用Hessian序列化失败[{}]", object.getClass().getName(),e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if (bytes == null || clazz == null) {
            return null;
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            Hessian2Input hessian2Input = new Hessian2Input(bais);
            Object object = hessian2Input.readObject(clazz);
            if (log.isDebugEnabled()) {
                log.debug("使用Hessian反序列化完成[{}]", clazz.getName());
            }
            return (T) object;
        } catch (IOException e) {
            log.error("使用Hessian反序列化失败[{}]", clazz.getName(),e);
            throw new RuntimeException(e);
        }
    }
}
