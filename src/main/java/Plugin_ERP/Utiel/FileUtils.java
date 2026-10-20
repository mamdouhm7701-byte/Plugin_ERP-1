package Plugin_ERP.Utiel;

import Plugin_ERP.Bots.LogUtils;

import java.io.File;

public class FileUtils {
    private static String userDir = PropertyReader.getProperty("user.dir") + File.separator;

    // rename
    public static void renameFile(String oldPath, String newPath) {
        try {
            File oldFile = new File(userDir + oldPath);
            File newFile = new File(userDir + newPath);
            if (oldFile.exists()) {
                boolean renamed = oldFile.renameTo(newFile);
                if (renamed) {
                    LogUtils.info("File renamed from " + oldPath + " to " + newPath);
                } else {
                    LogUtils.error("Failed to rename file from " + oldPath + " to " + newPath);
                }
            } else {
                LogUtils.error("File not found: " + oldPath);
            }
        } catch (Exception e) {
            // log
            LogUtils.error(
                    "Failed to rename file from "
                            + oldPath
                            + " to "
                            + newPath
                            + " - "
                            + e.getMessage());
        }
    }

    // create
    public static void creatdictory(String path) {
        try {
            File file = new File(userDir + path);
            if (!file.exists()) {
                boolean created = file.mkdirs();
                if (created) {
                    LogUtils.info("Directory created: " + path);
                } else {
                    LogUtils.error("Failed to create directory: " + path);
                }
            }

        } catch (Exception e) {
            // log
            LogUtils.error("Failed to create directory: " + path + " - " + e.getMessage());
        }
    }

    // claenup
    public static void clean(File file) {
        try {
            org.apache.commons.io.FileUtils.deleteDirectory(file);
        } catch (Exception e) {
            // log
            LogUtils.error(
                    "Failed to clean file: " + file.getAbsolutePath() + " - " + e.getMessage());
        }
    }
}
