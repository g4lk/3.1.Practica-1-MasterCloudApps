package es.codeurjc.mca.practica_1_cloud_ordinaria_2021.image;
import java.io.IOException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service("storageService")
@Profile("production")
public class RemoteImageService implements ImageService {

    private AmazonS3 s3;

    private String awsRegion;

    private String awsBucketName;
    
    public RemoteImageService(
        @Value("${amazon.s3.region}") String awsRegion, 
        @Value("${amazon.s3.bucket-name}") String awsBucketName) {

        this.awsRegion = awsRegion;
        this.awsBucketName = awsBucketName;

        this.s3 = AmazonS3ClientBuilder.standard().withRegion(this.awsRegion).build();
    }

    @Override
    public String createImage(MultipartFile multiPartFile) {
        String fileName = "image_" + UUID.randomUUID() + "_" +multiPartFile.getOriginalFilename();
        String path = "events/"+ fileName;
        if (!s3.doesBucketExistV2(awsBucketName)) {
            s3.createBucket(awsBucketName);
        }

        ObjectMetadata omd = new ObjectMetadata();
        omd.setContentType(multiPartFile.getContentType());
        omd.setContentLength(multiPartFile.getSize());
        omd.setHeader("filename", multiPartFile.getName());

        PutObjectRequest objectRequest;
        try {
            objectRequest = new PutObjectRequest(
            		awsBucketName,
                    path,
                    multiPartFile.getInputStream(),
                    omd
            );
            objectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
            s3.putObject(objectRequest);
            String url = s3.getUrl(awsBucketName, path).toString();
            return url;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void deleteImage(String image) {

        String s3Object = image.substring(image.indexOf("event"));

        if (s3.doesObjectExist(this.awsBucketName, s3Object)){
            s3.deleteObject(this.awsBucketName, s3Object);
        }
        else {
            throw new AmazonS3Exception("Object not exists, cannot be deleted");
        }
    }
    
}
