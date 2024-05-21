package fr.univrouen.cv24.controllers;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fr.univrouen.cv24.model.CVList;
import fr.univrouen.cv24.model.TestCV;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

}