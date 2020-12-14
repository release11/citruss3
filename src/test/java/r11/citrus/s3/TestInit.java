package r11.citrus.s3;

import com.consol.citrus.dsl.testng.TestNGCitrusTestRunner;
import io.findify.s3mock.S3Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.*;

import java.io.FileInputStream;
import java.io.IOException;

@Test(testName = "Initialization")
public class TestInit extends TestNGCitrusTestRunner {
    @Autowired
    private S3AbstractHost s3AbstractHost;

    @BeforeSuite
    void startMockS3 (){
        try {
            s3AbstractHost.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterSuite
    void stopMockS3(){
        try {
            s3AbstractHost.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
