package koreatech.in.util;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class S3Util {
    private String accessKey;
    private String secretKey;

    private AmazonS3 conn;

    public S3Util(String accessKey, String secretKey) {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setProtocol(Protocol.HTTP);
        this.conn = new AmazonS3Client(credentials, clientConfig);
        conn.setEndpoint("s3.ap-northeast-2.amazonaws.com"); // 엔드포인트 설정 [ 아시아 태평양 서울 ]
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
    public void fileUpload(String bucketName, String fileName, byte[] fileData) throws FileNotFoundException {
        String filePath = (fileName).replace(File.separatorChar, '/'); // 파일 구별자를 `/`로 설정(\->/) 이게 기존에 / 였어도 넘어오면서 \로 바뀌는 거같다.
        ObjectMetadata metaData = new ObjectMetadata();

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
}
