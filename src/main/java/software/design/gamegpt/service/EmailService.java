package software.design.gamegpt.service;

import org.springframework.mail.SimpleMailMessage;
import software.design.gamegpt.model.ConfirmationToken;

public interface EmailService {
    void sendEmail(SimpleMailMessage email);

    void saveConfirmationToken(ConfirmationToken confirmationToken);

    ConfirmationToken findConfirmationToken(String token);
}
