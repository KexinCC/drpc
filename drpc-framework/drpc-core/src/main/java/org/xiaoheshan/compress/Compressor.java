package org.xiaoheshan.compress;

/**
 * 压缩器
 */
public interface Compressor {

    /**
     * 压缩
     *
     * @param bytes 原始字节数组
     * @return 压缩后的字节数组
     */
    byte[] compress(byte[] bytes);

    /**
     * 解压
     *
     * @param bytes 压缩后的字节数组
     * @return 原始字节数组
     */
    byte[] decompress(byte[] bytes);
}
