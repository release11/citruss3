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
    private S3EndpointResponse response;
    //    private S3Request request;
    private S3Message forwardedMessage;


    /**
     * Returns received response then clear the field.
     *
     * @return
     */
    public S3EndpointResponse getResponse() {
        S3EndpointResponse resp = this.response;
        this.response = null;
        return resp;
    }

    public S3Message retrieveMessage() {
        S3Message m = this.forwardedMessage;
        this.forwardedMessage = null;
        return m;
    }

    public boolean hasMessage() {
        return forwardedMessage != null;
    }

    public boolean hasResponse() {
        return response != null;
    }

    public void setForwardedMessage(S3Message forwardedMessage) {
        this.forwardedMessage = forwardedMessage;
    }

    //    public boolean hasRequest() {
//        return request != null;
//    }

//    public S3Request getRequest() {
//        S3Request req = request;
//        this.request = null;
//        return req;
//    }
//    public void setRequest(S3Request request) {
//        this.request = request;
//    }

    /**
     * @param response
     */
    public void setResponse(S3EndpointResponse response) {
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
    public static S3EndpointBuilder builder() {
        return new S3EndpointBuilder();
    }

}
