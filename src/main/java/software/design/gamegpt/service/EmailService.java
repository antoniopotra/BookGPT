package software.design.gamegpt.service;

import software.design.gamegpt.model.ConfirmationToken;

public interface EmailService {
    /**
     * @param destination the person to send the email to
     * @param subject     the subject / title of the email
     * @param body        the content of the email
     */
    void sendEmail(String destination, String subject, String body);

    /**
     * Saves a configuration token to the database
     *
     * @param confirmationToken the one to be saved
     */
    void saveConfirmationToken(ConfirmationToken confirmationToken);

    /**
     * Retrieves a ConfirmationToken from the database, used to check if the token is accessed by the correct user
     *
     * @param token the string to search for
     * @return the ConfirmationToken object
     */
    ConfirmationToken findConfirmationToken(String token);
}
