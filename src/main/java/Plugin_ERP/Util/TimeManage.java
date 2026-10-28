/* شرح الملف: تجهيز الوقت المستخدم في أسماء الملفات وسجلات التشغيل.
 */
package Plugin_ERP.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeManage {
    // screenshot-Log_report
    public static String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
    }

    // unique data
    public static String getSimpleTimestamp() {
        return Long.toString(System.currentTimeMillis());
    }

    public static void main(String[] args) {
        System.out.println(getTimestamp());
        System.out.println(getSimpleTimestamp());
    }
}
