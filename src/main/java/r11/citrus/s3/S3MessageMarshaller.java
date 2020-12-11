package r11.citrus.s3;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Class uses Jaxb annotations in S3Message to perfom marshal and unmarshal operations
 */
public class S3MessageMarshaller {
    private JAXBContext context = JAXBContext.newInstance(S3Message.class);

    /**
     * Default constructor
     * @throws JAXBException
     */
    public S3MessageMarshaller() throws JAXBException {
    }

    /**
     * Marshals S3Message to xml notation
     * @param message
     * @return
     * @throws JAXBException
     */
    public String marshal(S3Message message) throws JAXBException {
        StringWriter stringWriter = new StringWriter();
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(message, stringWriter);
        return stringWriter.toString();
    }

    /**
     * Unmarshals xml notation to S3Message object
     * @param xml
     * @return
     * @throws JAXBException
     */
    public S3Message unmarshal(String xml) throws JAXBException {
        StringReader stringReader = new StringReader(xml);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (S3Message)unmarshaller.unmarshal(stringReader);
    }
}
