package org.xiaoheshan.compress;

import lombok.extern.slf4j.Slf4j;
import org.xiaoheshan.compress.impl.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CompressFactory {
    private static final Map<String, CompressWrapper> COMPRESSOR_CACHE = new HashMap<>();
    private static final Map<Integer, CompressWrapper> COMPRESS_CACHE_CODE = new HashMap<>();

    static {
        COMPRESSOR_CACHE.put("gzip", new CompressWrapper((byte) 1, "gzip", new GzipCompressor()));
        // todo add other compressors
//        COMPRESSOR_CACHE.put("snappy", new CompressWrapper((byte)2, "snappy", new SnappyCompressor()));
//        COMPRESSOR_CACHE.put("lzf", new CompressWrapper((byte)3, "lzf", new LzfCompressor()));
//        COMPRESSOR_CACHE.put("lz4", new CompressWrapper((byte)4, "lz4", new Lz4Compressor()));
//        COMPRESSOR_CACHE.put("zstd", new CompressWrapper((byte)5, "zstd", new ZstdCompressor()));
//        COMPRESSOR_CACHE.put("none", new CompressWrapper((byte) 6, "none", new NoneCompressor()));

        COMPRESS_CACHE_CODE.put(1, COMPRESSOR_CACHE.get("gzip"));
        // todo add other compressors
//        COMPRESS_CACHE_CODE.put(1, COMPRESSOR_CACHE.get("snappy"));
//        COMPRESS_CACHE_CODE.put(2, COMPRESSOR_CACHE.get("lzf"));
//        COMPRESS_CACHE_CODE.put(3, COMPRESSOR_CACHE.get("lz4"));
//        COMPRESS_CACHE_CODE.put(4, COMPRESSOR_CACHE.get("zstd"));
//        COMPRESS_CACHE_CODE.put(5, COMPRESSOR_CACHE.get("none"));
    }

    public static CompressWrapper getCompressor(String compressType) {
        CompressWrapper compressWrapper = COMPRESSOR_CACHE.get(compressType);

        if (compressWrapper == null) {
            if (log.isDebugEnabled())
                log.debug("压缩类型[{}]不存在,使用默认压缩类型[gzip]", compressType);
            return COMPRESSOR_CACHE.get("gzip");
        }

        return compressWrapper;
    }

    public static CompressWrapper getCompressor(int compressCode) {
        CompressWrapper compressWrapper = COMPRESS_CACHE_CODE.get(compressCode);

        if (compressWrapper == null) {
            if (log.isDebugEnabled())
                log.debug("压缩类型[{}]不存在,使用默认压缩类型[gzip]", compressCode);
            return COMPRESS_CACHE_CODE.get(1);
        }

        return compressWrapper;
    }

}
