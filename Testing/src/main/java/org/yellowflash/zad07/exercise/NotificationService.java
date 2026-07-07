package org.yellowflash.zad07.exercise;

public class NotificationService {

    private final UserRepository userRepository;
    private final EmailSender emailSender;
    private final SmsSender smsSender;

    public NotificationService(UserRepository userRepository,
                               EmailSender emailSender,
                               SmsSender smsSender) {
        this.userRepository = userRepository;
        this.emailSender = emailSender;
        this.smsSender = smsSender;
    }

    public void notifyUser(Long userId, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getEmail() != null) {
            emailSender.send(user.getEmail(), message);
        }

        if (user.getPhoneNumber() != null) {
            smsSender.send(user.getPhoneNumber(), message);
        }
    }
}
