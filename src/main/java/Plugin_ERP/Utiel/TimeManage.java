package Plugin_ERP.Utiel;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class TimeManage {
//screenshot-Log_report
    public static String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
                .format(new Date());
    }
//unique data
    public static String getSimpleTimestamp() {
        return Long.toString(System.currentTimeMillis());
    }

    public static void main(String[] args) {
        System.out.println(getTimestamp());
        System.out.println(getSimpleTimestamp());
    }
}
