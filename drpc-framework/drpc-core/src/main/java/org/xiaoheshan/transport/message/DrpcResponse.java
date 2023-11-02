package org.xiaoheshan.transport.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DrpcResponse {
    private long requestId;

    // 压缩的类型 序列化的方式
    private byte compressType;
    private byte serializeType;

    // 响应类型 1 成功 2 异常
    private byte code;

    // 具体消息体
    private Object body;

}
