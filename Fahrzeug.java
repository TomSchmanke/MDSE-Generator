package TODO;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public Fahrzeug  {

    @Id
    @GeneratedValue
    private int id

    private String Fahrgestellnummer;

    private String Kennzeichen;

    private String Typbezeichnung;

    @ManyToOne
    @JoinColumn(name="FK_Hersteller")
    private Hersteller Hersteller;

    @ManyToOne
    @JoinColumn(name="FK_Flottenchef")
    private Benutzer Flottenchef;

    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="wunschFahrzeug")
    @JsonIgnore
    private Antrag Antrag;


    public Fahrzeug() {

    }

    public Fahrzeug(String Fahrgestellnummer, String Kennzeichen, CurrencyT KostenJeKilometer, String Typbezeichnung, Fahrzeug Fahrzeug, Hersteller Hersteller, Fahrzeug VerwalteteFahrzeuge, Benutzer Flottenchef, Fahrzeug WunschFahrzeug, Antrag Antrag) {
        this.Fahrgestellnummer = Fahrgestellnummer;
		this.Kennzeichen = Kennzeichen;
		this.KostenJeKilometer = KostenJeKilometer;
		this.Typbezeichnung = Typbezeichnung;
		this.Fahrzeug = Fahrzeug;
		this.Hersteller = Hersteller;
		this.VerwalteteFahrzeuge = VerwalteteFahrzeuge;
		this.Flottenchef = Flottenchef;
		this.WunschFahrzeug = WunschFahrzeug;
		this.Antrag = Antrag;
    }


    public String getFahrgestellnummer() {
        return this.Fahrgestellnummer;
    }

    public void setFahrgestellnummer(String Fahrgestellnummer) {
        this.Fahrgestellnummer = Fahrgestellnummer;
    }

    public String getKennzeichen() {
        return this.Kennzeichen;
    }

    public void setKennzeichen(String Kennzeichen) {
        this.Kennzeichen = Kennzeichen;
    }

    public CurrencyT getKostenJeKilometer() {
        return this.KostenJeKilometer;
    }

    public void setKostenJeKilometer(CurrencyT KostenJeKilometer) {
        this.KostenJeKilometer = KostenJeKilometer;
    }

    public String getTypbezeichnung() {
        return this.Typbezeichnung;
    }

    public void setTypbezeichnung(String Typbezeichnung) {
        this.Typbezeichnung = Typbezeichnung;
    }

    public List<Fahrzeug> getFahrzeug() {
        return this.Fahrzeug;
    }

    public void setFahrzeug(List<Fahrzeug> Fahrzeug) {
        this.Fahrzeug = Fahrzeug;
    }

    public Hersteller getHersteller() {
        return this.Hersteller;
    }

    public void setHersteller(Hersteller Hersteller) {
        this.Hersteller = Hersteller;
    }

    public List<Fahrzeug> getVerwalteteFahrzeuge() {
        return this.VerwalteteFahrzeuge;
    }

    public void setVerwalteteFahrzeuge(List<Fahrzeug> VerwalteteFahrzeuge) {
        this.VerwalteteFahrzeuge = VerwalteteFahrzeuge;
    }

    public Benutzer getFlottenchef() {
        return this.Flottenchef;
    }

    public void setFlottenchef(Benutzer Flottenchef) {
        this.Flottenchef = Flottenchef;
    }

    public Fahrzeug getWunschFahrzeug() {
        return this.WunschFahrzeug;
    }

    public void setWunschFahrzeug(Fahrzeug WunschFahrzeug) {
        this.WunschFahrzeug = WunschFahrzeug;
    }

    public List<Antrag> getAntrag() {
        return this.Antrag;
    }

    public void setAntrag(List<Antrag> Antrag) {
        this.Antrag = Antrag;
    }

}
