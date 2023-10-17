package org.xiaoheshan;

import org.xiaoheshan.discovery.RegistryConfig;

public class Application {
    public static void main(String[] args) {
        // 像尽一切办法获取代理对象
        ReferenceConfig<HelloDrpc> reference = new ReferenceConfig<>();
        reference.setInterfaceRef(HelloDrpc.class);


        DrpcBootstrap.getInstance()
                .application("first-drpc-consumer")
                .registry(new RegistryConfig("zookeeper://nas.kexincc.club:2181"))
                .reference(reference)
                .start();

        HelloDrpc helloDrpc = reference.get();
        helloDrpc.sayHi("hi");

    }


}
