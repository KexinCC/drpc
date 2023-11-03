package org.xiaoheshan.compress.impl;

import lombok.extern.slf4j.Slf4j;
import org.xiaoheshan.compress.Compressor;
import org.xiaoheshan.exception.CompressException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Slf4j
public class GzipCompressor implements Compressor {
    @Override
    public byte[] compress(byte[] bytes) {

        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(baos);
        ) {
            gzipOutputStream.write(bytes);
            gzipOutputStream.finish();
            if (log.isDebugEnabled()) {
                log.debug("压缩前的字节数组长度为:[{}],压缩后的字节数组长度为:[{}]", bytes.length, baos.toByteArray().length);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("对字节数组进行gzip压缩时发生异常", e);
            throw new CompressException(e);
        }

    }

    @Override
    public byte[] decompress(byte[] bytes) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             GZIPInputStream gzipInputStream = new GZIPInputStream(bais);
        ) {
            byte[] result = gzipInputStream.readAllBytes();
            if (log.isDebugEnabled()) {
                log.debug("解压前的字节数组长度为:[{}],解压后的字节数组长度为:[{}]", bytes.length, result.length);
            }
            return result;
        } catch (IOException e) {
            log.error("对字节数组进行gzip解压时发生异常", e);
            throw new RuntimeException(e);
        }
    }
}
