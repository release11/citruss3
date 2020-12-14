package r11.citrus.s3;

import com.consol.citrus.dsl.testng.TestNGCitrusTestRunner;
import io.findify.s3mock.S3Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.*;

import java.io.FileInputStream;
import java.io.IOException;

@Test(priority = 1)
public class TestInit extends TestNGCitrusTestRunner {

    private S3Mock s3Mock;
    @Autowired
    private S3AbstractHost s3AbstractHost;

    @BeforeSuite
    void startMockS3 (){
//        s3Mock = new S3Mock.Builder().withPort(8001).withInMemoryBackend().build();
//        s3Mock.start();
        try {
            s3AbstractHost.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadProperties(){
        try {
            System.getProperties().load(new FileInputStream("src/test/resources/test.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Error loading properies file");
        }
    }


    @AfterSuite
    void stopMockS3(){
        try {
            s3AbstractHost.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        s3Mock.shutdown();
    }
}
