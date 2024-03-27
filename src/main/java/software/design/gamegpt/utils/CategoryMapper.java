package software.design.gamegpt.utils;

public class CategoryMapper {
    public static String toCategoryString(String value) {
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
