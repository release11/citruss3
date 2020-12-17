package r11.citrus.s3;

import com.consol.citrus.endpoint.AbstractEndpoint;
import com.consol.citrus.messaging.Consumer;
import com.consol.citrus.messaging.Producer;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;
import java.net.URISyntaxException;


/**
 * The class creates an object that is being used by CitrusTest case.
 */
public class S3Endpoint extends AbstractEndpoint {

    private String name = getClass().getSimpleName();
    private S3Client client;
    private S3Response response;
    private S3Message forwardedMessage;


    /**
     * Returns received response then clear the field.
     *
     * @return
     */
    public S3Response getResponse() {
        S3Response resp = this.response;
        this.response = null;
        return resp;
    }

    /**
     * Returns forwarded S3Message and deletes the reference.
     * @return
     */
    public S3Message retrieveMessage() {
        S3Message m = this.forwardedMessage;
        this.forwardedMessage = null;
        return m;
    }

    /**
     * Returns boolean if forwarded S3Message exists
     * @return
     */
    public boolean hasMessage() {
        return forwardedMessage != null;
    }

    /**
     * Returns boolean if s3 server response exists
     * @return
     */
    public boolean hasResponse() {
        return response != null;
    }

    /**
     * setter for forwardedMessage
     * @param forwardedMessage
     */
    public void setForwardedMessage(S3Message forwardedMessage) {
        this.forwardedMessage = forwardedMessage;
    }

    /**
     * @param response
     */
    public void setResponse(S3Response response) {
        this.response = response;
    }

    /**
     * @return
     */
    public S3Client getClient() {
        return client;
    }

    /**
     * Default constructor using endpoint configuration.
     *
     * @param endpointConfiguration
     */
    public S3Endpoint(S3EndpointConfiguration endpointConfiguration) throws URISyntaxException {
        super(endpointConfiguration);
        if (getEndpointConfiguration().isEndpointOverride()) {
            client = S3Client.builder()
                    .endpointOverride(new URI(this.getEndpointConfiguration().getEndpoint()))
                    .region(Region.of(this.getEndpointConfiguration().getRegion()))
                    .credentialsProvider(StaticCredentialsProvider.create(buildAwsSessionCredentials()))
                    .build();
        } else {
            client = S3Client.builder()
                    .region(Region.of(this.getEndpointConfiguration().getRegion()))
                    .credentialsProvider(StaticCredentialsProvider.create(buildAwsSessionCredentials()))
                    .build();
        }
    }

    /**
     * Builds credential object necessary to access s3 service.
     *
     * @return
     */
    private AwsSessionCredentials buildAwsSessionCredentials() {
        return AwsSessionCredentials.create(
                this.getEndpointConfiguration().getAccessKey(),
                this.getEndpointConfiguration().getSecretKey(),
                "");
    }

    /**
     * When asked by Citrus TestContext it returns Producer, which is responsible for sending a message.
     *
     * @return
     */
    public Producer createProducer() {
        return new S3Producer(this);
    }

    /**
     * When asked by Citrus TestContext it  returns Consumer, which is responsible for receiving response.
     *
     * @return
     */
    public Consumer createConsumer() {
        return new S3Consumer(this);
    }

    /**
     * @return
     */
    @Override
    public S3EndpointConfiguration getEndpointConfiguration() {
        return (S3EndpointConfiguration) super.getEndpointConfiguration();
    }

    /**
     * Returns the producer name
     *
     * @return
     */
    public String getProducerName() {
        return name + ":producer";
    }

    /**
     * Returns the consumer name
     *
     * @return
     */
    public String getConsumerName() {
        return name + ":consumer";
    }

    /**
     * Returns object building this object.
     *
     * @return
     */
    public static S3Endpoint.Builder builder() {
        return new S3Endpoint.Builder();
    }

    /**
     * The class is responsible for containing all required data to create S3Endpoint object.
     */
    static class Builder{
        private String endpoint = null;
        private String region = null;
        private String accessKey = null;
        private String secretKey = null;

        /**
         * Overrides default aws uri. Useful when using localstack
         * @param uri
         * @return
         */
        public S3Endpoint.Builder endpointUri(String uri){
            this.endpoint = uri;
            return this;
        }

        /**
         * Feeds s3client information about aws region. Region object is being built with Region.of(String region) method
         * Incorrect region name may throw an exception!
         * @param region
         * @return
         */
        public S3Endpoint.Builder region(String region){
            this.region = region;
            return this;
        }

        /**
         * Feeds s3client information about Access Key to build credentials object.
         * @param accessKey
         * @return
         */
        public S3Endpoint.Builder accessKey(String accessKey){
            this.accessKey = accessKey;
            return this;
        }

        /**
         * Feeds s3client information about Secret Key to build credentials object.
         * @param secretKey
         * @return
         */
        public S3Endpoint.Builder secretKey(String secretKey){
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

}
