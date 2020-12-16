package r11.citrus.s3.host;

/**
 * The instance of this class serves as builder to S3AbstractHost. It gathers all required
 * data to initialize an instance of the class. Operation build returns an object.
 */
public class S3AbstractHostBuilder {
    /** initialized with default value */
    private String service = "filesystem";
    /** initialized with default value */
    private String region = "eu-east-1";
    /** initialized with default value */
    private String localBlobPath = "./tmp/blobstore";
    /** initialized with default value */
    private String host = "http://127.0.0.1:4566";
    private String accessKey;
    private String secretKey;

    /**
     * service getter
     * @return
     */
    public String getService() {
        return service;
    }

    /**
     * region getter
     * @return
     */
    public String getRegion() {
        return region;
    }

    /**
     * localBlobPath getter
     * @return
     */
    public String getLocalBlobPath() {
        return localBlobPath;
    }

    /**
     * host getter
     * @return
     */
    public String getHost() {
        return host;
    }

    /**
     * accessKey getter
     * @return
     */
    public String getAccessKey() {
        return accessKey;
    }

    /**
     * secretKey getter
     * @return
     */
    public String getSecretKey() {
        return secretKey;
    }

    /**
     * Sets value during building
     * @param service
     * @return
     */
    public S3AbstractHostBuilder service(String service){
        this.service = service;
        return this;
    }

    /**
     * Sets value during building
     * @param region
     * @return
     */
    public S3AbstractHostBuilder region(String region){
        this.region=region;
        return this;
    }

    /**
     * Sets value during building
     * @param localBlobPath
     * @return
     */
    public S3AbstractHostBuilder localBlobPath(String localBlobPath){
        this.localBlobPath = localBlobPath;
        return this;
    }

    /**
     * Sets value during building
     * @param host
     * @return
     */
    public S3AbstractHostBuilder host(String host){
        this.host = host;
        return this;
    }

    /**
     * Sets value during building
     * @param accessKey
     * @return
     */
    public S3AbstractHostBuilder accessKey(String accessKey){
        this.accessKey = accessKey;
        return this;
    }

    /**
     * Sets value during building
     * @param secretKey
     * @return
     */
    public S3AbstractHostBuilder secretKey(String secretKey){
        this.secretKey = secretKey;
        return this;
    }

    /**
     * Creates S3AbstractHost instance based on provided data.
     * @return
     */
    public S3AbstractHost build(){
        return new S3AbstractHost(this);
    }
}
