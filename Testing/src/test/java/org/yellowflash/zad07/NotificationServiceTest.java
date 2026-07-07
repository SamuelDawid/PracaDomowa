package org.yellowflash.zad07;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yellowflash.zad07.exercise.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    EmailSender emailSender;
    @Mock
    SmsSender smsSender;
    @InjectMocks
    NotificationService service;

    @Test
    void shouldReturnTwoMessagesViaSmsAndEmailIfBothValid() {
        // Arrange
        User user = new User(1L, "jan@example.com", "123456789");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        //act
            service.notifyUser(1L,"Hello");
        //assert
            verify(emailSender).send("jan@example.com", "Hello");
            verify(smsSender).send("123456789", "Hello");

    }
    @Test
    void shouldReturnOnlyEmailMessage(){
        // Arrange
        User user = new User(1L, "jan@example.com", null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        //act
        service.notifyUser(1L,"Hello");
        //assert
        verify(emailSender).send("jan@example.com", "Hello");
        verify(smsSender,never()).send(any(),anyString());

    }
    @Test
    void shouldNotSendAny_ThrowsUserNotFoundException(){
        //Arrange
        when(userRepository.findById(any())).thenThrow(UserNotFoundException.class);
        // act + assert
        assertThrows(UserNotFoundException.class, () -> service.notifyUser(1L,"Hello"));
        // check
        verify(emailSender,never()).send(any(),anyString());
        verify(smsSender,never()).send(any(),anyString());
    }
    @Captor
    ArgumentCaptor<String> messageCaptor;

    @Test
    void shouldContainCorrectMessage(){
        // ARRANGE
        User user = new User(1L, "jan@example.com", null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        //ACT
        service.notifyUser(1L,"Hello");
        //ASSERT
        verify(emailSender).send(eq("jan@example.com"),messageCaptor.capture());
        assertThat(messageCaptor.getValue()).isEqualTo("Hello");
    }
}