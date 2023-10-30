package koreatech.in.domain.Upload;

import java.util.Date;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class PreSignedUrlResult {

    private final String url;
    private final Date expiration;
}
