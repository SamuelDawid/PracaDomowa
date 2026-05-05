package pl.kurs.movierental.session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.kurs.movierental.error.RentalStateException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

class SessionLogTest {
    SessionLog log = new SessionLog();

    @Test
    void logCountTest() {
    log.log("test-1");
    assertThat(log.entryCount()).isEqualTo(1);
    }

    @Test
    void logThrRentalStateException() throws Exception {
        log.close();
        RentalStateException ex = assertThrows(RentalStateException.class, () -> log.log("test"));
    }

    @Test
    void closeAfterClose() throws Exception {
        log.close();
       assertThatCode(() -> log.close()).doesNotThrowAnyException();
    }
}