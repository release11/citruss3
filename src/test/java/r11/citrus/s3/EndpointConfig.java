package r11.citrus.s3;

import com.consol.citrus.dsl.endpoint.CitrusEndpoints;
import com.consol.citrus.endpoint.Endpoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.net.URISyntaxException;

@Configuration
public class EndpointConfig {

    @Bean
    public S3Endpoint s3Endpoint() throws URISyntaxException {
        return S3Endpoint.builder()
                .endpointUri("http://localhost:8001")
                .region("us-east-1")
                .accessKey("1234")
                .secretKey("1234")
                .build();
    }
}
