package org.xiaoheshan.impl;

import org.xiaoheshan.HelloDrpc;

public class HelloDrpcImpl implements HelloDrpc {
    @Override
    public String sayHi(String msg) {
        return "hi consumer:" + msg;
    }
}
