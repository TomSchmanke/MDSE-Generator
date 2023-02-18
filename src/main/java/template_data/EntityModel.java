package template_data;

import java.util.List;

public record EntityModel(String entityName, String identificationVariable, List<AttributeModel> attributeModels) {

    public String getEntityName() {
        return entityName;
    }

    public String getIdentificationVariable() {
        return identificationVariable;
    }

    public String getIdentificationVariableCamelCase() {
        return identificationVariable.substring(0, 1).toLowerCase() + identificationVariable.substring(1);
    }

    public List<AttributeModel> getAttributeDataModels() {
        return attributeModels;
    }

}
