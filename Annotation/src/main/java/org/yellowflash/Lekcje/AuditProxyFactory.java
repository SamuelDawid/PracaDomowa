package org.yellowflash.Lekcje;
import java.lang.reflect.Proxy;

public final class AuditProxyFactory {

    private AuditProxyFactory() {
        // utility class
    }

    @SuppressWarnings("unchecked")
    public static <T> T createAuditedProxy(T target, AuditLogger logger, Class<T> interfaceType) {
        return (T) Proxy.newProxyInstance(
                interfaceType.getClassLoader(),
                new Class<?>[]{interfaceType},
                new AuditInvocationHandler(target, logger)
        );
    }
}