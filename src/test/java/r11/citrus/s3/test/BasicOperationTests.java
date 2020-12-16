package r11.citrus.s3.test;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.testng.TestNGCitrusTestRunner;
import com.consol.citrus.validation.binary.BinaryMessageValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.*;
import r11.citrus.s3.S3Endpoint;
import r11.citrus.s3.S3EndpointResponse;
import r11.citrus.s3.S3Message;
import r11.citrus.s3.S3RequestType;
import r11.citrus.s3.host.S3AbstractHost;

import java.io.IOException;

@Test(testName = "BasicOperation")
public class BasicOperationTests extends TestNGCitrusTestRunner {
    @Autowired
    private S3Endpoint s3Endpoint;
    @Autowired
    S3AbstractHost s3AbstractHost;

    private final String bucket = "testbucket1";
    private final String key = "log.txt";
    private final String testValue = "testValue";

    @BeforeTest
    void startMockS3 (){
        try {
            s3AbstractHost.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterTest
    void stopMockS3(){
        try {
            s3AbstractHost.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CitrusTest
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
        s3AbstractHost.deleteBucket(bucket);

    }

    @CitrusTest
    public void getFileTest() throws IOException {
        s3AbstractHost.createBucket(bucket);
        s3AbstractHost.createObject(bucket, key, testValue);
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
        s3AbstractHost.deleteBucket(bucket);

    }

    @CitrusTest
    public void deleteFileTest() throws IOException {
        s3AbstractHost.createBucket(bucket);
        s3AbstractHost.createObject(bucket, key, testValue);
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
