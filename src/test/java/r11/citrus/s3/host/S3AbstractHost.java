package r11.citrus.s3.host;

import org.gaul.s3proxy.S3Proxy;
import org.gaul.shaded.org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;

import java.io.*;
import java.net.URI;
import java.util.Properties;

/**
 * Class manages local S3 simulation. It's responsible for both frontend and backend of service.
 */
public class S3AbstractHost {
    private final BlobStoreContext context;
    private final BlobStore blobStore;
    private final S3Proxy server;

    /**
     * Constructor Uses builder configuration to build an object
     * @param builder
     */
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
                .ignoreUnknownHeaders(true)
                .build();
    }

    /**
     * Starts and publishes restful server
     * @throws Exception
     */
    public void start() throws Exception {
        this.server.start();
        while (!this.server.getState().equals(AbstractLifeCycle.STARTED)) {
            Thread.sleep(1);
        }
    }

    /**
     * Shutdowns server
     * @throws Exception
     */
    public void stop() throws Exception {
        this.server.stop();
    }

    /**
     * Static method for obtaining the builder
     * @return
     */
    public static S3AbstractHostBuilder builder(){
        return new S3AbstractHostBuilder();
    }

    /**
     * context getter
     * @return
     */
    public BlobStoreContext getContext() {
        return context;
    }

    /**
     * blobStore getter
     * @return
     */
    public BlobStore getBlobStore() {
        return blobStore;
    }

    /**
     * server getter
     * @return
     */
    public S3Proxy getServer() {
        return server;
    }

    /**
     * Creates a bucket of a given name. Location equal null indicates use of location
     * (knowns as region, provider etc.) that object has been built with.
     * @param bucket
     */
    public void createBucket(String bucket){
        this.blobStore.createContainerInLocation(null, bucket);
    }

    /**
     * Deletes a bucket of a given name.
     * @param bucket
     */
    public void deleteBucket(String bucket){
        this.blobStore.deleteContainer(bucket);
    }

    /**
     * Deletes all objects in a bucket of a given name.
     * @param bucket
     */
    public void clearBucket(String bucket){
        this.blobStore.clearContainer(bucket);
    }

    /**
     * Creates an object inside a given bucket.
     * @param bucket
     * @param key
     * @param object
     * @throws IOException
     */
    public void createObject(String bucket, String key, Object object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(object);
        oos.flush();
        oos.close();
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        Blob blob = this.blobStore.blobBuilder(key).payload(is).build();
        this.blobStore.putBlob(bucket, blob);
    }

    /**
     * Deletes an object inside a given bucket
     * @param bucket
     * @param key
     */
    public void deleteObject(String bucket, String key){
        this.blobStore.removeBlob(bucket, key);
    }

}
