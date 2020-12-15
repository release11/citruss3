package r11.citrus.s3;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.message.Message;
import com.consol.citrus.messaging.Producer;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The class is being created by S3Endpoint which is given as an endpoint() method argument to citrus test.
 * It's responsible for sending a request to the server and receiving response.
 * Response is being stored in the endpoint object from where it can be accessed by receive() citrus request.
 */
public class S3Producer implements Producer {
    private final S3Endpoint s3Endpoint;

    /**
     * Producer is created by endpoint object and is initialized by it. All necessary data is contained within
     * the given object.
     *
     * @param s3Endpoint
     */
    public S3Producer(S3Endpoint s3Endpoint) {
        this.s3Endpoint = s3Endpoint;
    }

    /**
     * Method receives message built in citrus test message builder and according to it's parameters,
     * it builds proper aws s3 request. After request is sent and response received, it feeds the response
     * to endpoint object.
     *
     * @param message
     * @param context
     */
    public void send(Message message, TestContext context) {
        String xmlPayload = (String) message.getPayload();
        try {
            S3Message s3Message = new S3Message(xmlPayload);
            S3EndpointResponse response = performRequest(s3Endpoint.getClient(), s3Message);

            if (response != null) {
                s3Endpoint.setResponse(response);
            }

        } catch (JAXBException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks value of S3 operation parameter and chooses request type.
     * After response is received, a response handler class with raw response object is returned.
     *
     * @param s3Client
     * @param s3Message
     * @return
     */
    public S3EndpointResponse performRequest(S3Client s3Client, S3Message s3Message) {
        Object response;
        S3Request request = s3Message.getS3Request();
        RequestBody body = s3Message.getRequestBody();

        if (request instanceof PutObjectRequest) {
            PutObjectRequest req = (PutObjectRequest) request; //Cast request to proper object
            if (!listBucketsAsString().contains(req.bucket()) && s3Message.isCreateBucket()) { //Check if we should create a bucket
                s3Client.createBucket(CreateBucketRequest.builder().bucket(req.bucket()).build()); //Create bucket if needed
            }
            s3Client.putObject(req, body); //put object to S3
            response = S3EndpointResponse.PUT_OBJECT_SUCCESS; //write mock response

        } else if (request instanceof DeleteObjectRequest) {
            s3Client.deleteObject((DeleteObjectRequest) request);
            response = S3EndpointResponse.DELETE_OBJECT_SUCCESS;

        } else if (request instanceof DeleteBucketRequest) {
            s3Client.deleteBucket((DeleteBucketRequest) request);
            response = S3EndpointResponse.DELETE_BUCKET_SUCCESS;

        } else if (request instanceof CreateBucketRequest) {
            CreateBucketRequest req = (CreateBucketRequest) request;
            if (!listBucketsAsString().contains(req.bucket())) { //Check if we should create a bucket
                s3Client.createBucket(CreateBucketRequest.builder().bucket(req.bucket()).build()); //Create bucket if needed
            }
            response = S3EndpointResponse.CREATE_BUCKET_SUCCESS; //write mock response

        } else {
            s3Endpoint.setForwardedMessage(s3Message);
//            ResponseBytes res = s3Client.getObjectAsBytes((GetObjectRequest) request);
            response = null;
        }
        return new S3EndpointResponse(response);
    }

    private List<String> listBucketsAsString() {
        return listBuckets().stream().map(Bucket::name).collect(Collectors.toList());
    }

    private List<Bucket> listBuckets() {
        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
        ListBucketsResponse resp = s3Endpoint.getClient().listBuckets(listBucketsRequest);
        return resp.buckets();
    }

    /**
     * Returnes the name of the producer
     *
     * @return
     */
    public String getName() {
        return s3Endpoint.getProducerName();
    }

    /**
     * Reads and returns header value, then deletes the header.
     *
     * @param message
     * @param headerName
     * @return
     */
    public static String getHeaderData(Message message, String headerName) {
        String headerValue = (String) message.getHeader(headerName);
        message.removeHeader(headerName);
        return headerValue;
    }


}
