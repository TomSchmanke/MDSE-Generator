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
 * @version 1.0 Initial implementation
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

    /**
     * Create {@link ControllerModel}s from classes of the UML diagram.
     *
     * @return list of {@link ControllerModel}s
     */
    private List<ControllerModel> convertController() {
        List<ControllerModel> controllerModels = new ArrayList<>();
        for (Class c : mdsdDiagram.getClasses()) {
            var model = new ControllerModel(c.getName(), IDENTIFICATION_VAR);
            controllerModels.add(model);
        }
        return controllerModels;
    }

    /**
     * Create {@link EntityModel}s from classes of the UML diagram.
     *
     * @return list of {@link EntityModel}s
     */
    private List<EntityModel> convertEntities() {
        List<EntityModel> entityModels = new ArrayList<>();
        for (Class cl : mdsdDiagram.getClasses()) {
            var classAttributes = convertAttributes(cl);
            var model = new EntityModel(cl.getName(), IDENTIFICATION_VAR, classAttributes);
            entityModels.add(model);
        }
        return entityModels;
    }

    /**
     * Create {@link AttributeModel}s from a class of the UML diagram.
     *
     * @param cl UML class with attributes
     * @return list of {@link AttributeModel}s
     */
    private List<AttributeModel> convertAttributes(Class cl) {
        List<AttributeModel> attributeModels = new ArrayList<>();
        for (Attribute att : cl.getAttributes()) {
            var model = new AttributeModel(att.getName(), normalizeType(att.getType()), att.getVisibility());
            attributeModels.add(model);
        }
        return attributeModels;
    }

    /**
     * Normalize data type. It is possible that in the UML class diagram abbreviations
     * such as str for String or bool for boolean is used. To get the right Java data type
     * a normalization is needed.
     *
     * @param oldType data type from UML diagram
     * @return normalized data type
     */
    private DataType normalizeType(DataType oldType) {
        var oldName = oldType.getName();
        return switch (oldName) {
            case "string", "str" -> new DataType("String");
            case "boolean", "bool" -> new DataType("boolean");
            case "int", "num" -> new DataType("int");
            default -> oldType;
        };
    }

    /**
     * Create {@link AssociationsModel}s from associations in UML diagram.
     *
     * @return list of {@link AssociationsModel}s
     */
    private List<AssociationsModel> convertAssociations() {
        List<AssociationsModel> associationsModels = new ArrayList<>();
        for (Assoziation a : mdsdDiagram.getAssoziations()) {
            var from = a.getFrom();
            var to = a.getTo();
            var model = new AssociationsModel(from.getReference().getName(), from.getRoleName(),
                    to.getReference().getName(), to.getRoleName());
            associationsModels.add(model);
        }
        return associationsModels;
    }

    /**
     * Create {@link RepositoryModel}s from classes of the UML diagram.
     *
     * @return list of {@link RepositoryModel}
     */
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
