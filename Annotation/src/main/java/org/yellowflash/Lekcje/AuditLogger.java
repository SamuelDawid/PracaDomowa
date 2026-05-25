package org.yellowflash.Lekcje;

public class AuditLogger {

    public void log(String operation, boolean critical, long durationMs) {
        System.out.printf(
                "AUDIT operation=%s, critical=%s, durationMs=%d%n",
                operation,
                critical,
                durationMs
        );
    }
}