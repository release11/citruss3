__Hello there!__
It's just simple md file

Here is some code:

```java

package r11.citrus.s3;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.testng.TestNGCitrusTestRunner;
import com.consol.citrus.message.DefaultMessage;
import com.consol.citrus.validation.xml.DomXmlMessageValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Test
public class S3CitrusTest extends TestNGCitrusTestRunner {
    @Autowired
    private S3Endpoint s3Endpoint;


    @CitrusTest
    public void libTest(){
        String testMessage = "Hello citrus!";
        String bucket = "testbucket";
        String key = "testobject";

        sequential().actions(
                send(builder -> builder
                        .endpoint(s3Endpoint)
                        .message(S3Message.builder().method(S3RequestType.PUT).bucket(bucket).key(key).build())
                        .payload(testMessage)
                        .build()
                ),
                receive(builder -> builder
                        .endpoint(s3Endpoint)
                        .payload(S3EndpointResponse.PUT_OBJECT_SUCCESS)
                )
        );

        sequential().actions(
                send(builder -> builder
                        .endpoint(s3Endpoint)
                        .message(S3Message.builder().method(S3RequestType.GET).bucket(bucket).key(key).build())
                        .build()
                ),
                receive(builder -> builder
                        .endpoint(s3Endpoint)
                        .payload(testMessage)
                )
        );

    }

}

```