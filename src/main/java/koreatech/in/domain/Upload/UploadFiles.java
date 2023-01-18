package koreatech.in.domain.Upload;

import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public class UploadFiles {
    private final List<UploadFile> uploadFiles;

    public List<UploadFile> getUploadFiles() {
        return Collections.unmodifiableList(uploadFiles);
    }
}
