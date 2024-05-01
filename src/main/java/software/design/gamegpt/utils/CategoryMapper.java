package software.design.gamegpt.utils;

/**
 * Mapper class used to display words instead of numbers for a game (the response from the api is a number)
 * The mapping is taken from the IGDB Api documentation
 */
public class CategoryMapper {
    private static CategoryMapper instance = null;

    private CategoryMapper() {
    }

    public static CategoryMapper getInstance() {
        if (instance == null) {
            instance = new CategoryMapper();
        }
        return instance;
    }

    public String toCategoryString(String value) {
        return switch (value) {
            case "0" -> "main game";
            case "1" -> "dlc addon";
            case "2" -> "expansion";
            case "3" -> "bundle";
            case "4" -> "standalone expansion";
            case "5" -> "mode";
            case "6" -> "episode";
            case "7" -> "season";
            case "8" -> "remake";
            case "9" -> "remaster";
            case "10" -> "expanded game";
            case "11" -> "port";
            case "12" -> "fork";
            case "13" -> "pack";
            default -> "update";
        };
    }
}
