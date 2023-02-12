package koreatech.in.domain.Upload;

import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ByteSize {


    public static final long ZERO = 0L;
    public static final int RIGHT_IS_BIGGER = -1;

    private Long getSize() {
        return size;
    }

    public void validateAcceptable(ByteSize candidate) {
        if (getSize().compareTo(candidate.getSize()) == RIGHT_IS_BIGGER) {
            throw new BaseException(ExceptionInformation.FILE_SIZE_OVER);
        }
    }

    private final Long size;

    public static ByteSize from(Long size) {
        validates(size);
        return new ByteSize(size);
    }

    private static void validates(Long size) {
        if(size.compareTo(ZERO) == RIGHT_IS_BIGGER) {
            throw new BaseException(ExceptionInformation.FILE_INVALID);
        }
    }

    public static final Long _10_MB = 10_000_000L;
    public static ByteSize DEFAULT = from(_10_MB);
    public static ByteSize OWNER_MAX_SIZE = from(_10_MB);
}
