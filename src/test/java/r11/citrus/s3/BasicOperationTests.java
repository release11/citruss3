package r11.citrus.s3;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.testng.TestNGCitrusTestRunner;
import com.consol.citrus.validation.binary.BinaryMessageValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.io.IOException;

@Test(priority = 10, testName = "BasicTest")
public class BasicOperationTests extends TestNGCitrusTestRunner {
    @Autowired
    private S3Endpoint s3Endpoint;
    @Autowired S3AbstractHost s3AbstractHost;

    private final String bucket = "testbucket1";
    private final String key = "log.txt";
    private final String testValue = "testValue";

    @CitrusTest
    @Test(priority = 11)
    public void createBucketTest() {
        //Put request message
        S3Message m1 = S3Message.builder().bucket(bucket).method(S3RequestType.CREATE_BUCKET).build();

        //Upload file to S3
        send(sendMessageBuilder -> sendMessageBuilder
                .endpoint(s3Endpoint)
                .message(m1)
                .payload(testValue)
        );
        //Confirm file upload successful
        receive(receive -> receive
                .endpoint(s3Endpoint)
                .payload(S3EndpointResponse.CREATE_BUCKET_SUCCESS)
        );
        s3AbstractHost.deleteBucket(bucket);

    }


    @CitrusTest
    @Test(priority = 12)
    public void putFileTest() {
        s3AbstractHost.createBucket(bucket);
        //Put request message
        S3Message m1 = S3Message.builder().bucket(bucket).key(key).method(S3RequestType.PUT).build();

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
        s3AbstractHost.removeObject(bucket, key);
        s3AbstractHost.deleteBucket(bucket);

    }

    @CitrusTest
    @Test(priority = 13)
    public void getFileTest() throws IOException {
        s3AbstractHost.createBucket(bucket);
        s3AbstractHost.addObject(bucket, key, "Hello world!");
        //Get request message
        S3Message m2 = S3Message.builder().bucket(bucket).key(key).method(S3RequestType.GET).build();

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
        s3AbstractHost.removeObject(bucket, key);
        s3AbstractHost.deleteBucket(bucket);

    }

    @CitrusTest
    @Test(priority = 14)
    public void deleteFileTest() throws IOException {
        s3AbstractHost.createBucket(bucket);
        s3AbstractHost.addObject(bucket, key, "Hello world!");
        //Delete object request
        S3Message m3 = S3Message.builder().bucket(bucket).key(key).method(S3RequestType.DELETE).build();

        //Send delete object request
        send(sendMessageBuilder -> sendMessageBuilder
                .endpoint(s3Endpoint)
                .message(m3)
        );
        //Confirm file delete successful
        receive(receive -> receive
                .endpoint(s3Endpoint)
                .payload(S3EndpointResponse.DELETE_OBJECT_SUCCESS)
        );
        s3AbstractHost.removeObject(bucket, key);
        s3AbstractHost.deleteBucket(bucket);

    }

    @CitrusTest
    @Test(priority = 15)
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

    }

}