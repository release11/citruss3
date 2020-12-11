package r11.citrus.s3;

import com.consol.citrus.message.DefaultMessage;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.*;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.nio.ByteBuffer;

/**
 * Objects of class are used to create Citrus Message which is fed to either Producer or Consumer.
 * The class uses Jaxb annotations for marshalling of content, which is necessary for object reconstruction.
 */
@XmlRootElement(name = "s3message")
@XmlAccessorType(XmlAccessType.NONE)
public class S3Message extends DefaultMessage {
    @XmlElement
    private S3RequestType method;
    @XmlElement
    private String bucket;
    @XmlElement
    private String key;
    @XmlElement(name = "payload")
    private Object payload;

    /**
     * Constructor that uses S3MessageBuilder object data.
     * @param builder
     */
    public S3Message(S3MessageBuilder builder){
        this.method = builder.getMethod();
        this.bucket = builder.getBucket();
        this.key = builder.getKey();
    }

    /**
     * Constructor that uses all necessary data.
     * @param method
     * @param bucket
     * @param key
     */
    public S3Message(S3RequestType method, String bucket, String key) {
        this.method = method;
        this.bucket = bucket;
        this.key = key;
    }

    /**
     * Constructor that uses xml notation to get all necessary data.
     * Uses S3MessageMarshaller for unmarshalling of the content.
     * Constructor is used to reconstruct S3Message object from DefaultMessage object.
     * @param xml
     * @throws JAXBException
     */
    public S3Message(String xml) throws JAXBException {
        S3MessageMarshaller messageMarshaller = new S3MessageMarshaller();
        S3Message message = messageMarshaller.unmarshal(xml);
        this.method = message.getMethod();
        this.bucket = message.getBucket();
        this.key = message.getKey();
        this.setPayload(message.getData());
    }

    /**
     * Default constructor.
     */
    public S3Message() {
    }

    /**
     * Returns a new instance of S3MessageBuilder.
     * @return
     */
    public static S3MessageBuilder builder(){
        return new S3MessageBuilder();
    }

    /**
     * Returns object content marshalled to xml notation. The method feeds DefaultMessage constructor
     * with payload content that contains all data required to reconstruct S3Message.
     * @return
     */
    @Override
    public Object getPayload() {
        try {
            S3MessageMarshaller messageMarshaller = new S3MessageMarshaller();
            return messageMarshaller.marshal(this);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return super.getPayload();
    }

    /**
     * Sets fields of S3Message as well as DefaultMessage
     * @param payload
     */
    @Override
    public void setPayload(Object payload) {
        this.payload = payload;
        super.setPayload(payload);
    }

    /**
     * Getter for method
     * @return
     */
    public S3RequestType getMethod() {
        return method;
    }

    /**
     * Returns if a bucket should be created when uploading file to S3
     * @return
     */
    public boolean isCreateBucket() {
        return this.method.equals(S3RequestType.PUT_BUCKET_CREATE);
    }

    /**
     * Returns if a file should be deleted as a secondary operation
     * @return
     */
    public boolean isDelete(){
        return this.method.equals(S3RequestType.GET_DELETE);
    }

    /**
     * Getter for method. Returns string.
     * @return
     */
    public String getMethodAsString(){
        return method.toString();
    }

    /**
     * Getter for bucket
     * @return
     */
    public String getBucket() {
        return bucket;
    }

    /**
     * Getter for key
     * @return
     */
    public String getKey() {
        return key;
    }

    /**
     * Getter for payload field
     * @return
     */
    public Object getData(){
        return this.payload;
    }

    /**
     * Returns software.amazon.awssdk.core.sync.RequestBody with payload field data
     * @return
     */
    public RequestBody getRequestBody(){
        return createBody(this.getData());
    }

    /**
     * Based on context of object, constructs S3Request object according to S3Message data.
     * @return
     */
    public S3Request getS3Request(){
        switch(method){
            case PUT:
            case PUT_BUCKET_CREATE:
                return PutObjectRequest.builder().bucket(bucket).key(key).build();
            case GET:
            case GET_DELETE:
                return GetObjectRequest.builder().bucket(bucket).key(key).build();
            case DELETE:
                return DeleteObjectRequest.builder().bucket(bucket).key(key).build();
            case CREATE_BUCKET:
                return CreateBucketRequest.builder().bucket(bucket).build();
            case DELETE_BUCKET:
                return DeleteBucketRequest.builder().bucket(bucket).build();
            case LIST_FILES:
                return ListObjectsV2Request.builder().bucket(bucket).prefix(key).build();
            default:
                return null;
        }
    }

    /**
     * Tries to define what type of Object is citrus message payload and feed data to aws sdk RequestBody accordingly.
     * @param payload
     * @return
     */
    private RequestBody createBody(Object payload){
        if(payload instanceof String)
            return RequestBody.fromString((String)payload);
        if(payload instanceof ByteBuffer)
            return RequestBody.fromByteBuffer((ByteBuffer)payload);
        if(payload instanceof byte[])
            return RequestBody.fromBytes((byte[])payload);
        if(payload instanceof File)
            return RequestBody.fromFile((File)payload);
        return RequestBody.empty();
    }


}
