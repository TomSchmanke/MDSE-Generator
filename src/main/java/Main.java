import de.arinir.mdsd.metamodell.MDSDMetamodell.UMLClassDiagramm;

import template_data.*;
import util.CreateProjectStructureAsJson;
import util.ProjectInitializer;
import util.TemplateResolver;
import util.XMLConverter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main {

    public static void main(String[] args) throws IOException {
        File file = new File ("./src/main");
        CreateProjectStructureAsJson createProjectStructureAsJson = new CreateProjectStructureAsJson(file);
        ProjectInitializer projectInitializer = new ProjectInitializer();
        String nameOfZip = projectInitializer.loadGeneratedFilesFromSpringInitializer();

        if(nameOfZip == null) {
            return;
        }
        projectInitializer.unzipFile(nameOfZip, nameOfZip.substring(0, nameOfZip.length() - 3));

        List<ControllerDataModel> controllerDataModels = new ArrayList<>();
        controllerDataModels.add(new ControllerDataModel("Antrag", "id"));
        controllerDataModels.add(new ControllerDataModel("Benutzer", "id"));
        controllerDataModels.add(new ControllerDataModel("Fahrzeug", "id"));
        controllerDataModels.add(new ControllerDataModel("Fahrzeug", "id"));

        List<EntityDataModel> entityDataModels = new ArrayList<>();
        List<AttributeDataModel> attributeDataModel = new ArrayList<>();
        attributeDataModel.add(new AttributeDataModel("Name", "String", Multiplicity.SINGLE));
        attributeDataModel.add(new AttributeDataModel("Hersteller", "Hersteller", Multiplicity.SINGLE));
        attributeDataModel.add(new AttributeDataModel("Fahrzeug", "Fahrzeug", Multiplicity.SINGLE));
        entityDataModels.add(new EntityDataModel("Hersteller", "id", attributeDataModel));
        attributeDataModel = new ArrayList<>();
        attributeDataModel.add(new AttributeDataModel("Fahrgestellnummer", "String", Multiplicity.SINGLE));
        attributeDataModel.add(new AttributeDataModel("Kennzeichen", "String", Multiplicity.SINGLE));
        attributeDataModel.add(new AttributeDataModel("KostenJeKilometer", "CurrencyT", Multiplicity.SINGLE));
        attributeDataModel.add(new AttributeDataModel("Typbezeichnung", "String", Multiplicity.SINGLE));
        attributeDataModel.add(new AttributeDataModel("Fahrzeug", "Fahrzeug", Multiplicity.MULTI));
        attributeDataModel.add(new AttributeDataModel("Hersteller", "Hersteller", Multiplicity.SINGLE));
        attributeDataModel.add(new AttributeDataModel("VerwalteteFahrzeuge", "Fahrzeug", Multiplicity.MULTI));
        attributeDataModel.add(new AttributeDataModel("Flottenchef", "Benutzer", Multiplicity.SINGLE));
        attributeDataModel.add(new AttributeDataModel("WunschFahrzeug", "Fahrzeug", Multiplicity.SINGLE));
        attributeDataModel.add(new AttributeDataModel("Antrag", "Antrag", Multiplicity.MULTI));
        entityDataModels.add(new EntityDataModel("Fahrzeug", "id", attributeDataModel));
        attributeDataModel = new ArrayList<>();
        attributeDataModel.add(new AttributeDataModel("IstBearbeitet", "boolean", Multiplicity.SINGLE));
        attributeDataModel.add(new AttributeDataModel("IstFreigegeben", "boolean", Multiplicity.SINGLE));
        attributeDataModel.add(new AttributeDataModel("Kilometer", "float", Multiplicity.SINGLE));
        attributeDataModel.add(new AttributeDataModel("Kosten", "CurrencyT", Multiplicity.SINGLE));
        attributeDataModel.add(new AttributeDataModel("Ziel", "String", Multiplicity.SINGLE));
        attributeDataModel.add(new AttributeDataModel("Antrag", "Antrag", Multiplicity.MULTI));
        attributeDataModel.add(new AttributeDataModel("WunschFahrzeug", "Fahrzeug", Multiplicity.SINGLE));
        attributeDataModel.add(new AttributeDataModel("Antraege", "Antrag", Multiplicity.MULTI));
        attributeDataModel.add(new AttributeDataModel("Antragsteller", "Benutzer", Multiplicity.SINGLE));
        attributeDataModel.add(new AttributeDataModel("BearbeiteteAntraege", "Antrag", Multiplicity.MULTI));
        attributeDataModel.add(new AttributeDataModel("Flottenchef", "Benutzer", Multiplicity.SINGLE));
        entityDataModels.add(new EntityDataModel("Antrag", "id", attributeDataModel));
        attributeDataModel = new ArrayList<>();
        attributeDataModel.add(new AttributeDataModel("Flottenchef", "Benutzer", Multiplicity.SINGLE));
        attributeDataModel.add(new AttributeDataModel("VerwalteteFahrzeuge", "Fahrzeuge", Multiplicity.MULTI));
        attributeDataModel.add(new AttributeDataModel("Antragsteller", "Benutzer", Multiplicity.SINGLE));
        attributeDataModel.add(new AttributeDataModel("Antraege", "Antrag", Multiplicity.MULTI));
        attributeDataModel.add(new AttributeDataModel("Flottenchef", "Benutzer", Multiplicity.SINGLE));
        attributeDataModel.add(new AttributeDataModel("BearbeiteteAntraege", "Antrag", Multiplicity.MULTI));
        entityDataModels.add(new EntityDataModel("Benutzer", "id", attributeDataModel));

        List<AssociationsDataModel> associationsDataModels = new ArrayList<>();
        associationsDataModels.add(new AssociationsDataModel("Fahrzeug", "Fahrzeug", Multiplicity.MULTI, "Hersteller", "Hersteller", Multiplicity.SINGLE));
        associationsDataModels.add(new AssociationsDataModel("Fahrzeug", "VerwalteteFahrzeuge", Multiplicity.MULTI, "Benutzer", "Flottenchef", Multiplicity.SINGLE));
        associationsDataModels.add(new AssociationsDataModel("Antrag", "Antrag", Multiplicity.MULTI, "Fahrzeug", "WunschFahrzeug", Multiplicity.SINGLE));
        associationsDataModels.add(new AssociationsDataModel("Antrag", "Antraege", Multiplicity.MULTI, "Benutzer", "Antragsteller", Multiplicity.SINGLE));
        associationsDataModels.add(new AssociationsDataModel("Antrag", "BearbeiteteAntraege", Multiplicity.MULTI, "Benutzer", "Flottenchef", Multiplicity.SINGLE));

        TemplateResolver templateResolver = new TemplateResolver();
        templateResolver.setControllerDataModels(controllerDataModels);
        templateResolver.setEntityDataModels(entityDataModels);
        templateResolver.setAssociationsDataModels(associationsDataModels);

        templateResolver.createControllerFiles();
        templateResolver.createEntityFiles();
        templateResolver.createRepositoryFiles();

        XMLConverter xmlConverter = new XMLConverter();
        UMLClassDiagramm diagramm = xmlConverter.processXMLUMLFile("/Flottenmanagement.xml");
        //System.out.println(diagramm);
    }
}
