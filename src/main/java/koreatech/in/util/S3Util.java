package koreatech.in.util;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.HttpMethod;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class S3Util {
    private final AmazonS3 conn;

    @Autowired
    public S3Util(@Value("${s3.key}") String accessKey, @Value("${s3.secret}") String secretKey) {
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setProtocol(Protocol.HTTP);

        this.conn = AmazonS3ClientBuilder.standard().withRegion(Regions.AP_NORTHEAST_2)
                .withClientConfiguration(clientConfig)
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();
    }

    // 버킷 리스트를 가져오는 메서드이다.
    public List<Bucket> getBucketList() {
        return conn.listBuckets();
    }

    // 버킷을 생성하는 메서드이다.
    public Bucket createBucket(String bucketName) {
        return conn.createBucket(bucketName);
    }

    // 폴더 생성 (폴더는 파일명 뒤에 "/"를 붙여야한다.)
    public void createFolder(String bucketName, String folderName) {
        conn.putObject(bucketName, folderName + "/", new ByteArrayInputStream(new byte[0]), new ObjectMetadata());
    }

    // 파일 업로드
    public void fileUpload(String bucketName, String filePath, byte[] fileData) {

        ObjectMetadata metaData = new ObjectMetadata();
        metaData.setContentLength(fileData.length);   //메타데이터 설정 -->원래는 128kB까지 업로드 가능했으나 파일크기만큼 버퍼를 설정시켰다.

        conn.putObject(
                new PutObjectRequest(bucketName, filePath, new ByteArrayInputStream(fileData), metaData)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    // 이미지 업로드 전용
    public void fileUpload(String bucketName, String fileName, byte[] fileData, MultipartFile multipartFile) throws FileNotFoundException {
        String filePath = (fileName).replace(File.separatorChar, '/'); // 파일 구별자를 `/`로 설정(\->/) 이게 기존에 / 였어도 넘어오면서 \로 바뀌는 거같다.
        ObjectMetadata metaData = new ObjectMetadata();

        metaData.setContentType(multipartFile.getContentType());
        metaData.setContentLength(fileData.length);   //메타데이터 설정 -->원래는 128kB까지 업로드 가능했으나 파일크기만큼 버퍼를 설정시켰다.
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileData); //파일 넣음

        conn.putObject(new PutObjectRequest(bucketName, filePath, byteArrayInputStream, metaData).withCannedAcl(CannedAccessControlList.PublicRead));
    }

    // 파일 삭제
    public void fileDelete(String bucketName, String fileName) {
        String imgName = (fileName).replace(File.separatorChar, '/');
        conn.deleteObject(bucketName, imgName);
    }

    // 파일 URL
    public String getFileURL(String bucketName, String fileName) {
        String imgName = (fileName).replace(File.separatorChar, '/');
        return conn.generatePresignedUrl(new GeneratePresignedUrlRequest(bucketName, imgName)).toString();
    }

    public String generatePreSignedUrlForPut(String bucketName, String filePath) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = PreSignedUrlRequest.of(bucketName, filePath)
                .generate(HttpMethod.PUT, new Date());
        return conn.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }

    @RequiredArgsConstructor(staticName = "of")
    class PreSignedUrlRequest {
        private static final int FILE_EXPIRATION_HOUR = 2;

        private final String bucketName;
        private final String filePath;

        public GeneratePresignedUrlRequest generate(HttpMethod httpMethod, Date now) {
            GeneratePresignedUrlRequest generatePresignedUrlRequest = createPreSignedUrlRequest(httpMethod, now);
            enrichReadAccess(generatePresignedUrlRequest);

            return generatePresignedUrlRequest;
        }

        private GeneratePresignedUrlRequest createPreSignedUrlRequest(HttpMethod httpMethod, Date now) {
            return new GeneratePresignedUrlRequest(bucketName, filePath)
                    .withMethod(httpMethod)
                    .withExpiration(makePreSignedUrlExpiration(now));
        }

        private void enrichReadAccess(GeneratePresignedUrlRequest generatePresignedUrlRequest) {
            generatePresignedUrlRequest.addRequestParameter(
                    Headers.S3_CANNED_ACL,
                    CannedAccessControlList.PublicRead.toString());
        }

        private Date makePreSignedUrlExpiration(Date now) {
            return DateUtil.addHoursToJavaUtilDate(now, FILE_EXPIRATION_HOUR);
        }
    }

}
