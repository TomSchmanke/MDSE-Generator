package util;

import de.arinir.mdsd.metamodell.MDSDMetamodell.Assoziation;
import de.arinir.mdsd.metamodell.MDSDMetamodell.Attribute;
import de.arinir.mdsd.metamodell.MDSDMetamodell.Class;
import de.arinir.mdsd.metamodell.MDSDMetamodell.UMLClassDiagramm;
import template_data.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to convert given MDSDMetamodell data to a more suitable datamodel for
 * our generator.
 *
 * @author Laura Schmidt
 */
public class DataConverter {

    private static final String IDENTIFICATION_VAR = "id";
    private final UMLClassDiagramm mdsdDiagram;

    public DataConverter(UMLClassDiagramm mdsdDiagram) {
        this.mdsdDiagram = mdsdDiagram;
    }

    public DataModel convertMDSDDiagramToDataModel() {
        DataModel dataModel = new DataModel();

        dataModel.setControllerDataModels(convertController());
        dataModel.setEntityDataModels(convertEntities());
        dataModel.setAssociationsDataModels(convertAssociations());

        return dataModel;
    }

    private List<ControllerDataModel> convertController() {
        List<ControllerDataModel> controllerModels = new ArrayList<>();
        for (Class c : mdsdDiagram.getClasses()) {
            var model = new ControllerDataModel(c.getName(), IDENTIFICATION_VAR);
            controllerModels.add(model);
        }
        return controllerModels;
    }

    private List<EntityDataModel> convertEntities() {
        List<EntityDataModel> entityModels = new ArrayList<>();
        for (Class cl : mdsdDiagram.getClasses()) {
            var classAttributes = convertAttributes(cl);
            var model = new EntityDataModel(cl.getName(), IDENTIFICATION_VAR, classAttributes);
            entityModels.add(model);
        }
        return entityModels;
    }

    private List<AttributeDataModel> convertAttributes(Class cl) {
        List<AttributeDataModel> attributeModels = new ArrayList<>();
        for (Attribute att : cl.getAttributes()) {
            var model = new AttributeDataModel(att.getName(), att.getType(), att.getVisibility());
            attributeModels.add(model);
        }
        return attributeModels;
    }

    private List<AssociationsDataModel> convertAssociations() {
        List<AssociationsDataModel> associationsModels = new ArrayList<>();
        for (Assoziation a : mdsdDiagram.getAssoziations()) {
            var from = a.getFrom();
            var to = a.getTo();
            var model = new AssociationsDataModel(from.getName(), from.getReference().getName(), from.getMultiplicity(),
                    to.getName(), to.getReference().getName(), to.getMultiplicity());
            associationsModels.add(model);
        }
        return associationsModels;
    }
}
