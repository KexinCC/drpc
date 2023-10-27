package org.xiaoheshan;

import org.xiaoheshan.discovery.RegistryConfig;
import org.xiaoheshan.impl.HelloDrpcImpl;

public class ProviderApplication {
    public static void main(String[] args) {
        /*
         * 服务提供方、注册服务、启动服务
         * 1.封装要发布的服务
         * 2.定义注册中心
         * 3.通过引导程序，启动服务提供方
         */
        ServiceConfig<HelloDrpc> service = new ServiceConfig<>();
        service.setInterfaceProvider(HelloDrpc.class);
        service.setRef(new HelloDrpcImpl());




        // 配置 -- 应用的名称 -- 注册中心
        DrpcBootstrap.getInstance()
                // 配置启动类名字
                .application("first-rpc-provider")
                // 配置注册中心
                .registry(new RegistryConfig("zookeeper://nas.kexincc.club:2181"))
                // 配置协议
                .protocol(new ProtocolConfig("jdk"))
                // 发布服务
                .publish(service)
                // 启动服务
                .start();


    }
}
