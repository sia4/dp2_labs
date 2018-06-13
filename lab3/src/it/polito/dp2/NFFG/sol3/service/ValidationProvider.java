package it.polito.dp2.NFFG.sol3.service;

import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

@Provider
@Consumes({"application/xml","text/xml"})
public class ValidationProvider<T> implements MessageBodyReader<T> {
	
	final String jaxbPackage = "it.polito.dp2.NFFG.sol3.service.generated.model";
	Unmarshaller unmarshaller;
	Logger logger;
	String responseBodyTemplate;

	public ValidationProvider() {
		logger = Logger.getLogger(ValidationProvider.class.getName());

		try {				
			InputStream schemaStream = ValidationProvider.class.getResourceAsStream("/xsd/nffgVerifier.xsd");
			if (schemaStream == null) {
				logger.log(Level.SEVERE, "xml schema file Not found.");
				throw new IOException();
			}
            JAXBContext jc = JAXBContext.newInstance( jaxbPackage );
            unmarshaller = jc.createUnmarshaller();
            SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(new StreamSource(schemaStream));
            unmarshaller.setSchema(schema);
            
			InputStream templateStream = ValidationProvider.class.getResourceAsStream("/html/BadRequestBodyTemplate.html");
			if (templateStream == null) {
				logger.log(Level.SEVERE, "html template file Not found.");
				throw new IOException();
			}
	        BufferedReader reader = new BufferedReader(new InputStreamReader(templateStream));
	        StringBuilder out = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            out.append(line);
	        }
			responseBodyTemplate = out.toString();

            logger.log(Level.INFO, "NffgService initialized successfully");
		} catch (SAXException | JAXBException | IOException se) {
			logger.log(Level.SEVERE, "Error parsing xml directory file. Service will not work properly.", se);
		}
	}
	
	@Override
	public boolean isReadable(Class<?> type, Type arg1, Annotation[] arg2, MediaType arg3) {
		return JAXBElement.class.equals(type) || jaxbPackage.equals(type.getPackage().getName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public T readFrom(Class<T> arg0, Type arg1, Annotation[] arg2, MediaType arg3, MultivaluedMap<String, String> arg4,
			InputStream entityStream) throws IOException, WebApplicationException {
		try {
			return (T) unmarshaller.unmarshal(entityStream);
		} catch (JAXBException ex) {
			logger.log(Level.WARNING, "Request body validation error.", ex);
			Throwable linked = ex.getLinkedException();
			String validationErrorMessage = "Request body validation error";
			if (linked != null && linked instanceof SAXParseException)
				validationErrorMessage += ": " + linked.getMessage();
			BadRequestException bre = new BadRequestException("Request body validation error");
			String responseBody = responseBodyTemplate.replaceFirst("___TO_BE_REPLACED___", validationErrorMessage);
			Response response = Response.fromResponse(bre.getResponse()).entity(responseBody).build();
			throw new BadRequestException("Request body validation error", response);
		}
	}	
	
}
