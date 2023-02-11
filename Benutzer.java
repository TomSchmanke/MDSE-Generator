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
public Benutzer  {

    @Id
    @GeneratedValue
    private int id

    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="flottenchef")
    @JsonIgnore
    private Fahrzeug VerwalteteFahrzeuge;

    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="antragsteller")
    @JsonIgnore
    private Antrag Antraege;

    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="flottenchef")
    @JsonIgnore
    private Antrag BearbeiteteAntraege;


    public Benutzer() {

    }

    public Benutzer(Benutzer Flottenchef, Fahrzeuge VerwalteteFahrzeuge, Benutzer Antragsteller, Antrag Antraege, Benutzer Flottenchef, Antrag BearbeiteteAntraege) {
        this.Flottenchef = Flottenchef;
		this.VerwalteteFahrzeuge = VerwalteteFahrzeuge;
		this.Antragsteller = Antragsteller;
		this.Antraege = Antraege;
		this.Flottenchef = Flottenchef;
		this.BearbeiteteAntraege = BearbeiteteAntraege;
    }


    public Benutzer getFlottenchef() {
        return this.Flottenchef;
    }

    public void setFlottenchef(Benutzer Flottenchef) {
        this.Flottenchef = Flottenchef;
    }

    public List<Fahrzeuge> getVerwalteteFahrzeuge() {
        return this.VerwalteteFahrzeuge;
    }

    public void setVerwalteteFahrzeuge(List<Fahrzeuge> VerwalteteFahrzeuge) {
        this.VerwalteteFahrzeuge = VerwalteteFahrzeuge;
    }

    public Benutzer getAntragsteller() {
        return this.Antragsteller;
    }

    public void setAntragsteller(Benutzer Antragsteller) {
        this.Antragsteller = Antragsteller;
    }

    public List<Antrag> getAntraege() {
        return this.Antraege;
    }

    public void setAntraege(List<Antrag> Antraege) {
        this.Antraege = Antraege;
    }

    public Benutzer getFlottenchef() {
        return this.Flottenchef;
    }

    public void setFlottenchef(Benutzer Flottenchef) {
        this.Flottenchef = Flottenchef;
    }

    public List<Antrag> getBearbeiteteAntraege() {
        return this.BearbeiteteAntraege;
    }

    public void setBearbeiteteAntraege(List<Antrag> BearbeiteteAntraege) {
        this.BearbeiteteAntraege = BearbeiteteAntraege;
    }

}
