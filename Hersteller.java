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
public Hersteller  {

    @Id
    @GeneratedValue
    private int id

    private String Name;

    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="hersteller")
    @JsonIgnore
    private Fahrzeug Fahrzeug;


    public Hersteller() {

    }

    public Hersteller(String Name, Hersteller Hersteller, Fahrzeug Fahrzeug) {
        this.Name = Name;
		this.Hersteller = Hersteller;
		this.Fahrzeug = Fahrzeug;
    }


    public String getName() {
        return this.Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public Hersteller getHersteller() {
        return this.Hersteller;
    }

    public void setHersteller(Hersteller Hersteller) {
        this.Hersteller = Hersteller;
    }

    public Fahrzeug getFahrzeug() {
        return this.Fahrzeug;
    }

    public void setFahrzeug(Fahrzeug Fahrzeug) {
        this.Fahrzeug = Fahrzeug;
    }

}
