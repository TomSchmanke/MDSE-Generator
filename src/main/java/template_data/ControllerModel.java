package template_data;

import util.Plurals;

/**
 * Record that holds information for REST controller creation.
 *
 * @param entityName name of entity
 * @param identificationVariable variable used for identification
 *
 * @author Tom Schmanke
 * @version 1.0 initial creation
 */
public record ControllerModel(String entityName, String identificationVariable) {

    public String getEntityName() {
        return entityName;
    }

    public String getIdentificationVariable() {
        return identificationVariable;
    }

    public String getIdentificationVariableCamelCase() {
        return identificationVariable.substring(0, 1).toLowerCase() + identificationVariable.substring(1);
    }

    public String getControllerName() {
        return entityName.substring(0, 1).toUpperCase() + entityName.substring(1) + "Controller";
    }

    public String getRepositoryName() {
        return entityName.substring(0, 1).toUpperCase() + entityName.substring(1) + "Repository";
    }

    public String getRepositoryNameCamelCase() {
        return entityName.substring(0, 1).toLowerCase() + entityName.substring(1) + "Repository";
    }

    public String getURLPath() {
        return Plurals.getPlural(entityName.replaceAll("([a-z])([A-Z]+)", "$1_$2")).toLowerCase();
    }
}
