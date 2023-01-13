package koreatech.in.domain.Upload;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;

public class UploadFileFullPath {
    public static final String SLASH = "/";
    private static final String CALENDAR_DECIMAL_FORMAT = "00";

    private final String path;
    private final UploadFileFullName uploadFileFullName;

    public UploadFileFullPath(String uploadPath, String originalFileName) {
        this.path = uploadPath;
        this.uploadFileFullName = UploadFileFullName.from(originalFileName);
    }

    //유닉스 시스템에서의 경로를 반환한다.
    public String fileFullPath() {
        return updateFileSeparator(value());
    }
    //윈도우 시스템에서의 경로를 반환한다.
    public String value() {
        return path + getCalendarPath() + uploadFileFullName.getFileFullName();
    }

    private String getCalendarPath() {
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
}

