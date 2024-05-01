package software.design.gamegpt.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.design.gamegpt.model.ConfirmationToken;
import software.design.gamegpt.repository.ConfirmationTokenRepository;

@Service
public class EmailServiceImpl implements EmailService {
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final JavaMailSender javaMailSender;

    public EmailServiceImpl(ConfirmationTokenRepository confirmationTokenRepository, JavaMailSender javaMailSender) {
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.javaMailSender = javaMailSender;
    }

    @Override
    @Async
    public void sendEmail(String destination, String subject, String body) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(destination);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);
        javaMailSender.send(mailMessage);
    }

    @Override
    public void saveConfirmationToken(ConfirmationToken confirmationToken) {
        confirmationTokenRepository.save(confirmationToken);
    }

    @Override
    public ConfirmationToken findConfirmationToken(String token) {
        return confirmationTokenRepository.findByConfirmationToken(token);
    }
}
