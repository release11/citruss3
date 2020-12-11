package r11.citrus.s3;

import java.net.URISyntaxException;

/**
 * The class sole purpose is to deliver easy construction method for creating S3Endpoint object.
 */
public class S3EndpointBuilder {
    private String endpoint = null;
    private String region = null;
    private String accessKey = null;
    private String secretKey = null;

    public S3EndpointBuilder() {
    }

    /**
     * Overrides default aws uri. Useful when using localstack
     * @param uri
     * @return
     */
    public S3EndpointBuilder endpointUri(String uri){
        this.endpoint = uri;
        return this;
    }

    /**
     * Feeds s3client information about aws region. Region object is being built with Region.of(String region) method
     * Incorrect region name may throw an exception!
     * @param region
     * @return
     */
    public S3EndpointBuilder region(String region){
        this.region = region;
        return this;
    }

    /**
     * Feeds s3client information about Access Key to build credentials object.
     * @param accessKey
     * @return
     */
    public S3EndpointBuilder accessKey(String accessKey){
        this.accessKey = accessKey;
        return this;
    }

    /**
     * Feeds s3client information about Secret Key to build credentials object.
     * @param secretKey
     * @return
     */
    public S3EndpointBuilder secretKey(String secretKey){
        this.secretKey = secretKey;
        return this;
    }

    /**
     * Creates citrus S3Endpoint
     * @return
     * @throws URISyntaxException
     */
    public S3Endpoint build() {
        S3EndpointConfiguration config = new S3EndpointConfiguration();
        config.setEndpoint(this.endpoint);
        config.setRegion(this.region);
        config.setAccessKey(this.accessKey);
        config.setSecretKey(this.secretKey);
        try {
            return new S3Endpoint(config);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
