package software.design.gamegpt.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeMapper {
    public static Integer getYearFromUnixTime(Long unixTime) {
        if (unixTime == null) {
            return 0;
        }
        Date date = new Date(unixTime * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String year = sdf.format(date);
        return Integer.valueOf(year);
    }
}
