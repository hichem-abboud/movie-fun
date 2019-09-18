package org.superbiz.moviefun;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.superbiz.moviefun.blobstore.Blob;
import org.superbiz.moviefun.blobstore.BlobStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@Component
public class S3Store implements BlobStore {
    public S3Store() {
    }

    private AmazonS3Client s3Client;
    private String photoStorageBucket;


    public S3Store(AmazonS3Client s3Client, String photoStorageBucket) {
        this.s3Client = s3Client;
        this.photoStorageBucket = photoStorageBucket;
    }

    @Override
    public void put(Blob blob) throws IOException {
        List<Bucket> buckets =  this.s3Client.listBuckets();
        Bucket bucket = buckets.get(0);
        this.s3Client.putObject(
                bucket.getName(),
                "covers/" + blob.name,
                blob.inputStream,null);

    }

    @Override
    public Optional<Blob> get(String name) throws IOException, URISyntaxException {
        List<Bucket> buckets =  this.s3Client.listBuckets();
        Bucket bucket = buckets.get(0);
        S3Object s3object = this.s3Client.getObject(bucket.getName(),"covers/"+name);
        S3ObjectInputStream inputStream = s3object.getObjectContent();


        Optional<Blob> blob = Optional.ofNullable(new Blob(name,inputStream,null));

        return blob;
    }

    @Override
    public void deleteAll() {

    }
}
