package koreatech.in.domain.Upload;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.UUID;

public class UploadFileFullPath {
    private static final String DASH = "-";
    public static final String SLASH = "/";
    private static final String CALENDAR_DECIMAL_FORMAT = "00";

    private final String path;
    private final UploadFileFullName uploadFileFullName;

    public UploadFileFullPath(String path, UploadFileFullName uploadFileFullName) {
        this.path = path;
        this.uploadFileFullName = uploadFileFullName;
    }

    public static UploadFileFullPath of(String uploadPath, String originalFileName) {
        return new UploadFileFullPath(makePath(uploadPath), UploadFileFullName.from(originalFileName));
    }

    //유닉스 시스템에서의 경로를 반환한다.
    public String unixValue() {
        return updateFileSeparator(value());
    }
    //윈도우 시스템에서의 경로를 반환한다.
    public String value() {
        return path + File.separator + uploadFileFullName.getFileFullName();
    }

    public String getFileFullName() {
        return uploadFileFullName.getFileFullName();
    }

    private static String makePath(String uploadPath) {
        return uploadPath + makeCalendarPath() + makeIdentifyPath();
    }

    private static String makeCalendarPath() {
        Calendar calendar = Calendar.getInstance();

        String yearPath = File.separator + calendar.get(Calendar.YEAR);
        String monthPath = File.separator + new DecimalFormat(CALENDAR_DECIMAL_FORMAT).format(calendar.get(Calendar.MONTH) + 1);
        String datePath = File.separator + new DecimalFormat(CALENDAR_DECIMAL_FORMAT).format(calendar.get(Calendar.DATE));

        return yearPath + monthPath + datePath;
    }

    private String updateFileSeparator(String path) {
        if (isUnixFileSeparator()) {
            return path;
        }
        return path.replace(File.separator, SLASH);
    }

    private boolean isUnixFileSeparator() {
        return File.separator.equals(SLASH);
    }

    private static String makeIdentifyPath() {
        return File.separator + UUID.randomUUID() + DASH + System.currentTimeMillis();
    }
}

