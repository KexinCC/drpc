package org.xiaoheshan.transport.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用来描述 请求调用方请求的接口方法的描述
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestPayload {

    // 接口名字
    private String interfaceName;
    private String methodName; //-- sayHi

    private Class<?>[] parametersType;  //-- {java.lang.String}
    private Object[] parametersValue; //-- "你好"
    private Class<?> returnType; //-- {java.lang.String}


}
