package Plugin_ERP.Util;

import java.io.File;

public class VideoConverterUtils {

    public static File convertAviToMp4(File aviFile) {
        try {
            String mp4Path = aviFile.getAbsolutePath().replace(".avi", ".mp4");
            File mp4File = new File(mp4Path);

            ProcessBuilder pb =
                    new ProcessBuilder(
                            "ffmpeg",
                            "-y",
                            "-i",
                            aviFile.getAbsolutePath(),
                            "-c:v",
                            "libx264",
                            "-preset",
                            "ultrafast",
                            "-crf",
                            "28",
                            "-an",
                            mp4File.getAbsolutePath());

            pb.redirectErrorStream(true);
            Process p = pb.start();
            p.waitFor();

            if (mp4File.exists() && mp4File.length() > 0) {
                return mp4File;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
