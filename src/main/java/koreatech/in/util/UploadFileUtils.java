package koreatech.in.util;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.UUID;

public class UploadFileUtils {
    private static final Logger logger = LoggerFactory.getLogger(UploadFileUtils.class);

    private S3Util s3Util;
    private String bucketName;
    private String domain;

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setS3Util(S3Util s3Util) {
        this.s3Util = s3Util;
    }

    public String getDomain() {
        return domain;
    }

    public String uploadFile(String uploadPath, String originalFileName, byte[] byteData) throws Exception {
        int index = originalFileName.lastIndexOf(".");
        String fileExt = originalFileName.substring(index+1);

        UUID uid = UUID.randomUUID();

        String savedName = "/" + uid.toString() + "-" + System.currentTimeMillis() + "." + fileExt;

        //\2017\12\27 같은 형태로 저장해준다.
        String savedPath = calcPath(uploadPath);

        String uploadedFileName = (savedPath + savedName).replace(File.separatorChar, '/');

        //S3Util 의 fileUpload 메서드로 파일을 업로드한다.
        s3Util.fileUpload(bucketName, uploadPath + uploadedFileName, byteData);
        //System.out.println(uploadPath + uploadedFileName);

        logger.info(uploadedFileName);

        return uploadedFileName;
    }
    // 이미지 업로드 전용
    public String uploadFile(String uploadPath, String originalFileName, byte[] byteData, MultipartFile multipartFile) throws Exception {
        int index = originalFileName.lastIndexOf(".");
        String fileExt = originalFileName.substring(index+1);

        UUID uid = UUID.randomUUID();

        String savedName = "/" + uid.toString() + "-" + System.currentTimeMillis() + "." + fileExt;



        String uploadedFileName = savedName.replace(File.separatorChar, '/');

        //S3Util 의 fileUpload 메서드로 파일을 업로드한다.
        s3Util.fileUpload(bucketName, uploadPath + uploadedFileName, byteData, multipartFile);
        //System.out.println(uploadPath + uploadedFileName);


        return uploadedFileName;
    }
    private String calcPath(String uploadPath) {
        Calendar cal = Calendar.getInstance();
        String yearPath = File.separator + cal.get(Calendar.YEAR);
        String monthPath = yearPath + File.separator + new DecimalFormat("00").format(cal.get(Calendar.MONTH) + 1);
        String datePath = monthPath + File.separator + new DecimalFormat("00").format(cal.get(Calendar.DATE));
        makeDir(uploadPath, yearPath, monthPath, datePath);
        logger.info(datePath);
        return datePath;
    }

    private void makeDir(String uploadPath, String... paths) {
        if (new File(paths[paths.length - 1]).exists()) {
            return;
        }

        for (String path : paths) {
            File dirPath = new File(uploadPath + path);
            if (!dirPath.exists()) {
                dirPath.mkdir();
            }
        }
    }

    public void makeThumbnail(String filePath, String fileName, String fileExt, int width, int height) throws IOException {
        BufferedImage srcImg = ImageIO.read(new File(filePath));

        int oldWidth = srcImg.getWidth();
        int oldHeight = srcImg.getHeight();

        int newWidth = oldWidth;
        int newHeight = (oldWidth * height) / width;

        if(newHeight > oldHeight) {
            newWidth = (oldHeight * width) / height;
            newHeight = oldHeight;
        }

        BufferedImage cropImg = Scalr.crop(srcImg, (oldWidth - newWidth) / 2, (oldHeight - newHeight) / 2, newWidth, newHeight);

        BufferedImage destImg = Scalr.resize(cropImg, width, height);

        File thumbFile = new File("THUMB_" + fileName + fileExt);
        ImageIO.write(destImg, fileExt.toUpperCase(), thumbFile);
    }

    // TODO: 로직 계산할 것.
    public void removeThumbnail(String filePath, String fileName, String fileExt) throws Exception {
        File file = new File(filePath);

        if(!file.exists() || !file.delete()) {
            throw new Exception();
        }

        File file2 = new File("THUMB_" + fileName + fileExt);

        if(!file2.exists() || !file2.delete()) {
            throw new Exception();
        }
    }
}
