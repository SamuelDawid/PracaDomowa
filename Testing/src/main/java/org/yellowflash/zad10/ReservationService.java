package org.yellowflash.zad10;

public class ReservationService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationEmailService emailService;
    private final ConfirmationCodeGenerator codeGenerator;
    private final EventPublisher eventPublisher;

    public ReservationService(RoomRepository roomRepository,
                              ReservationRepository reservationRepository,
                              ReservationEmailService emailService,
                              ConfirmationCodeGenerator codeGenerator,
                              EventPublisher eventPublisher) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
        this.emailService = emailService;
        this.codeGenerator = codeGenerator;
        this.eventPublisher = eventPublisher;
    }

    public Reservation createReservation(String roomId, String organizerEmail,
                                         TimeSlot timeSlot, int attendees) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Sala nie istnieje: " + roomId));

        if (attendees > room.getCapacity()) {
            throw new IllegalArgumentException(
                    String.format("Sala %s ma pojemność %d, żądano %d miejsc",
                            room.getName(), room.getCapacity(), attendees));
        }

        if (reservationRepository.existsByRoomIdAndTimeSlot(roomId, timeSlot)) {
            throw new IllegalStateException(
                    "Sala " + room.getName() + " jest już zarezerwowana w tym terminie");
        }

        String confirmationCode = codeGenerator.generate();

        Reservation reservation = new Reservation();
        reservation.setRoomId(roomId);
        reservation.setOrganizerEmail(organizerEmail);
        reservation.setTimeSlot(timeSlot);
        reservation.setAttendees(attendees);
        reservation.setConfirmationCode(confirmationCode);
        reservation.setStatus(ReservationStatus.CONFIRMED);

        Reservation saved = reservationRepository.save(reservation);

        String subject = "Potwierdzenie rezerwacji sali " + room.getName();
        String body = String.format(
                "Rezerwacja potwierdzona!\nSala: %s\nData: %s\nGodziny: %s - %s\nKod: %s",
                room.getName(),
                timeSlot.getDate(),
                timeSlot.getStartTime(),
                timeSlot.getEndTime(),
                confirmationCode
        );
        emailService.sendConfirmation(new ConfirmationEmail(organizerEmail, subject, body));

        eventPublisher.publish("RESERVATION_CREATED",
                "Rezerwacja " + confirmationCode + " dla sali " + room.getName());

        return saved;
    }
}
