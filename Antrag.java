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
public Antrag  {

    @Id
    @GeneratedValue
    private int id

    private boolean IstBearbeitet;

    private boolean IstFreigegeben;

    private float Kilometer;

    private String Ziel;

    @ManyToOne
    @JoinColumn(name="FK_WunschFahrzeug")
    private Fahrzeug WunschFahrzeug;

    @ManyToOne
    @JoinColumn(name="FK_Antragsteller")
    private Benutzer Antragsteller;

    @ManyToOne
    @JoinColumn(name="FK_Flottenchef")
    private Benutzer Flottenchef;


    public Antrag() {

    }

    public Antrag(boolean IstBearbeitet, boolean IstFreigegeben, float Kilometer, CurrencyT Kosten, String Ziel, Antrag Antrag, Fahrzeug WunschFahrzeug, Antrag Antraege, Benutzer Antragsteller, Antrag BearbeiteteAntraege, Benutzer Flottenchef) {
        this.IstBearbeitet = IstBearbeitet;
		this.IstFreigegeben = IstFreigegeben;
		this.Kilometer = Kilometer;
		this.Kosten = Kosten;
		this.Ziel = Ziel;
		this.Antrag = Antrag;
		this.WunschFahrzeug = WunschFahrzeug;
		this.Antraege = Antraege;
		this.Antragsteller = Antragsteller;
		this.BearbeiteteAntraege = BearbeiteteAntraege;
		this.Flottenchef = Flottenchef;
    }


    public boolean getIstBearbeitet() {
        return this.IstBearbeitet;
    }

    public void setIstBearbeitet(boolean IstBearbeitet) {
        this.IstBearbeitet = IstBearbeitet;
    }

    public boolean getIstFreigegeben() {
        return this.IstFreigegeben;
    }

    public void setIstFreigegeben(boolean IstFreigegeben) {
        this.IstFreigegeben = IstFreigegeben;
    }

    public float getKilometer() {
        return this.Kilometer;
    }

    public void setKilometer(float Kilometer) {
        this.Kilometer = Kilometer;
    }

    public CurrencyT getKosten() {
        return this.Kosten;
    }

    public void setKosten(CurrencyT Kosten) {
        this.Kosten = Kosten;
    }

    public String getZiel() {
        return this.Ziel;
    }

    public void setZiel(String Ziel) {
        this.Ziel = Ziel;
    }

    public List<Antrag> getAntrag() {
        return this.Antrag;
    }

    public void setAntrag(List<Antrag> Antrag) {
        this.Antrag = Antrag;
    }

    public Fahrzeug getWunschFahrzeug() {
        return this.WunschFahrzeug;
    }

    public void setWunschFahrzeug(Fahrzeug WunschFahrzeug) {
        this.WunschFahrzeug = WunschFahrzeug;
    }

    public List<Antrag> getAntraege() {
        return this.Antraege;
    }

    public void setAntraege(List<Antrag> Antraege) {
        this.Antraege = Antraege;
    }

    public Benutzer getAntragsteller() {
        return this.Antragsteller;
    }

    public void setAntragsteller(Benutzer Antragsteller) {
        this.Antragsteller = Antragsteller;
    }

    public List<Antrag> getBearbeiteteAntraege() {
        return this.BearbeiteteAntraege;
    }

    public void setBearbeiteteAntraege(List<Antrag> BearbeiteteAntraege) {
        this.BearbeiteteAntraege = BearbeiteteAntraege;
    }

    public Benutzer getFlottenchef() {
        return this.Flottenchef;
    }

    public void setFlottenchef(Benutzer Flottenchef) {
        this.Flottenchef = Flottenchef;
    }

}
