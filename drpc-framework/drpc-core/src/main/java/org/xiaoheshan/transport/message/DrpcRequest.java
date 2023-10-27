package org.xiaoheshan.transport.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.Authenticator;

/**
 * 服务调用发起的请求内容
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DrpcRequest {

    // 请求的id
    private long requestId;

    // 请求的类型 压缩的类型 序列化的方式
    private byte requestType;
    private byte compressType;
    private byte serializeType;

    // 具体消息体
    private RequestPayload requestPayload;


}
