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
    public S3AbstractHost(S3AbstractHost.Builder builder){
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
    public static S3AbstractHost.Builder builder(){
        return new S3AbstractHost.Builder();
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

    /**
     * The instance of this class serves as builder to S3AbstractHost. It gathers all required
     * data to initialize an instance of the class. Operation build returns an object.
     */
    static class Builder {

        /**
         * initialized with default value
         */
        private String service = "filesystem";
        /**
         * initialized with default value
         */
        private String region = "eu-east-1";
        /**
         * initialized with default value
         */
        private String localBlobPath = "./tmp/blobstore";
        /**
         * initialized with default value
         */
        private String host = "http://127.0.0.1:4566";
        private String accessKey;
        private String secretKey;

        /**
         * service getter
         *
         * @return
         */
        public String getService() {
            return service;
        }

        /**
         * region getter
         *
         * @return
         */
        public String getRegion() {
            return region;
        }

        /**
         * localBlobPath getter
         *
         * @return
         */
        public String getLocalBlobPath() {
            return localBlobPath;
        }

        /**
         * host getter
         *
         * @return
         */
        public String getHost() {
            return host;
        }

        /**
         * accessKey getter
         *
         * @return
         */
        public String getAccessKey() {
            return accessKey;
        }

        /**
         * secretKey getter
         *
         * @return
         */
        public String getSecretKey() {
            return secretKey;
        }

        /**
         * Sets value during building
         *
         * @param service
         * @return
         */
        public S3AbstractHost.Builder service(String service) {
            this.service = service;
            return this;
        }

        /**
         * Sets value during building
         *
         * @param region
         * @return
         */
        public S3AbstractHost.Builder region(String region) {
            this.region = region;
            return this;
        }

        /**
         * Sets value during building
         *
         * @param localBlobPath
         * @return
         */
        public S3AbstractHost.Builder localBlobPath(String localBlobPath) {
            this.localBlobPath = localBlobPath;
            return this;
        }

        /**
         * Sets value during building
         *
         * @param host
         * @return
         */
        public S3AbstractHost.Builder host(String host) {
            this.host = host;
            return this;
        }

        /**
         * Sets value during building
         *
         * @param accessKey
         * @return
         */
        public S3AbstractHost.Builder accessKey(String accessKey) {
            this.accessKey = accessKey;
            return this;
        }

        /**
         * Sets value during building
         *
         * @param secretKey
         * @return
         */
        public S3AbstractHost.Builder secretKey(String secretKey) {
            this.secretKey = secretKey;
            return this;
        }

        /**
         * Creates S3AbstractHost instance based on provided data.
         *
         * @return
         */
        public S3AbstractHost build() {
            return new S3AbstractHost(this);
        }
    }
}
