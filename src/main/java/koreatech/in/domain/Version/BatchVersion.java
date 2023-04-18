package koreatech.in.domain.Version;

import koreatech.in.util.DateUtil;
import lombok.Getter;

@Getter
public class BatchVersion {
    private static final Character ZERO_PADDING = '0';
    private final String version;
    private final VersionTypeEnum type;

    private BatchVersion(String version, VersionTypeEnum type) {
        this.version = version;
        this.type = type;
    }

    public static BatchVersion from(VersionTypeEnum type) {
        return new BatchVersion(makeVersion(), type);
    }

    private static String makeVersion() {
        return String.format("%s%c_%d", DateUtil.getYearOfNow(), ZERO_PADDING, DateUtil.getTimeStampSecondOfNow() );
    }
    public String getType() {
        return type.getType();
    }
}
