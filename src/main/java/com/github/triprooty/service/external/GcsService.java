package com.github.triprooty.service.external;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GcsService {
    private final Storage storage;
    @Value("${gcp.bucket}") private String bucket;

    public String uploadPublic(String objectName, String contentType, byte[] bytes) {
        BlobId blobId = BlobId.of(bucket, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(contentType)
                .setCacheControl("public, max-age=31536000, immutable")
                .build();
        storage.create(blobInfo, bytes);
        return "https://storage.googleapis.com/" + bucket + "/" + objectName;
    }

    public void copy(String src, String dest) {
        storage.copy(Storage.CopyRequest.of(BlobId.of(bucket, src), BlobId.of(bucket, dest)));
    }

    public void delete(String objectName) {
        storage.delete(BlobId.of(bucket, objectName));
    }

    public String toObjectName(String url) {
        return url.replace("https://storage.googleapis.com/" + bucket + "/", "");
    }
    public String getBucketName() {
        return bucket;
    }

    public String publicUrlOf(String objectName) {
        return "https://storage.googleapis.com/" + bucket + "/" + objectName;
    }
}
