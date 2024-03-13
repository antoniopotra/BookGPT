package software.design.gamegpt.utils;

public class Validator {
    private final String pattern;

    public Validator(String pattern) {
        this.pattern = pattern;
    }

    public boolean validate(String input) {
        return input != null && !input.isEmpty() && !input.isBlank() && input.matches(pattern);
    }
}
