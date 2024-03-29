package koreatech.in.domain.Upload;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.multipart.MultipartFile;

import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UploadFiles {
    private final List<UploadFile> uploadFiles;

    public static UploadFiles of(List<MultipartFile> multipartFiles, String domainPath) {
        List<UploadFile> files = multipartFiles.stream()
            .map(multipartFile ->  makeFile(multipartFile, domainPath))
            .collect(Collectors.toList());

        return new UploadFiles(files);
    }

    private static UploadFile makeFile(MultipartFile multipartFile, String domainPath) {
        try {
            return UploadFile.of(multipartFile, domainPath);
        } catch (IOException e) {
            throw new BaseException(ExceptionInformation.FILE_INVALID);
        }
    }

    public List<UploadFile> getUploadFiles() {
        return Collections.unmodifiableList(uploadFiles);
    }
}
