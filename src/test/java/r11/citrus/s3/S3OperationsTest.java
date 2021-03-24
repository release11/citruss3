package r11.citrus.s3;

import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.testng.TestNGCitrusTestRunner;
import com.consol.citrus.validation.binary.BinaryMessageValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Test(testName = "BasicOperation")
public class S3OperationsTest extends TestNGCitrusTestRunner {
    @Autowired
    private S3Endpoint s3Endpoint;
    @Autowired
    S3AbstractHost s3AbstractHost;

    private final String bucket = "testbucket1";
    private final String key = "log.txt";
    private final String testValue = "testValue";

    @BeforeTest
    void startMockS3() {
        try {
            s3AbstractHost.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterTest
    void stopMockS3() {
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
                .payload(S3Response.CREATE_BUCKET_SUCCESS)
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
                .payload(S3Response.PUT_OBJECT_SUCCESS)
        );
        s3AbstractHost.deleteBucket(bucket);

    }

    @CitrusTest
    public void getObjectsListTest() throws IOException {

        String key1 = "part1/part2/test.txt";
        String key2 = "part1/part2/test2.txt";

        s3AbstractHost.createBucket(bucket);
        s3AbstractHost.createObject(bucket, key1, testValue);
        s3AbstractHost.createObject(bucket, key2, testValue);

        // Get objects list request message from bucket
        S3Message getList = S3Message.builder().bucket(bucket).method(S3RequestType.GET_OBJECTS_LIST).build();

        //Send list request
        send(sendMessageBuilder -> sendMessageBuilder
                .endpoint(s3Endpoint)
                .message(getList));

        List<String> expected = new ArrayList<>();
        expected.add(key2);
        expected.add(key1);
        Collections.sort(expected);
        log.info("Expected list: " + expected.toString());

        // Comparison of received list of objects in bucket with the expected
        receive(receive -> receive
                .endpoint(s3Endpoint)
                .messageName("resultList")
                .validator(new BinaryMessageValidator())
                .payload(expected.toString())
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
                .payload(S3Response.DELETE_OBJECT_SUCCESS)
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
                .payload(S3Response.DELETE_BUCKET_SUCCESS)
        );
        s3AbstractHost.deleteBucket(bucket);
    }

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
                .payload(S3Response.PUT_OBJECT_SUCCESS)
        );
        s3AbstractHost.deleteBucket(bucket);
    }

    @CitrusTest
    public void getFileAndDeleteTest() throws IOException {
        s3AbstractHost.createBucket(bucket);
        s3AbstractHost.createObject(bucket, key, testValue);
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
        s3AbstractHost.deleteBucket(bucket);
    }



}
