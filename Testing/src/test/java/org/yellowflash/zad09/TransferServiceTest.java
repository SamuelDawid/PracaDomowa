package org.yellowflash.zad09;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {
    @Mock
    AccountRepository accountRepository;
    @Mock
    AuditLogger auditLogger;
    @Mock
    NotificationService notificationService;

    @InjectMocks
    TransferService service;


    @Nested
    class SuccessfulTransferTests{
        Account accountOne = new Account("123","Sam",1000);
        Account accountTwo = new Account("321","NieSam",500);

        @Test
        void shouldTransferMoneyBetweenAccounts(){
           //arrange
            when(accountRepository.findByAccountNumber("123")).thenReturn(Optional.of(accountOne));
            when(accountRepository.findByAccountNumber("321")).thenReturn(Optional.of(accountTwo));
            //act
            service.transfer("123","321",200.0);


            //assert
            assertThat(accountOne.getBalance()).isEqualTo(800);
            assertThat(accountTwo.getBalance()).isEqualTo(700);

            verify(notificationService).sendTransferConfirmation("123",200.0,"321");
            verify(accountRepository).save(accountOne);
            verify(accountRepository).save(accountTwo);
            verify(notificationService,never()).sendTransferFailure("123",200.0,"321");
            verify(auditLogger).logTransfer(any());
        }

    }
}