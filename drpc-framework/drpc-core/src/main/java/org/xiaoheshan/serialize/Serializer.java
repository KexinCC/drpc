package org.xiaoheshan.serialize;

/**
 * 序列化器
 */
public interface Serializer {
    /**
     * 抽象的序列化方法
     * @param object 待序列化的实例
     * @return 序列化完成后的数据
     */
    byte[] serialize(Object object);

    /**
     * 反序列化的方法
     * @param bytes 待反序列化的数组
     * @param clazz 目标类的class对象
     * @return 目标实例
     * @param <T> 目标类范型
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
