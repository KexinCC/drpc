package org.xiaoheshan.compress.impl;

import org.xiaoheshan.compress.Compressor;

public class SnappyCompressor implements Compressor {
    @Override
    public byte[] compress(byte[] bytes) {
        return new byte[0];
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        return new byte[0];
    }
}
