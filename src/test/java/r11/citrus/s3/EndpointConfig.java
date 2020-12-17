package r11.citrus.s3;

import com.google.common.io.Resources;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Configuration
public class EndpointConfig {
    @Bean
    public Properties testProperties(){
        Properties properties = new Properties();
        try (InputStream is = Resources.asByteSource(Resources.getResource(
                Constants.TEST_PROPERTIES_FILE)).openStream()) {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    @Bean
    public S3AbstractHost s3AbstractHost(Properties testProperties){
        return S3AbstractHost.builder()
                .service(testProperties.getProperty(Constants.SERVICE))
                .region(testProperties.getProperty(Constants.REGION))
                .host(testProperties.getProperty(Constants.URI_OVERRIDE))
                .accessKey(testProperties.getProperty(Constants.ACCESS_KEY))
                .secretKey(testProperties.getProperty(Constants.SECRET_KEY))
                .localBlobPath(testProperties.getProperty(Constants.LOCAL_BLOBSTORE))
                .build();
    }

    @Bean
    public S3Endpoint s3Endpoint(Properties testProperties) {
        return S3Endpoint.builder()
                .endpointUri(testProperties.getProperty(Constants.URI_OVERRIDE))
                .region(testProperties.getProperty(Constants.REGION))
                .accessKey(testProperties.getProperty(Constants.ACCESS_KEY))
                .secretKey(testProperties.getProperty(Constants.SECRET_KEY))
                .build();
    }
}
