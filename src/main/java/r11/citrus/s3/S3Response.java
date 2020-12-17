package r11.citrus.s3;

/**
 * The class is responsible for managing raw response from S3 and its translation to required data type.
 */
public class S3Response {
    public static final String GET_OBJECT_FAILED = "Cannot receive specified object.";
    public static final String DELETE_OBJECT_SUCCESS = "Object has been deleted.";
    public static final String PUT_OBJECT_SUCCESS = "Object has been added to bucket.";
    public static final String CREATE_BUCKET_SUCCESS = "Bucket has been created.";
    public static final String DELETE_BUCKET_SUCCESS = "Bucket has been deleted.";
    private Object response;

    /**
     * Constructs S3EndpointResponse with response from S3. In case of get request returns object,
     * otherwise returns confirmation.
     * @param response
     */
    public S3Response(Object response) {
        this.response = response;
    }

    /**
     * Translates default response to String.
     * @return
     */
    public String asString(){
        if(response instanceof String)
            return (String)response;
        if(response instanceof byte[])
            return new String((byte[])response);
        return null;
    }

}
