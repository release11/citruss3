package r11.citrus.s3;

import org.gaul.s3proxy.S3Proxy;
import org.gaul.shaded.org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;

import java.io.*;
import java.net.URI;
import java.util.Properties;

public class S3AbstractHost {
    private final BlobStoreContext context;
    private final BlobStore blobStore;
    private final S3Proxy server;

    public S3AbstractHost(S3AbstractHostBuilder builder){
        Properties properties = new Properties();
        properties.setProperty("jclouds.filesystem.basedir", builder.getLocalBlobPath());
        properties.setProperty("jclouds.regions", builder.getRegion());
        this.context = ContextBuilder.newBuilder(builder.getService())
                .credentials(builder.getAccessKey(), builder.getSecretKey())
                .overrides(properties)
                .build(BlobStoreContext.class);
        this.blobStore = context.getBlobStore();
        this.server = S3Proxy.builder()
                .blobStore(this.blobStore)
                .endpoint(URI.create(builder.getHost()))
                .build();
    }

    public void start() throws Exception {
        this.server.start();
        while (!this.server.getState().equals(AbstractLifeCycle.STARTED)) {
            Thread.sleep(1);
        }
    }

    public void stop() throws Exception {
        this.server.stop();
    }

    public static S3AbstractHostBuilder builder(){
        return new S3AbstractHostBuilder();
    }

    public BlobStoreContext getContext() {
        return context;
    }

    public BlobStore getBlobStore() {
        return blobStore;
    }

    public S3Proxy getServer() {
        return server;
    }

    public void createBucket(String bucket){
        this.blobStore.createContainerInLocation(null, bucket);
    }

    public void deleteBucket(String bucket){
        this.blobStore.deleteContainer(bucket);
    }

    public void addObject(String bucket, String key, Object object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(object);
        oos.flush();
        oos.close();
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        Blob blob = this.blobStore.blobBuilder(key).payload(is).build();
        this.blobStore.putBlob(bucket, blob);
    }

    public void removeObject(String bucket, String key){
        this.blobStore.removeBlob(bucket, key);
    }

}
