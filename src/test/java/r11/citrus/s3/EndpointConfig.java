package r11.citrus.s3;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EndpointConfig {
    @Bean
    public S3AbstractHost s3AbstractHost(){
        return S3AbstractHost.builder()
                .service("filesystem")
                .region("eu-north-1")
                .host("http://localhost:8001")
                .accessKey("1234")
                .secretKey("1234")
                .localBlobPath("./s3-tmp/blobstore")
                .build();
    }

    @Bean
    public S3Endpoint s3Endpoint() {
        return S3Endpoint.builder()
                .endpointUri("http://localhost:8001")
                .region("eu-north-1")
                .accessKey("1234")
                .secretKey("1234")
                .build();
    }
}
