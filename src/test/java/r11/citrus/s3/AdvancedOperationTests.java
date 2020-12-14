package r11.citrus.s3;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.testng.TestNGCitrusTestRunner;
import com.consol.citrus.validation.binary.BinaryMessageValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

@Test(testName = "AdvancedOperation")
public class AdvancedOperationTests extends TestNGCitrusTestRunner {
    @Autowired
    private S3Endpoint s3Endpoint;
    @Autowired
    private S3AbstractHost s3AbstractHost;

    private final String bucket = "testbucket1";
    private final String key = "log.txt";
    private final String testValue = "testValue";

    @CitrusTest
    public void createBucketAndUploadTest() {
        //Put request message
        S3Message m1 = S3Message.builder().bucket(bucket).key(key).method(S3RequestType.PUT_BUCKET_CREATE).build();

        //Upload file to S3
        send(sendMessageBuilder -> sendMessageBuilder
                .endpoint(s3Endpoint)
                .message(m1)
                .payload(testValue)
        );
        //Confirm file upload successful
        receive(receive -> receive
                .endpoint(s3Endpoint)
                .payload(S3EndpointResponse.PUT_OBJECT_SUCCESS)
        );
//        s3AbstractHost.deleteObject(bucket, key);
        s3AbstractHost.deleteBucket(bucket);
    }

    @CitrusTest
    public void getFileAndDeleteTest() {
        s3AbstractHost.createBucket(bucket);
        //Get request message
        S3Message m2 = S3Message.builder().bucket(bucket).key(key).method(S3RequestType.GET_DELETE).build();

        //Send S3 file request
        send(sendMessageBuilder -> sendMessageBuilder
                .endpoint(s3Endpoint)
                .message(m2)
        );
        //Receive previously requested file from S3
        receive(receive -> receive
                .endpoint(s3Endpoint)
                .validator(new BinaryMessageValidator())
                .payload(testValue)
        );
//        s3AbstractHost.deleteObject(bucket, key);
        s3AbstractHost.deleteBucket(bucket);
    }

    @CitrusTest
    public void deleteBucketTest() {
        s3AbstractHost.createBucket(bucket);
        //Get request message
        S3Message m2 = S3Message.builder().bucket(bucket).method(S3RequestType.DELETE_BUCKET).build();

        //Send S3 file request
        send(sendMessageBuilder -> sendMessageBuilder
                .endpoint(s3Endpoint)
                .message(m2)
        );
        //Receive previously requested file from S3
        receive(receive -> receive
                .endpoint(s3Endpoint)
                .payload(S3EndpointResponse.DELETE_BUCKET_SUCCESS)
        );
        s3AbstractHost.deleteBucket(bucket);

    }
}
