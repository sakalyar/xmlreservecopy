package fr.univrouen.cv24.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.xml.sax.InputSource;
import org.w3c.dom.Document;
import java.io.FileOutputStream;
import java.io.FileWriter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.file.Files;
import fr.univrouen.cv24.model.CVList;
import fr.univrouen.cv24.model.TestCV;
import jakarta.annotation.Resource;
import jakarta.xml.bind.Element;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
@RestController
public class GetController {
	@GetMapping("/resume")
	public String getListCVinXML() {
		return "Envoi de la liste des CV";
	}
	@GetMapping("/cvid")
	public String getCVinXML(
	@RequestParam(value = "texte") String texte) {
		return ("Détail du CV n°" + texte);
	}
	
	@GetMapping("/test")
	public String testMethod(
	    @RequestParam(value = "id") int id,
	    @RequestParam(value = "titre") String titre) {
	    return "Test :\n" +
	           "id = " + id + "\n" +
	           "titre = " + titre;
	}
	
	@RequestMapping(value="/testxml", produces=MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody TestCV getXML() {
		TestCV cv = new TestCV("Femme","Margaret","HAMILTON", "ingenieure", "mathematiques", "1969/07/21",
		"Appollo11@nasa.us");
		return cv;
	}
	
	@RequestMapping(value = "/cv24/resume/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public CVList getCVListXML() {
        List<TestCV> cvs = new ArrayList<>();
        try {
            File folder = new ClassPathResource("xml").getFile();
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".xml"));
            if (files != null) {
                JAXBContext context = JAXBContext.newInstance(TestCV.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                for (File file : files) {
                    TestCV cv = (TestCV) unmarshaller.unmarshal(file);
                    cvs.add(cv);
                }
            }
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }
        return new CVList(cvs);
    }
	
	@GetMapping(value = "/cv24/resume", produces = "text/html")
    @ResponseBody
    public ResponseEntity<String> getCVListHTML() {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html><body><h1>List of CVs</h1><ul>");

        try {
            File folder = new ClassPathResource("xml").getFile();
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".xml"));
            if (files != null) {
                for (File file : files) {
                    String xmlContent = new String(Files.readAllBytes(file.toPath()));
                    htmlBuilder.append("<li>").append(parseCVFromXML(xmlContent)).append("</li>");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        htmlBuilder.append("</ul></body></html>");
        return ResponseEntity.ok(htmlBuilder.toString());
    }

	private final String directoryPath = "src/main/resources/xml";

    @PostMapping("/cv24/insert")
    public ResponseEntity<String> insertCV(@RequestBody String xmlData) {
        try {
            // Get the directory path for storing uploaded files
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Generate a new file name
            String fileName = "cv_" + System.currentTimeMillis() + ".xml";

            // Write the XML content to a new file in the directory
            File newFile = new File(directory, fileName);
            try (FileWriter fileWriter = new FileWriter(newFile)) {
                fileWriter.write(xmlData);
            }

            return ResponseEntity.ok("CV data saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving CV data.");
        }
    }

    private void validateXML(String xmlPayload) throws SAXException, IOException, ParserConfigurationException {
        // Load XSD schema
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(getClass().getResource("/xsd/cv24.xsd"));

        // Create XML document from payload
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document document = docBuilder.parse(new InputSource(new StringReader(xmlPayload)));

        // Validate XML against XSD schema
        schema.newValidator().validate(new StreamSource(new StringReader(xmlPayload)));
    }

    // Dummy implementation to generate unique ID (replace with actual logic)
    private int generateUniqueId() {
        return (int) (Math.random() * 1000); // Generate a random number for demo purposes
    }

    private String parseCVFromXML(String xmlContent) {
        String genre = getValueFromTag(xmlContent, "<genre>");
        String prenom = getValueFromTag(xmlContent, "<prenom>");
        String nom = getValueFromTag(xmlContent, "<nom>");
        String objectif = getValueFromTag(xmlContent, "<objectif>");
        String diplome = getValueFromTag(xmlContent, "<diplome>");
        String date = getValueFromTag(xmlContent, "<date>");
        String mel = getValueFromTag(xmlContent, "<mel>");

        return "<cvs><cv>" +
                "<genre>" + genre + "</genre>\n" +
                "<prenom>" + prenom + "</prenom>\n" +
                "<nom>" + nom + "</nom>\n" +
                "<objectif>" + objectif + "</objectif\n>" +
                "<diplome>" + diplome + "</diplome>\n" +
                "<date>" + date + "</date>\n" +
                "<mel>" + mel + "</mel>\n" +
                "</cv></cvs>\n";
    }

    private String getValueFromTag(String xmlContent, String tag) {
        int startIndex = xmlContent.indexOf(tag) + tag.length();
        int endIndex = xmlContent.indexOf("</", startIndex);
        return xmlContent.substring(startIndex, endIndex);
    }

}