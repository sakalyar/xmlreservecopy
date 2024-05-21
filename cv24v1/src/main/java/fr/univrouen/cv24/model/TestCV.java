package fr.univrouen.cv24.model;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name = "cv")
@XmlAccessorType(XmlAccessType.NONE)
public class TestCV implements Serializable {
    private static final long serialVersionUID = 2024L;
    private static int compteur = 1;

    @XmlAttribute
    private Integer id;

    @XmlElement
    private String genre;

    @XmlElement
    private String prenom;

    @XmlElement
    private String nom;

    @XmlElement
    private String objectif;

    @XmlElement
    private String diplome;

    @XmlElement
    private String date;

    @XmlElement
    private String mel;

    public TestCV(String genre, String prenom, String nom, String objectif, String diplome, String date, String mel) {
        super();
        this.id = compteur++;
        this.genre = genre;
        this.prenom = prenom;
        this.nom = nom;
        this.objectif = objectif;
        this.diplome = diplome;
        this.date = date;
        this.mel = mel;
    }

    public TestCV() {}

    @Override
    public String toString() {
        return "CV (" + id + ") [" + genre + ", " + prenom + " " + nom + ", Objectif=" + objectif + ", Diplome=" + diplome + ", Date=" + date + ", mel=" + mel + "]";
    }

}
