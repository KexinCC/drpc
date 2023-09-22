package org.xiaoheshan;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

public class NettyTest {

    @Test
    public void testByteBuf() {
        ByteBuf header = Unpooled.buffer();
        ByteBuf body = Unpooled.buffer();

        // 通过逻辑组装而不是物理拷贝，实现在jvm中零拷贝
        CompositeByteBuf byteBuf = Unpooled.compositeBuffer();
        byteBuf.addComponents(header, body);

    }

    @Test
    public void testWrapper() {
        byte[] buf = new byte[1024];
        byte[] buf2 = new byte[1024];

        // 共享byte数组的内容而不是拷贝
        ByteBuf byteBuf = Unpooled.wrappedBuffer(buf, buf2);
    }

    @Test
    public void testSlice() {
        byte[] buf = new byte[1024];
        byte[] buf2 = new byte[1024];

        // 使用共享地址而非拷贝
        ByteBuf byteBuf = Unpooled.wrappedBuffer(buf, buf2);
        ByteBuf buf1 = byteBuf.slice(1, 5);
        ByteBuf buf3 = byteBuf.slice(6, 15);
    }

    @Test
    public void testMessage() throws IOException {
        ByteBuf message = Unpooled.buffer();
        message.writeBytes("xhs".getBytes(StandardCharsets.UTF_8));
        message.writeByte(1);
        message.writeShort(125);
        message.writeInt(256);
        message.writeByte(1);
        message.writeByte(0);
        message.writeByte(2);
        message.writeLong(251455L);
        // 用对象流转化为字节数组
        String s = "123123123";
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(arrayOutputStream);
        oos.writeObject(s);
        byte[] buf = arrayOutputStream.toByteArray();
        message.writeBytes(buf);
        System.out.println(message);
    }

}
