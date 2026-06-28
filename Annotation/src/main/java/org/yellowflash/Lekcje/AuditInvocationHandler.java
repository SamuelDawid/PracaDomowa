package org.yellowflash.Lekcje;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class AuditInvocationHandler implements InvocationHandler {

    private final Object target;
    private final AuditLogger auditLogger;

    public AuditInvocationHandler(Object target, AuditLogger auditLogger) {
        this.target = target;
        this.auditLogger = auditLogger;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Pobierz "prawdziwą" metodę z klasy implementacji,
        // żeby odczytać adnotacje z implementacji, a nie z interfejsu.
        Method targetMethod = target
                .getClass()
                .getMethod(method.getName(), method.getParameterTypes());

        Audit audit = targetMethod.getAnnotation(Audit.class);

        if (audit == null) {
            // brak adnotacji – zwykłe wywołanie
            return targetMethod.invoke(target, args);
        }

        long start = System.currentTimeMillis();
        try {
            Object result = targetMethod.invoke(target, args);
            long duration = System.currentTimeMillis() - start;
            auditLogger.log(audit.operation(), audit.critical(), duration);
            return result;
        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - start;
            auditLogger.log(audit.operation(), audit.critical(), duration);
            throw ex;
        }

    }
}
