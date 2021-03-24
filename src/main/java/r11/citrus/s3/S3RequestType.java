package r11.citrus.s3;

/**
 * Defines the list of possible request types.
 */
public enum S3RequestType {
    PUT,
    PUT_BUCKET_CREATE,
    GET,
    DELETE,
    GET_DELETE,
    CREATE_BUCKET,
    DELETE_BUCKET,
    GET_OBJECTS_LIST;

    public static final String REQUEST_PUT = "put";
    public static final String REQUEST_PUT_BUCKET_CREATE = "put_bucket_create";
    public static final String REQUEST_GET = "get";
    public static final String REQUEST_DELETE = "delete";
    public static final String REQUEST_GET_DELETE = "get_delete";
    public static final String REQUEST_CREATE_BUCKET = "create_bucket";
    public static final String REQUEST_DELETE_BUCKET = "delete_bucket";
    public static final String REQUEST_GET_OBJECTS_LIST = "get_objects_list";

    /**
     * Translates string input to one of it's enum values.
     * @param type
     * @return
     */
    public static S3RequestType findMethod(String type){
        String input = type.replaceAll("\\s+","").toLowerCase();
        switch(input){
            case REQUEST_GET:
                return S3RequestType.GET;
            case REQUEST_PUT:
                return S3RequestType.PUT;
            case REQUEST_PUT_BUCKET_CREATE:
                return S3RequestType.PUT_BUCKET_CREATE;
            case REQUEST_DELETE:
                return S3RequestType.DELETE;
            case REQUEST_GET_DELETE:
                return S3RequestType.GET_DELETE;
            case REQUEST_DELETE_BUCKET:
                return S3RequestType.DELETE_BUCKET;
            case REQUEST_CREATE_BUCKET:
                return S3RequestType.CREATE_BUCKET;
            case REQUEST_GET_OBJECTS_LIST:
                return S3RequestType.GET_OBJECTS_LIST;
            default:
                return null;
        }

    }
}
