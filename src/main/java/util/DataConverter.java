package util;

import de.arinir.mdsd.metamodell.MDSDMetamodell.*;
import de.arinir.mdsd.metamodell.MDSDMetamodell.Class;
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

    private static final String IDENTIFICATION_VAR = "Id";
    private final UMLClassDiagramm mdsdDiagram;

    public DataConverter(UMLClassDiagramm mdsdDiagram) {
        this.mdsdDiagram = mdsdDiagram;
    }

    public DataModel convertMDSDDiagramToDataModel() {
        DataModel dataModel = new DataModel();

        dataModel.setControllerDataModels(convertController());
        dataModel.setEntityDataModels(convertEntities());
        dataModel.setAssociationsDataModels(convertAssociations());
        dataModel.setRepositoryDataModels(convertRepositories());

        return dataModel;
    }

    private List<ControllerModel> convertController() {
        List<ControllerModel> controllerModels = new ArrayList<>();
        for (Class c : mdsdDiagram.getClasses()) {
            var model = new ControllerModel(c.getName(), IDENTIFICATION_VAR);
            controllerModels.add(model);
        }
        return controllerModels;
    }

    private List<EntityModel> convertEntities() {
        List<EntityModel> entityModels = new ArrayList<>();
        for (Class cl : mdsdDiagram.getClasses()) {
            var classAttributes = convertAttributes(cl);
            var model = new EntityModel(cl.getName(), IDENTIFICATION_VAR, classAttributes);
            entityModels.add(model);
        }
        return entityModels;
    }

    private List<AttributeModel> convertAttributes(Class cl) {
        List<AttributeModel> attributeModels = new ArrayList<>();
        for (Attribute att : cl.getAttributes()) {
            var model = new AttributeModel(att.getName(), normalizeType(att.getType()), att.getVisibility());
            attributeModels.add(model);
        }
        return attributeModels;
    }

    private DataType normalizeType(DataType oldType) {
        var oldName = oldType.getName();
        return switch (oldName) {
            case "string", "str" -> new DataType("String");
            case "boolean", "bool" -> new DataType("boolean");
            case "int", "num" -> new DataType("int");
            default -> oldType;
        };
    }

    private List<AssociationsModel> convertAssociations() {
        List<AssociationsModel> associationsModels = new ArrayList<>();
        for (Assoziation a : mdsdDiagram.getAssoziations()) {
            var from = a.getFrom();
            var to = a.getTo();
            var model = new AssociationsModel(from.getReference().getName(), from.getRoleName(), from.getMultiplicity(),
                    to.getReference().getName(), to.getRoleName(), to.getMultiplicity());
            associationsModels.add(model);
        }
        return associationsModels;
    }

    private List<RepositoryModel> convertRepositories() {
        List<RepositoryModel> repositoryModels = new ArrayList<>();
        for (Class c : mdsdDiagram.getClasses()) {
            var name = c.getName();
            var model = new RepositoryModel(name);
            repositoryModels.add(model);
        }
        return repositoryModels;
    }
}
