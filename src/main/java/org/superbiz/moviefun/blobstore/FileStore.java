package org.superbiz.moviefun.blobstore;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;
import static java.lang.String.format;
import static java.nio.file.Files.readAllBytes;

@Component
public class FileStore implements BlobStore {

    @Override
    public void put(Blob blob) throws IOException {

        File targetFile = new File("covers/" + blob.name);
        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        byte[] buf = new byte[1024];
        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
           int numRead = 00;
           while((numRead = blob.inputStream.read(buf)) >= 0) {
               outputStream.write(buf, 0, numRead);
           }
        }



    }

    @Override
    public Optional<Blob> get(String name) throws URISyntaxException,IOException {
        File coverFile = getCoverFile(name);
        Path coverFilePath;

        if (coverFile.exists()) {
            coverFilePath = coverFile.toPath();
        } else {
            coverFilePath = Paths.get(getSystemResource("default-cover.jpg").toURI());
        }

        Optional<Blob> blob = Optional.ofNullable(new Blob(name,new FileInputStream(coverFilePath.toFile()),null));

        return blob;

    }

    private File getCoverFile(String albumId) {
        String coverFileName = format("covers/%d", albumId);
        return new File(coverFileName);
    }

    @Override
    public void deleteAll() {
        // ...
    }


}
