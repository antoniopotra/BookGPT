package software.design.gamegpt.utils;

/**
 * Record for validating the user input
 *
 * @param isValid      whether the input (username, email, password) is valid or not
 * @param errorField   set to "" when the input is valid
 * @param errorCode    set to "" when the input is valid
 * @param errorMessage set to "" when the input is valid
 */
public record ValidationResult(boolean isValid, String errorField, String errorCode, String errorMessage) {
}
