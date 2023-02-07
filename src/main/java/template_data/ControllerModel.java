package template_data;

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

    public String getURLPath() {
        return entityName.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase() + "s";
    }
}
