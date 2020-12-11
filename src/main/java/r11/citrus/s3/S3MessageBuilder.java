package r11.citrus.s3;

/**
 * Class builds an object of S3Message
 */
public class S3MessageBuilder {
    private S3RequestType method;
    private String bucket;
    private String key;

    /**
     * Default constructor
     */
    public S3MessageBuilder() {
    }

    /**
     * Initializes method field
     * @param method
     * @return
     */
    public S3MessageBuilder method(S3RequestType method){
        this.method = method;
        return this;
    }

    /**
     * Initializes bucket field
     * @param bucket
     * @return
     */
    public S3MessageBuilder bucket(String bucket){
        this.bucket = bucket;
        return this;
    }

    /**
     * Initializes key field
     * @param key
     * @return
     */
    public S3MessageBuilder key(String key){
        this.key = key;
        return this;
    }

    /**
     * Returns S3Message object
     * @return
     */
    public S3Message build(){
        return new S3Message(this);
    }

    /**
     * Getter for method
     * @return
     */
    protected S3RequestType getMethod() {
        return method;
    }

    /**
     * Getter for bucket
     * @return
     */
    protected String getBucket() {
        return bucket;
    }

    /**
     * Getter for key
     * @return
     */
    protected String getKey() {
        return key;
    }
}
