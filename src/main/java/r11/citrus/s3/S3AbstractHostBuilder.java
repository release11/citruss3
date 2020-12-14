package r11.citrus.s3;

public class S3AbstractHostBuilder {
    private String service = "filesystem";
    private String region = "eu-east-1";
    private String localBlobPath = "/tmp/blobstore";
    private String host = "http://127.0.0.1:4566";
    private String accessKey;
    private String secretKey;

    public String getService() {
        return service;
    }

    public String getRegion() {
        return region;
    }

    public String getLocalBlobPath() {
        return localBlobPath;
    }

    public String getHost() {
        return host;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public S3AbstractHostBuilder service(String service){
        this.service = service;
        return this;
    }

    public S3AbstractHostBuilder region(String region){
        this.region=region;
        return this;
    }

    public S3AbstractHostBuilder localBlobPath(String localBlobPath){
        this.localBlobPath = localBlobPath;
        return this;
    }

    public S3AbstractHostBuilder host(String host){
        this.host = host;
        return this;
    }

    public S3AbstractHostBuilder accessKey(){
        this.accessKey = accessKey;
        return this;
    }

    public S3AbstractHostBuilder secretKey(){
        this.secretKey = secretKey;
        return this;
    }

    public S3AbstractHost build(){
        return new S3AbstractHost(this);
    }
}
