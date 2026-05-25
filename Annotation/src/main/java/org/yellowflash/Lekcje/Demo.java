package org.yellowflash.Lekcje;


public class Demo {
    public static void main(String[] args) throws InterruptedException {
        AuditLogger auditLogger = new AuditLogger();
        UserService target = new UserServiceImpl();

        // Tworzymy proxy z audytem
        UserService userService = AuditProxyFactory.createAuditedProxy(
                target,
                auditLogger,
                UserService.class
        );

        // Wywołania – audyt uruchomi się tylko dla metod z @Audit
        userService.createUser("alice@example.com");
        userService.listUsers();
        userService.healthCheck();
    }
}