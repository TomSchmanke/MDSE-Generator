package template_data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    public String getAttributesForConstructorHeader() {
        return attributeModels.stream().map(AttributeModel::getTypeAndName).collect(Collectors.joining(", "));
    }

    public String getAttributesForConstructorBody() {
        return attributeModels.stream().map(AttributeModel::getConstructorAssignment).collect(Collectors.joining("\n\t\t"));
    }

    public List<AttributeModel> getSimpleAttributes() {
        return attributeModels.stream().filter(attributeModel -> Arrays.asList("int", "float", "String", "boolean").contains(attributeModel.attributeType())).toList();
    }
}
