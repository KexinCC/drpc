package org.xiaoheshan.channelhandler.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.xiaoheshan.DrpcBootstrap;
import org.xiaoheshan.ServiceConfig;
import org.xiaoheshan.enumeration.ResponseType;
import org.xiaoheshan.transport.message.DrpcRequest;
import org.xiaoheshan.transport.message.DrpcResponse;
import org.xiaoheshan.transport.message.RequestPayload;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class MethodCallHandler extends SimpleChannelInboundHandler<DrpcRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DrpcRequest drpcRequest) throws Exception {
        // 1.获取负载
        RequestPayload requestPayload = drpcRequest.getRequestPayload();

        // 2.根据负载内容 进行方法调用
        Object result = callTargetMethod(requestPayload);
        if (log.isDebugEnabled()) {
            log.debug("请求[{}]已经在服务端完成了方法调用", drpcRequest.getRequestId());
        }

        // 3.封装响应
        DrpcResponse drpcResponse = new DrpcResponse();
        drpcResponse.setCode(ResponseType.HEART_BEAT.getCode());
        drpcResponse.setRequestId(drpcRequest.getRequestId());
        drpcResponse.setSerializeType(drpcRequest.getSerializeType());
        drpcResponse.setCompressType(drpcRequest.getCompressType());
        drpcResponse.setBody(result);

        // 4.写出响应
        channelHandlerContext.channel().writeAndFlush(drpcResponse);
    }

    private Object callTargetMethod(RequestPayload requestPayload) {
        String interfaceName = requestPayload.getInterfaceName();
        String methodName = requestPayload.getMethodName();
        Class<?>[] parametersType = requestPayload.getParametersType();
        Object[] parametersValue = requestPayload.getParametersValue();

        // 寻找合适的类进行方法调用
        ServiceConfig<?> serviceConfig = DrpcBootstrap.SERVICE_LIST.get(interfaceName);
        Object refImpl = serviceConfig.getRef();

        // 通过反射调用 1. 获取方法类型 2.执行invoke方法
        Class<?> clazz = refImpl.getClass();
        Method method = null;
        Object returnValue = null;
        try {
            method = clazz.getMethod(methodName, parametersType);
            returnValue = method.invoke(refImpl, parametersValue);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            log.error("调用请服务的[{}]的方法[{}]发生了异常", interfaceName, methodName, e);
            throw new RuntimeException(e);
        }
        return returnValue;
    }
}
