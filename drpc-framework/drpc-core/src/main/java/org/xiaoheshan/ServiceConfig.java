package org.xiaoheshan;

public class ServiceConfig<T> {

    // 接口
    private Class<T> interfaceProvider;
    //具体实现
    private Object ref;


    public Class<T> getInterfaceProvider() {
        return interfaceProvider;
    }

    public void setInterfaceProvider(Class<T> interfaceProvider) {
        this.interfaceProvider = interfaceProvider;
    }

    public Object getRef() {
        return ref;
    }

    public void setRef(Object ref) {
        this.ref = ref;
    }
}
