package org.yellowflash.zad09;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
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
    Account senderAccount;
    Account receiverAccount;

    @BeforeEach
    void setUp(){
        senderAccount = new Account("ACC-1", "Jan Kowalski", 100.0);
        receiverAccount = new Account("ACC-2", "Anna Nowak", 500.0);
    }

    @Nested
    class SuccessfulTransferTests{
        @BeforeEach
        void setUp(){
            senderAccount = new Account("ACC-1", "Jan Kowalski", 1000.0);
            receiverAccount = new Account("ACC-2", "Anna Nowak", 500.0);
        }
        @Test
        void shouldTransferMoneyBetweenAccounts(){
           //arrange
            when(accountRepository.findByAccountNumber("ACC-1")).thenReturn(Optional.of(senderAccount));
            when(accountRepository.findByAccountNumber("ACC-2")).thenReturn(Optional.of(receiverAccount));
            //act
            service.transfer("ACC-1","ACC-2",200.0);
            //assert
            assertThat(senderAccount.getBalance()).isEqualTo(800);
            assertThat(receiverAccount.getBalance()).isEqualTo(700);

            verify(notificationService).sendTransferConfirmation("ACC-1",200.0,"ACC-2");
            verify(accountRepository).save(senderAccount);
            verify(accountRepository).save(receiverAccount);
            verify(notificationService,never()).sendTransferFailure("ACC-1",200.0,"ACC-2");
            verify(auditLogger).logTransfer(any());
        }

    }
    @Nested class InsufficientFundsTests{
        @BeforeEach
        void setUpNestedFundTest(){
            senderAccount = new Account("ACC-1", "Jan Kowalski", 100.0);
            receiverAccount = new Account("ACC-2", "Anna Nowak", 500.0);
        }
        @Test
        void shouldThrowExceptionWhenInsufficientFunds(){
                //arrange
            when(accountRepository.findByAccountNumber("ACC-1")).thenReturn(Optional.of(senderAccount));
            when(accountRepository.findByAccountNumber("ACC-2")).thenReturn(Optional.of(receiverAccount));
            // act + assert
            assertThrows(InsufficientFundsException.class, () ->  service.transfer("ACC-1","ACC-2",500.0));
            verify(accountRepository,never()).save(senderAccount);
            verify(auditLogger).logTransfer(any());
            verify(notificationService).sendTransferFailure(eq("ACC-1"),eq(500.0),anyString());
        }
        @Test
        void shouldNotSendSuccessNotificationWhenInsufficientFunds(){
            //arrange
            when(accountRepository.findByAccountNumber("ACC-1")).thenReturn(Optional.of(senderAccount));
            when(accountRepository.findByAccountNumber("ACC-2")).thenReturn(Optional.of(receiverAccount));
            // act + assert
            assertThrows(InsufficientFundsException.class, () ->  service.transfer("ACC-1","ACC-2",500.0));
            verify(notificationService).sendTransferFailure(eq("ACC-1"),eq(500.0),anyString());
            verifyNoMoreInteractions(notificationService);
        }
    }
    @Nested class AccountNotFoundTests{

        @Test
        void shouldThrowExceptionWhenSenderAccountNotFound(){
            //arrange
            when(accountRepository.findByAccountNumber("ACC-FAKE")).thenReturn(Optional.empty());
            //act + assert
            assertThrows(AccountNotFoundException.class, () -> service.transfer("ACC-FAKE","ACC-2",1000));
            verifyNoInteractions(auditLogger,notificationService);
        }
        @Test
        void shouldThrowExceptionWhenReceiverAccountNotFound(){
            when(accountRepository.findByAccountNumber("ACC-1")).thenReturn(Optional.of(senderAccount));
            when(accountRepository.findByAccountNumber("ACC-2")).thenReturn(Optional.empty());
            //act + assert
            assertThrows(AccountNotFoundException.class, () -> service.transfer("ACC-1","ACC-2",100));
            verifyNoInteractions(auditLogger,notificationService);

        }
    }
    @Nested class ValidationTests{
        @Test
        void shouldThrowExceptionForNegativeAmount(){
            //act + assert
           IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.transfer("ACC-1","ACC-2",-100));
           assertThat(ex.getMessage()).isEqualTo("Kwota przelewu musi być większa od zera");
            verifyNoInteractions(accountRepository, auditLogger, notificationService);
        }
        @Test
        void shouldThrowExceptionForZeroAmount(){

        }
    }
    @Nested class DailyLimitTest{
        @BeforeEach
        void setUp(){
            senderAccount = new Account("ACC-1", "Jan Kowalski", 1000.0);
            receiverAccount = new Account("ACC-2", "Anna Nowak", 500.0);
        }
        @Test
        void shouldThrowDailyLimitException(){
            DailyLimitExceededException ex = assertThrows(DailyLimitExceededException.class, () -> service.transferWithLimit("ACC-1","ACC-2",200,190));
            assertThat(ex.getMessage()).isEqualTo("Kwota ktora chesz wyslac przekracza Twoj dzienny limit");
            verify(auditLogger).logTransfer(any());
            verifyNoMoreInteractions(notificationService,auditLogger,accountRepository);
        }
        @Test
        void shouldReceiveWithinTheLimit(){
            //arrange
            when(accountRepository.findByAccountNumber("ACC-1")).thenReturn(Optional.of(senderAccount));
            when(accountRepository.findByAccountNumber("ACC-2")).thenReturn(Optional.of(receiverAccount));
            //act
            service.transferWithLimit("ACC-1","ACC-2",500,1000);
            //assert
            assertThat(receiverAccount.getBalance()).isEqualTo(1000);
            assertThat(senderAccount.getBalance()).isEqualTo(500);
            verify(notificationService).sendTransferConfirmation("ACC-1",500,"ACC-2");
            verify(auditLogger).logTransfer(any());
            verify(accountRepository).save(senderAccount);
            verify(accountRepository).save(receiverAccount);
        }
        @Test
        void shouldReceiveWhenAmountEqualsLimit(){
            when(accountRepository.findByAccountNumber("ACC-1")).thenReturn(Optional.of(senderAccount));
            when(accountRepository.findByAccountNumber("ACC-2")).thenReturn(Optional.of(receiverAccount));
            //act
            service.transferWithLimit("ACC-1","ACC-2",500,500);
            //assert
            assertThat(receiverAccount.getBalance()).isEqualTo(1000);
            assertThat(senderAccount.getBalance()).isEqualTo(500);
            verify(notificationService).sendTransferConfirmation("ACC-1",500,"ACC-2");
            verify(auditLogger).logTransfer(any());
            verify(accountRepository).save(senderAccount);
            verify(accountRepository).save(receiverAccount);
        }
    }


}