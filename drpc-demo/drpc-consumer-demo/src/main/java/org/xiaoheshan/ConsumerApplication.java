package org.xiaoheshan;

import lombok.extern.slf4j.Slf4j;
import org.xiaoheshan.discovery.RegistryConfig;

@Slf4j
public class ConsumerApplication {
    public static void main(String[] args) {
        // 想尽一切办法获取代理对象
        ReferenceConfig<HelloDrpc> reference = new ReferenceConfig<>();
        reference.setInterfaceRef(HelloDrpc.class);


        DrpcBootstrap.getInstance()
                .application("first-drpc-consumer")
                .registry(new RegistryConfig("zookeeper://nas.kexincc.club:2181"))
                .reference(reference);

        HelloDrpc helloDrpc = reference.get();
        String hi = helloDrpc.sayHi("hihi");
        log.info("hi ---- > {}", hi);



    }



}
