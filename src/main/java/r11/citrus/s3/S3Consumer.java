package r11.citrus.s3;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.message.DefaultMessage;
import com.consol.citrus.message.Message;
import com.consol.citrus.messaging.Consumer;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The class responsibility is to retrieve response from endpoint object and create Message endpoint based on it.
 * Created object is fed to citrus receive() method.
 */
public class S3Consumer implements Consumer {
    private final S3Endpoint s3Endpoint;

    /**
     * Consumer is created by endpoint object and is initialized by it. All necessary data is contained within
     * the given object.
     *
     * @param s3Endpoint
     */
    public S3Consumer(S3Endpoint s3Endpoint) {
        this.s3Endpoint = s3Endpoint;

    }

    /**
     * Builds message based on data from endpoint object and returns it to citrus TestContext
     * in citrus receive() method.
     *
     * @param context
     * @return
     */
    @Override
    public Message receive(TestContext context) {
        Message result = new DefaultMessage();

        if (s3Endpoint.hasResponse()) {
            String response = s3Endpoint.getResponse().asString();
            result.setPayload(response);
        }

        if (s3Endpoint.hasMessage()) {
            S3Message m = s3Endpoint.retrieveMessage();
            if (m.getS3Request() instanceof GetObjectRequest) {
                ResponseBytes res = s3Endpoint.getClient().getObjectAsBytes((GetObjectRequest) m.getS3Request());
                try {
                    result.setPayload(responseBytesToObject(res));
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (m.isDelete()) {
                    s3Endpoint.getClient().deleteObject(DeleteObjectRequest.builder().bucket(m.getBucket()).key(m.getKey()).build());
                }
            } else if (m.getS3Request() instanceof ListObjectsV2Request) {
                ListObjectsV2Response res = s3Endpoint.getClient().listObjectsV2((ListObjectsV2Request) m.getS3Request());
                result.setPayload(res.contents().stream().map(S3Object::key).collect(Collectors.toList()));
            }

        }



        return result;
    }

    public static Object responseBytesToObject(ResponseBytes responseBytes) throws IOException, ClassNotFoundException {
        byte[] bytes = responseBytes.asByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }

    /**
     * Checks value of "method" parameter and based on it chooses request type. After response its received
     * a class responsible for managing responses is created with raw response object and returned.
     *
     * @param s3Client
     * @param s3Message
     * @return
     */
    public S3EndpointResponse performRequest(S3Client s3Client, S3Message s3Message) {
        Object response = null;
        S3Request request = s3Message.getS3Request();
        RequestBody body = s3Message.getRequestBody();
        if (request instanceof PutObjectRequest) {
            s3Client.putObject((PutObjectRequest) request, body);
            response = S3EndpointResponse.PUT_OBJECT_SUCCESS;
        }
        if (request instanceof GetObjectRequest) {
            response = s3Client.getObjectAsBytes((GetObjectRequest) request).asByteArray();
        }
        return new S3EndpointResponse(response);
    }

    /**
     * Method is being called by TestContext by default. Due to lack of introduction of timeout in s3 endpoint,
     * the method without timeout parameter is being called.
     *
     * @param context
     * @param timeout
     * @return
     */
    @Override
    public Message receive(TestContext context, long timeout) {
        return receive(context);


    }

    /**
     * Returns the name of the Consumer
     *
     * @return
     */
    @Override
    public String getName() {
        return this.getClass().toString();
    }
}
