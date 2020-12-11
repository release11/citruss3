package r11.citrus.s3;

import com.consol.citrus.endpoint.AbstractEndpointConfiguration;

/**
 * The class is responsible for containing all required data to create S3Endpoint object.
 */
public class S3EndpointConfiguration extends AbstractEndpointConfiguration {
    private String endpoint = null;
    private String region;
    private String accessKey;
    private String secretKey;

    public S3EndpointConfiguration() {
    }

    /**
     * Returns information if endpoint is overriden. Example: localstack service.
     * @return
     */
    public boolean isEndpointOverride(){
        return endpoint != null;
    }

    /**
     * Getter for endpoint field
     * @return
     */
    public String getEndpoint() {
        return endpoint;
    }

    /**
     * Setter for endpoint field
     * @param endpoint
     */
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * Getter for region
     * @return
     */
    public String getRegion() {
        return region;
    }

    /**
     * Setter for region
     * @param region
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * Getter for accessKey
     * @return
     */
    public String getAccessKey() {
        return accessKey;
    }

    /**
     * Setter for accessKey
     * @param accessKey
     */
    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    /**
     * Getter for secretKey
     * @return
     */
    public String getSecretKey() {
        return secretKey;
    }

    /**
     * Setter for secretKey
     * @param secretKey
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

}
