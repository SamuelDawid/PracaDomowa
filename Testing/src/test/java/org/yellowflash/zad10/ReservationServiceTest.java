package org.yellowflash.zad10;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @Mock RoomRepository roomRepository;
    @Mock ReservationRepository reservationRepository;
    @Mock ReservationEmailService emailService;
    @Mock ConfirmationCodeGenerator codeGenerator;
    @Mock EventPublisher eventPublisher;

    @InjectMocks ReservationService service;
    Room room;
    TimeSlot timeSlot;

    @BeforeEach
    void setUp(){
    room = new Room("ROOM-A", "Sala Konferencyjna A", 20, true);
    timeSlot = new TimeSlot(LocalDate.of(2026,6,15), LocalTime.of(10,0),LocalTime.of(12,0));
    }
    void setupSuccessfulReservation(){
        when(roomRepository.findById("ROOM-A")).thenReturn(Optional.of(room));
        when(codeGenerator.generate()).thenReturn("RES-TEST1234");
        when(reservationRepository.save(any())).thenAnswer(invocationOnMock -> {
            Reservation reservation = invocationOnMock.getArgument(0);
            reservation.setId(1L);
            return reservation;
        });
    }
    @Captor ArgumentCaptor<Reservation> reservationCaptor;
    @Captor ArgumentCaptor<ConfirmationEmail> emailCaptor;
    @Captor ArgumentCaptor<String> stringCaptor;

    @Nested
    class ArgumentCaptorTests {
        @Test
        @DisplayName("Powinien zapisać rezerwację z poprawnymi danymi")
        void shouldSaveReservationWithCorrectData() {
            //Arrange
            setupSuccessfulReservation();
            //Act
             service.createReservation("ROOM-A", "anna@firma.pl", timeSlot, 10);
            //assert
            verify(reservationRepository).save(reservationCaptor.capture());
            Reservation result = reservationCaptor.getValue();
            assertAll(
                    () -> assertEquals("ROOM-A", result.getRoomId()),
                    () -> assertEquals("anna@firma.pl", result.getOrganizerEmail()),
                    () -> assertEquals(10, result.getAttendees()),
                    () -> assertEquals("RES-TEST1234", result.getConfirmationCode()),
                    () -> assertEquals(ReservationStatus.CONFIRMED, result.getStatus())
            );
        }

        @Test
        @DisplayName("Powinien wysłać email z poprawną treścią")
        void shouldSendEmailWithCorrectContent() {
            //Arrange
            setupSuccessfulReservation();
            //Act
            service.createReservation("ROOM-A", "anna@firma.pl", timeSlot, 10);
            //assert
            verify(emailService).sendConfirmation(emailCaptor.capture());
            ConfirmationEmail result = emailCaptor.getValue();
            assertAll(
                    () -> assertEquals("anna@firma.pl",result.getRecipientEmail()),
                    () -> assertEquals("Potwierdzenie rezerwacji sali Sala Konferencyjna A",result.getSubject()),
                    () -> assertTrue(result.getBody().contains("RES-TEST1234") && result.getBody().contains("2026-06-15"))
            );
        }

        @Test
        @DisplayName("Powinien opublikować event ze szczegółami rezerwacji")
        void shouldPublishEventWithReservationDetails() {
            ArgumentCaptor<String> eventCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> detailCaptor = ArgumentCaptor.forClass(String.class);
            setupSuccessfulReservation();
            //act
            service.createReservation("ROOM-A", "anna@firma.pl", timeSlot, 10);
            //assert
            verify(eventPublisher).publish(eventCaptor.capture(),detailCaptor.capture());
            assertThat(eventCaptor.getValue()).isEqualTo("RESERVATION_CREATED");
            assertTrue(detailCaptor.getValue().contains("RES-TEST1234") && detailCaptor.getValue().contains("Sala Konferencyjna A"));
        }
    }

    @Nested
    class InOrderTests {
        @Test
        @DisplayName("Powinien wykonać operacje w poprawnej kolejności")
        void shouldExecuteOperationsInCorrectOrder() {
            //Arrange
            setupSuccessfulReservation();
            //act
            service.createReservation("ROOM-A", "anna@firma.pl", timeSlot, 10);
            //assert
            InOrder inOrder = inOrder(roomRepository, reservationRepository, codeGenerator, emailService, eventPublisher);
            inOrder.verify(roomRepository).findById("ROOM-A");
            inOrder.verify(reservationRepository).existsByRoomIdAndTimeSlot(eq("ROOM-A"), any(TimeSlot.class));
            inOrder.verify(codeGenerator).generate();
            inOrder.verify(reservationRepository).save(any(Reservation.class));
            inOrder.verify(emailService).sendConfirmation(any(ConfirmationEmail.class));
            inOrder.verify(eventPublisher).publish(anyString(), anyString());
        }
    }

    @Nested
    class ThenAnswerTests {
        @Test
        @DisplayName("Powinien wygenerować unikalne kody dla wielu rezerwacji")
        void shouldGenerateUniqueCodesForMultipleReservations() {
            //Arrange
            AtomicInteger codeCount = new AtomicInteger(1);
            AtomicLong idCount = new AtomicLong(1);
            when(roomRepository.findById("ROOM-A")).thenReturn(Optional.of(room));
            when(codeGenerator.generate()).thenAnswer(inv -> "RES-" + codeCount.getAndIncrement());
            when(reservationRepository.save(any())).thenAnswer(inv -> {
                Reservation r = inv.getArgument(0);
                r.setId(idCount.getAndIncrement());
                return  r;
            });
            TimeSlot slot1 = new TimeSlot(LocalDate.of(2026,6,15), LocalTime.of(10,0), LocalTime.of(12,0));
            TimeSlot slot2 = new TimeSlot(LocalDate.of(2026,6,16), LocalTime.of(14,0), LocalTime.of(16,0));

            //act
            service.createReservation("ROOM-A", "anna@firma.pl", slot1, 10);
            service.createReservation("ROOM-A", "anna@firma.pl", slot2, 5);
            //assert
            verify(reservationRepository,times(2)).save(reservationCaptor.capture());
            List<Reservation> saved = reservationCaptor.getAllValues();

            assertThat(saved.get(0).getConfirmationCode()).isEqualTo("RES-1");
            assertThat(saved.get(0).getId()).isEqualTo(1L);

            assertThat(saved.get(1).getConfirmationCode()).isEqualTo("RES-2");
            assertThat(saved.get(1).getId()).isEqualTo(2L);
        }
    }

    @Nested
    class ValidationTests {
        @Test
        @DisplayName("Powinien rzucić wyjątek gdy sala nie została znaleziona")
        void shouldThrowWhenRoomNotFound() {
            //arrange
            when(roomRepository.findById("ROOM-X")).thenReturn(Optional.empty());
            //act  + assert
            IllegalArgumentException ex =assertThrows(IllegalArgumentException.class, () -> service.createReservation("ROOM-X","me@gmail.com",timeSlot,15));
            assertEquals("Sala nie istnieje: ROOM-X",ex.getMessage());
            verifyNoInteractions(reservationRepository, emailService, codeGenerator, eventPublisher);
        }

        @Test
        @DisplayName("Powinien rzucić wyjątek gdy zbyt wielu uczestników")
        void shouldThrowWhenTooManyAttendees() {
            //arrange
            when(roomRepository.findById("ROOM-A")).thenReturn(Optional.of(room));
            //
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.createReservation("ROOM-A", "anna@firma.pl", timeSlot, 25));
            assertEquals("Sala Sala Konferencyjna A ma pojemność 20, żądano 25 miejsc",ex.getMessage());
            verifyNoMoreInteractions(reservationRepository, emailService, codeGenerator, eventPublisher);

        }

        @Test
        @DisplayName("Powinien rzucić wyjątek gdy termin jest już zarezerwowany")
        void shouldThrowWhenTimeSlotAlreadyBooked() {
            //arrange
            when(roomRepository.findById("ROOM-A")).thenReturn(Optional.of(room));
            when(reservationRepository.existsByRoomIdAndTimeSlot("ROOM-A",timeSlot)).thenReturn(true);
            //act
            IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.createReservation("ROOM-A","me@g.co",timeSlot,20));
            // assert
            assertEquals("Sala Sala Konferencyjna A jest już zarezerwowana w tym terminie",ex.getMessage());
            verify(codeGenerator,never()).generate();
            verify(reservationRepository,never()).save(any());

        }
    }
}