package es.codeurjc.mca.practica_1_cloud_ordinaria_2021.image;

import java.io.File;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service("storageService")
@Profile("production")
public class RemoteImageService implements ImageService {

    public static AmazonS3 s3;

    @Value("${amazon.s3.region}")
    private String awsRegion;

    @Value("${amazon.s3.bucket-name}")
    private String awsBucketName;

    public RemoteImageService() {
        this.s3 = AmazonS3ClientBuilder.standard().withRegion(this.awsRegion).build();
    }

    @Override
    public String createImage(MultipartFile multiPartFile) {
        String fileName = "image_" + UUID.randomUUID() + "_" +multiPartFile.getOriginalFilename();
        String path = "events/"+ fileName;
        if (!s3.doesBucketExistV2(awsBucketName)) {
            s3.createBucket(awsBucketName);
        }
        PutObjectRequest objectRequest = new PutObjectRequest(
        		awsBucketName,
                fileName,
                new File(path)
        );
        objectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
        s3.putObject(objectRequest);
        String url = s3.getUrl(awsBucketName, path).toString();
        return url;
    }

    @Override
    public void deleteImage(String image) {
        if (s3.doesObjectExist(this.awsBucketName, image)){
            s3.deleteObject(this.awsBucketName, image);
        }
        else {
            throw new AmazonS3Exception("Object not exists, cannot be deleted");
        }
    }
    
}
